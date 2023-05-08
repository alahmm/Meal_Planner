package org.example;

import java.util.*;
import java.sql.*;

public class Main {

    public static String MealPlanner(String category, String name, List<String> Ingredients) {

        String meal = String.format("%nCategory: %s%nName: %s%nIngredients:%n", category, name);
        String ingre = "";
        for (String ingredient : Ingredients
        ) {
            ingre += String.format("%s%n", ingredient);
        }
        return meal + ingre;
    }
    public static String MealPlanner(String name, List<String> Ingredients) {

        String meal = String.format("%nName: %s%nIngredients:%n", name);
        String ingre = "";
        for (String ingredient : Ingredients
        ) {
            ingre += String.format("%s%n", ingredient);
        }
        return meal + ingre;
    }
    public static void main(String[] args) throws SQLException {
        Map<Integer, List<String>> map = new HashMap<>();

        String Complete_meal = "";
        int meal_id = 0;
        //setting the connection with the database
        String DB_URL = "jdbc:postgresql:meals_db";
        String USER = "postgres";
        String PASS = "1111";


        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        connection.setAutoCommit(true);
        //creating tables
        Statement statement = connection.createStatement();

        //statement.executeUpdate("drop table if exists meals");
        statement.executeUpdate("create table if not exists meals (" +
                "category varchar," +
                "meal varchar," +
                "meal_id INT" +
                ")");
        //statement.executeUpdate("drop table if exists ingredients");
        statement.executeUpdate("create table if not exists ingredients (" +
                "ingredient varchar(1024)," +
                "ingredient_id INT," +
                "meal_id INT" +
                ")");
        ResultSet rs = statement.executeQuery("select * from meals");
        while (rs.next()) {
            List<String> list = new ArrayList<>();
            String category = rs.getString("category");
            list.add(category);
            list.add(rs.getString("meal"));
            map.put(rs.getInt("meal_id"), list);
        }
        rs.close();
        for (Map.Entry<Integer, List<String>> meal :
                map.entrySet()) {
            List<String> ingredients = new ArrayList<>();
            ResultSet rs_Ingredient = statement.executeQuery("select * from ingredients");
            while (rs_Ingredient.next()) {
                if (meal.getKey() == rs_Ingredient.getInt("meal_id")) {
                    ingredients.add(rs_Ingredient.getString("ingredient"));
                }
            }
            Complete_meal += MealPlanner(meal.getValue().get(0), meal.getValue().get(1), ingredients);
        }
        if (!Complete_meal.equals("")) {
            meal_id = map.size() + 1;
        }

        Scanner scanner = new Scanner(System.in);
        String regex = "[a-zA-Z ]+";

        while(true) {
            System.out.print("What would you like to do (add, show, exit)?");
            scanner.useDelimiter("\\n");
            String toDo = scanner.next();
            while (!toDo.equals("add") && !toDo.equals("show") && !toDo.equals("exit")) {
                break;
            }
            switch (toDo) {
                case "add" -> {
                    System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
                    String meal = scanner.next();
                    while (!meal.equals("breakfast") && !meal.equals("lunch") && !meal.equals("dinner")) {
                        System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                        meal = scanner.next();
                    }
                    System.out.println("Input the meal's name:");
                    String nameOfMale = scanner.next();
                    while (!nameOfMale.matches(regex)) {
                        System.out.println("Wrong format. Use letters only!");
                        nameOfMale = scanner.next();
                    }
                    System.out.println("Input the ingredients:");
                    List<String> ingredients = Arrays.stream(scanner.next().split(",")).toList();
                    for (int i = 0; i < ingredients.size(); i++) {
                        if  (!ingredients.get(i).matches(regex) || ingredients.get(i).equals("") || ingredients.get(i).equals(" ")) {
                            System.out.println("Wrong format. Use letters only!");
                            ingredients = Arrays.stream(scanner.next().split(",")).toList();
                            i = 0;
                        }
                    }
                    System.out.println("The meal has been added!");

                    //saving the mael in tables
                    String sql = String.format("insert into meals (category, meal, meal_id) values ('%s', '%s', %d)", meal, nameOfMale, meal_id);
                    statement.executeUpdate(sql);
                    for (int i = 0; i < ingredients.size(); i++) {
                        sql = String.format("insert into ingredients  values ('%s', %d, %d)", ingredients.get(i), i, meal_id);
                        statement.executeUpdate(sql);
                    }
                    meal_id ++;

                    Complete_meal += MealPlanner(meal, nameOfMale, ingredients);

                }
                case "show" ->  {
                    System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
                    String whichCategory = scanner.next();

                    while (!whichCategory.equals("breakfast") && !whichCategory.equals("lunch") && !whichCategory.equals("dinner")) {
                        System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                        whichCategory = scanner.next();
                    }
                    Complete_meal = "";
                    map = new HashMap<>();
                    String sql = String.format("select * from meals where category = '%s'", whichCategory);
                    rs = statement.executeQuery(sql);
                    while (rs.next()) {
                        List<String> list = new ArrayList<>();
                        String category = rs.getString("category");
                        list.add(category);
                        list.add(rs.getString("meal"));
                        map.put(rs.getInt("meal_id"), list);
                    }
                    rs.close();
                    for (Map.Entry<Integer, List<String>> meal :
                            map.entrySet()) {
                        List<String> ingredients = new ArrayList<>();
                        ResultSet rs_Ingredient = statement.executeQuery("select * from ingredients");
                        while (rs_Ingredient.next()) {
                            if (meal.getKey() == rs_Ingredient.getInt("meal_id")) {
                                ingredients.add(rs_Ingredient.getString("ingredient"));
                            }
                        }
                        Complete_meal += MealPlanner(meal.getValue().get(1), ingredients);
                    }
                    if (Complete_meal.equals("")) {
                        System.out.println("No meals found.");
                    }else {
                        System.out.printf("Category: %s", whichCategory);
                        System.out.println(Complete_meal);
                    }
                }
                case "exit" -> {
                    System.out.println("Bye!");
                    statement.close();
                    connection.close();
                    return;
                }
            }
        }
    }
}
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

    public static String Planner(String day, ResultSet rs, Scanner scanner, String category) throws SQLException {
        List<String> listOfBreakfast = new ArrayList<>();
        while (rs.next()) {
            System.out.println(rs.getString("meal"));
            listOfBreakfast.add(rs.getString("meal"));
        }
        System.out.printf("choose the %s for %s from the list above:%n", category, day);
        String theChosenBreakfast = scanner.next();
        while (!listOfBreakfast.contains(theChosenBreakfast)) {
            System.out.println("This meal doesn't exist. Choose a meal from the list above.");
            theChosenBreakfast = scanner.next();
        }
        return theChosenBreakfast;
    }

    public static List<String> PlannerForTheDay(String day, ResultSet rs, Scanner scanner, Statement statement) throws SQLException {
        List<String> listOfTheDay = new ArrayList<>();
        System.out.println(day);
        rs = statement.executeQuery("select meal from meals where category = 'breakfast' order by meals");
        String WhatToEat = String.format("breakfast: %s", Planner(day, rs, scanner, "breakfast"));
        listOfTheDay.add(WhatToEat);
        rs.close();
        rs = statement.executeQuery("select meal from meals where category = 'lunch'");
        WhatToEat = String.format("lunch: %s", Planner(day, rs, scanner, "lunch"));
        listOfTheDay.add(WhatToEat);
        rs.close();
        rs = statement.executeQuery("select meal from meals where category = 'dinner'");
        WhatToEat = String.format("dinner: %s", Planner(day, rs, scanner, "dinner"));
        listOfTheDay.add(WhatToEat);
        rs.close();
        System.out.printf("Yeah! We planned the meals for %s%n", day);
        System.out.println();

        return listOfTheDay;
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

        //create table named plan
        statement.executeUpdate("drop table if exists plan");
        statement.executeUpdate("create table if not exists plan(" +
                "plan_text varchar," +
                "plan_id INT" +
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

        while (true) {
            System.out.print("What would you like to do (add, show, plan, exit)?");
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
                        if (!ingredients.get(i).matches(regex) || ingredients.get(i).equals("") || ingredients.get(i).equals(" ")) {
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
                    meal_id++;

                    Complete_meal += MealPlanner(meal, nameOfMale, ingredients);

                }
                case "show" -> {
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
                    } else {
                        System.out.printf("Category: %s", whichCategory);
                        System.out.println(Complete_meal);
                    }
                }
                case "plan" -> {
                    Map<String, List<String>> planOfTheWeek = new LinkedHashMap<>();
                    String[] theDaysOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                    for (String day : theDaysOfTheWeek
                    ) {
                        planOfTheWeek.put(day, PlannerForTheDay(day, rs, scanner, statement));
                    }
                    String planString = "";
                    for (Map.Entry<String, List<String>> meal :
                            planOfTheWeek.entrySet()) {
                        System.out.println(meal.getKey());
                        planString += String.format(meal.getKey() + "%n");
                        for (String variable : meal.getValue()
                        ) {
                            System.out.println(variable);
                            planString += String.format(variable + "%n");
                        }
                        System.out.println();
                        planString += String.format("%n");

                    }
                    String sql = String.format("insert into plan values ('%s', %d)", planString, 1);
                    statement.executeUpdate(sql);

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
package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        String chosenBreakfast = Planner(day, rs, scanner, "breakfast");
        String WhatToEat = String.format("breakfast: %s", chosenBreakfast);
        listOfTheDay.add(WhatToEat);
        rs.close();
        rs = statement.executeQuery("select meal from meals where category = 'lunch'");
        String chosenLunch = Planner(day, rs, scanner, "lunch");
        WhatToEat = String.format("lunch: %s", chosenLunch);
        listOfTheDay.add(WhatToEat);
        rs.close();
        rs = statement.executeQuery("select meal from meals where category = 'dinner'");
        String chosenDinner = Planner(day, rs, scanner, "dinner");
        WhatToEat = String.format("dinner: %s", chosenDinner);
        listOfTheDay.add(WhatToEat);
        rs.close();
        
        
        String sql = String.format("insert into plan_for_each_meal(breakfast, lunch, dinner) values ('%s', '%s', '%s')", chosenBreakfast, chosenLunch, chosenDinner);
        statement.executeUpdate(sql);
        System.out.printf("Yeah! We planned the meals for %s%n", day);
        System.out.println();

        return listOfTheDay;
    }

    public static void main(String[] args) throws SQLException, FileNotFoundException {
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
        //statement.executeUpdate("drop table if exists plan");
        statement.executeUpdate("create table if not exists plan(" +
                "plan_text varchar," +
                "plan_id INT" +
                ")");
        statement.executeUpdate("drop table if exists plan_for_each_meal");
        statement.executeUpdate("create table if not exists plan_for_each_meal(" +
                "breakfast varchar," +
                "lunch varchar," +
                "dinner varchar" +
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
            System.out.print("What would you like to do (add, show, plan, save, exit)?");
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
                case "save" -> {
                    List<String> listOfMeals = new ArrayList<>();
                    List<Integer> listOfMealsID = new ArrayList<>();
                    List<String> listOfIngredients = new ArrayList<>();
                    ResultSet resultSet = statement.executeQuery("select * from plan");
                    String plan = "";
                    while (resultSet.next()) {
                        plan = resultSet.getString("plan_text");

                    }
                    resultSet.close();
                    if (plan.equals("")) {
                        System.out.println("Unable to save. Plan your meals first.");
                        String whatToDo = scanner.next();
                        if (whatToDo.equals("exit")) {
                            System.out.println("Bye!");
                            statement.close();
                            connection.close();
                            return;
                        }
                    } else {
                        System.out.println("Input a filename:");

                        rs = statement.executeQuery("select * from plan_for_each_meal");
                        while(rs.next()) {
                            listOfMeals.add(rs.getString("breakfast"));
                            listOfMeals.add(rs.getString("lunch"));
                            listOfMeals.add(rs.getString("dinner"));
                        }

                        rs.close();

                        rs = statement.executeQuery("select * from meals");
                        String newMeal = "";

                        while (rs.next()) {
                            newMeal = rs.getString("meal");
                            for (String meal : listOfMeals
                            ) {
                                if (Objects.equals(meal, newMeal)) {
                                    listOfMealsID.add(rs.getInt("meal_id"));
                                    //break;
                                }
                            }
                        }
                    }
                    rs.close();
                    rs = statement.executeQuery("select * from ingredients");
                    while (rs.next()) {
                        int newVariable = rs.getInt("meal_id");
                        for (int id : listOfMealsID
                        ) {
                            if (id == newVariable) {
                                listOfIngredients.add(rs.getString("ingredient"));
                                //break;
                            }
                        }
                    }
                    //TO DO: eleminate duplication from the list of Ingredients than write how many time it is duplicated
                    Map<String, Integer> mapOfIngredients = new TreeMap<>();
                    for (String key : listOfIngredients
                    ) {
                        if (mapOfIngredients.containsKey(key)) {
                            int value = mapOfIngredients.get(key);
                            mapOfIngredients.replace(key, value, value + 1);
                        } else {
                            mapOfIngredients.put(key, 1);
                        }
                    }

                    //saving the ingredients in a file works
                    String fileName = scanner.next();
                    //File file = new File(fileName);
                    try (PrintWriter out = new PrintWriter(fileName)) {
                        for (Map.Entry<String, Integer> ingredient :
                                mapOfIngredients.entrySet()) {
                            if(ingredient.getValue() != 1) {
                                out.println(ingredient.getKey() + " x" + ingredient.getValue());
                            } else {
                                out.println(ingredient.getKey());
                            }
                        }
                    }
                    System.out.println("Saved!");

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
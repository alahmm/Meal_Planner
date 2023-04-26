package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


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
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String regex = "[a-zA-Z ]+";
        String Complete_meal = "";
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
                    Complete_meal += MealPlanner(meal, nameOfMale, ingredients);

                }
                case "show" ->  {
                    if (Complete_meal.equals("")) {
                        System.out.println("No meals saved. Add a meal first.");
                    } else {
                        System.out.println(Complete_meal);
                    }
                }
                case "exit" -> {
                    System.out.println("Bye!");
                    return;
                }
            }

        }

    }
}
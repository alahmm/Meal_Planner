package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void MealPlanner(String category, String name, List<String> Ingredients) {
        System.out.printf("%nCategory: %s%n", category);
        System.out.printf("Name: %s%n", name);
        System.out.println("Ingredients:");
        Ingredients.forEach(System.out::println);
        System.out.println("The meal has been added!");
    }
    public static void main(String[] args) {

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        Scanner scanner = new Scanner(System.in);
        String meal = scanner.next();
        System.out.println("Input the meal's name:");
        String nameOfMale = scanner.next();
        System.out.println("Input the ingredients:");
        List<String> ingredients = Arrays.stream(scanner.nextLine().trim().split(",")).toList();
        MealPlanner(meal, nameOfMale, ingredients);

    }
}
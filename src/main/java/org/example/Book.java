package org.example;

class Book {

    private String title;
    private int yearOfPublishing;
    private String[] authors;

    public Book(String title, int yearOfPublishing, String[] authors) {
        this.title = title;
        this.yearOfPublishing = yearOfPublishing;
        this.authors = authors;
    }
    @Override
    public String toString() {
        return "title=" + title + ",yearOfPublishing=" + yearOfPublishing +
                ",authors=[" + String.join(",", authors) + "]";
    }
}
class Main2 {
    public static void main(String[] args) {
        String[] list = {"Mario Fusco", "Alan Mycroft"};
        Book book = new Book("Java 8 & 9 in Action", 2017, list);
        System.out.println(book);
    }
}
class Vehicle {

    protected String licensePlate;

    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    @Override
    public String toString() {
        return "Vehicle{licensePlate=" + licensePlate + "}";
    }
}

class Car extends Vehicle {

    protected int numberOfSeats;

    public Car(String licensePlate, int numberOfSeats) {
        super(licensePlate);
        this.numberOfSeats = numberOfSeats;
    }
    @Override
    public String toString() {
        return "Car{licensePlate=" + licensePlate +  ",numberOfSeats=" + numberOfSeats + "}";
    }
}
class Account {

    private long id;
    private String code;
    private Long balance;

    public Account(long id, String code, Long balance) {
        this.id = id;
        this.code = code;
        this.balance = balance;
    }

    // Override toString() here
    @Override
    public String toString() {
        return "Account{id=" + id + "code=" + code + "balance=" + balance + "}";
    }
}
class User {

    private String login;
    private String firstName;
    private String lastName;

    public User(String login, String firstName, String lastName) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @Override
    public String toString() {
        return "login=" + login + ",firstName=" + firstName + ",lastName=" + lastName;
    }
}
package com.example.a12122028_1220848_courseproject;

public class Student {
    private String id, firstName, lastName;

    public Student(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() { return id; }
    public String getFullName() { return firstName + " " + lastName; }
}


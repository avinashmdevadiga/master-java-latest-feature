package com.avinash.masterJava.java8.lamdas.data;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Data
@ToString
public class Student {
    private String name;
    private int gradeLevel;
    private double gpa;
    private String gender;
    private List<String> activities;
    private int noteBooks;
    private Optional<Bike> bike = Optional.empty();

    public Student(String name, int gradeLevel, double gpa, String gender, List<String> activities, int noteBooks) {
        this.name = name;
        this.gradeLevel = gradeLevel;
        this.gpa = gpa;
        this.gender = gender;
        this.activities = activities;
        this.noteBooks = noteBooks;
    }

    public Student() {

    }

    public Student(String s) {
        this.name = s;
    }

    public void printStudentActivities(){
        System.out.println(this.activities);
    }
}

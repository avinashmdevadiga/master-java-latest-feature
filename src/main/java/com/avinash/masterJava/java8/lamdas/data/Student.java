package com.avinash.masterJava.java8.lamdas.data;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class Student {
    private String name;
    private int gradeLevel;
    private double gpa;
    private String gender;
    private List<String> activities;

    public Student() {

    }

    public Student(String s) {
        this.name = s;
    }

    public void printStudentActivities(){
        System.out.println(this.activities);
    }
}

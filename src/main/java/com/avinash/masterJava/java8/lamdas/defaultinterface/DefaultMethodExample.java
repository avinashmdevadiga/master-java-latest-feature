package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultMethodExample {

    /*
    * prior java 8.
    * */
    private static List<String> sortList(){
        List<String> nameList = new java.util.ArrayList<>(List.of("Avinash", "Ravi", "Vijay", "Suresh"));
        Collections.sort(nameList);
        return nameList;
    }
    /*
    * java 8. list interface itself has default method sort, so we can directly call list.sort() instead of creating a separate method to sort the list.
    * */
    private static List<String> sortListWithDefaultMethod(){
        List<String> nameList = new java.util.ArrayList<>(List.of("Avinash", "Ravi", "Vijay", "Suresh"));
        nameList.sort(Comparator.naturalOrder());
        return nameList;
    }

    public static void main(String[] args) {

        System.out.println("Sorted List : "+sortList());
        System.out.println("Sorted List with default method : "+sortListWithDefaultMethod());
    }
}

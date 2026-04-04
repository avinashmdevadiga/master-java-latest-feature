package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Use Case: Contrasts pre-Java-8 and Java-8 list sorting approaches.
 * Before Java 8, Collections.sort() required an external utility call.
 * Java 8 introduced a default sort() method directly on List, letting code
 * call list.sort(comparator) without any helper class.
 */
public class DefaultMethodExample {

    private static final Logger LOGGER = Logger.getLogger(DefaultMethodExample.class.getName());

    /*
     * Prior Java 8 — uses Collections.sort() utility.
     */
    private static List<String> sortList(){
        LOGGER.info("DefaultMethodExample :: sortList (pre-Java-8 style) invoked");
        List<String> nameList = new java.util.ArrayList<>(List.of("Avinash", "Ravi", "Vijay", "Suresh"));
        Collections.sort(nameList);
        return nameList;
    }

    /*
     * Java 8 — list.sort() is a default method added to the List interface itself.
     */
    private static List<String> sortListWithDefaultMethod(){
        LOGGER.info("DefaultMethodExample :: sortListWithDefaultMethod (Java-8 default method style) invoked");
        List<String> nameList = new java.util.ArrayList<>(List.of("Avinash", "Ravi", "Vijay", "Suresh"));
        nameList.sort(Comparator.naturalOrder());
        return nameList;
    }

    public static void main(String[] args) {
        LOGGER.info("DefaultMethodExample :: main started");
        System.out.println("Sorted List : "+sortList());
        System.out.println("Sorted List with default method : "+sortListWithDefaultMethod());
        LOGGER.info("DefaultMethodExample :: main finished");
    }
}

package com.avinash.masterJava.java8.lamdas;

import java.util.Comparator;

public class ComparatorLamdaExample {
    public static void main(String[] args) {
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                //o1 == o1 -> 0
                //o1 > o2 -> 1
                //o1 < o2 -> -1
                return o1.compareTo(o2);

            }

        };
        System.out.println("compare method retunrs :" +comparator.compare(1,2));

        Comparator<Integer> comparatorLamda =  (o1,o2)->o1.compareTo(o2);
        System.out.println("lamda compare method return :" +comparatorLamda.compare(4,3));
    }
}

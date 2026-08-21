package com.avinash.masterJava.java8.lamdas.leetcodesolution.basicsutils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Tuple implements Comparable<Tuple> {
    int a, b;

    public Tuple(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Tuple{a=" + a + ", b=" + b + "}";
    }

    @Override
    public int compareTo(Tuple other) {
        // Sort primarily by 'b', then by 'a' if 'b' values are equal
        if (this.b != other.b) {
            return this.b - other.b;
        }
        return this.a - other.a;
    }
}
public class ArrayListUtils {

    public static void main(String[] args) {
        // Example 1: Basic ArrayList usage
        List<Integer> integerList = new ArrayList<>();
        integerList.add(10);
        integerList.add(30);
        integerList.add(40);
        integerList.add(20);
        System.out.println("ArrayList elements: {}" + integerList);

        // Example 2: Using Arrays.asList for quick initialization
        List<Integer> integerList1 = Arrays.asList(10, 20, 40, 25, 60);
        System.out.println("ArrayList (via Arrays.asList): {}" + integerList1);

        // Example 3: Sorting an array
        int[] intArray = {10, 50, 29, 15, 30};
        Arrays.sort(intArray);
        System.out.println("Sorted array: {}" + Arrays.toString(intArray));

        // Example 4: Sorting a subrange of an array
        int[] intArray1 = {10, 50, 29, 15, 30};
        Arrays.sort(intArray1, 2, intArray1.length - 1);
        System.out.println("Partially sorted array (index 2 to n-2): {}" + Arrays.toString(intArray1));

        // Example 5: Binary search in a sorted array
        int[] ints = {10, 40, 20, 90, 30, 15, 11};
        Arrays.sort(ints);
        System.out.println("Sorted array for binary search: {}" + Arrays.toString(ints));
        int key = 90;
        int index = Arrays.binarySearch(ints, key);
        System.out.println("Binary search result: key= {} " +key+"found at index= {}"+ index);

        // Example 6: Binary search in a subrange
        int key1 = 10;
        int index1 = Arrays.binarySearch(ints, 2, 7, key1);
        System.out.println("Binary search result: key= {} " +key1+"found at index= {}"+ index1);

        // Example 7: Filling an array with default values
        int[] intArray2 = new int[10];
        System.out.println("New array before fill: {}" + Arrays.toString(intArray2));
        Arrays.fill(intArray2, 5);
        System.out.println("Array after fill with 5: {}" + Arrays.toString(intArray2));

        // Example 8: Custom sort using Comparable (Tuple class)
        Tuple[] tuples = {
                new Tuple(20, 100),
                new Tuple(20, 10),
                new Tuple(10, 80),
                new Tuple(30, 10),
        };
        Arrays.sort(tuples);
        System.out.println("Tuples sorted using Comparable: {}" + Arrays.toString(tuples));

        // Example 9: Custom sort using Comparator (Lambda expression)
        Tuple[] tuples1 = {
                new Tuple(40, 100),
                new Tuple(20, 20),
                new Tuple(40, 80),
                new Tuple(30, 10),
        };
        Arrays.sort(tuples1, (o1, o2) -> {
            if (o1.b != o2.b) return o1.b - o2.b;
            return o1.a - o2.a;
        });
        System.out.println("Tuples sorted using Comparator (Lambda): {}" + Arrays.toString(tuples1));
    }
}

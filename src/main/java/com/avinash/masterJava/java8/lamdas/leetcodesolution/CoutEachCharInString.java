package com.avinash.masterJava.java8.lamdas.leetcodesolution;

import java.util.HashMap;
import java.util.Map;

/*
* Problem: Count how many times each character appears in a string. Example:

Input: s = "programming"
Output: {p=1, r=2, o=1, g=2, a=1, m=2, i=1, n=1}
* */
public class CoutEachCharInString {
    private  static void countOccurenceOfEachString(String str){
    Map<Character,Integer> occuranceMap = new HashMap<>();
    for (Character ch: str.toCharArray()){
        occuranceMap.put(ch,occuranceMap.get(ch) == null? 1 : occuranceMap.get(ch)+1);
    }
        System.out.println(occuranceMap);

    }
    public static void main(String[] args) {
        countOccurenceOfEachString("programming");

    }
}

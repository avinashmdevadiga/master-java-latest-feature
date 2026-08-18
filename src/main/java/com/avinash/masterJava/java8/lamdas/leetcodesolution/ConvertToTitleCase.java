package com.avinash.masterJava.java8.lamdas.leetcodesolution;
/*
* W11. Convert String to Title Case
Problem: Capitalize the first letter of every word. Example:

Input: s = "the quick brown fox"
Output: "The Quick Brown Fox"
* */
public class ConvertToTitleCase {

    private static String covertTitleCase(String str){
       StringBuilder result  = new StringBuilder();
       String[] strings =  str.trim().split("\\s+");
        for (int i = 0; i < strings.length; i++) {
            result.append(strings[i].substring(0, 1).toUpperCase()).append(strings[i].substring(1).toLowerCase());
            result.append(" ");
        }
        return  result.toString();
    }

    public static void main(String[] args) {
        System.out.println(covertTitleCase("the quick brown fox"));
    }
}

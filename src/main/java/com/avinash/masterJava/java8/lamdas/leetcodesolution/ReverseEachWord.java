package com.avinash.masterJava.java8.lamdas.leetcodesolution;

/*
* W9. Reverse Each Word in a Sentence (Keep Word Order)
Problem: Reverse the letters of each word individually, but keep the word order unchanged. Example:

Input: s = "Hello World"
Output: "olleH dlroW"
* */
public class ReverseEachWord {

    private static String reverseEachWord(String str){
        String[] strArray = str.trim().split("\\s+");
        String reversedString  ="";
        for (String str1 : strArray){
            reversedString =" "+reversedString;
            for(Character ch : str1.toCharArray()){
                reversedString=ch+reversedString;
            }
        }
        return reversedString.trim();
    }

    private static String reverEachWord(String str){
        StringBuilder result =  new StringBuilder();
        for(String st :  str.split("//s+")){
            result.append(new StringBuilder(st).reverse());
            result.append(" ");
        }
        return result.toString().trim();
    }

    public static void main(String[] args) {
        System.out.println(reverseEachWord("Hello World"));
        System.out.println(reverEachWord("Hello World"));
    }
}

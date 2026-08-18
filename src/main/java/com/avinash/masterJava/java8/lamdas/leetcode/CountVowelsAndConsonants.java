package com.avinash.masterJava.java8.lamdas.leetcode;

/*
* Problem: Count the number of vowels and consonants in a string. Example:

Input: s = "Hello World"
Output: vowels = 3, consonants = 7
* */
public class CountVowelsAndConsonants {

    private static void printVowelsAndConsonantsCount(String str) {
        int vowelCount = 0, consonantsCount = 0;
        for (char ch : str.toCharArray()) {
            if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' ||
                        ch == 'A' || ch == 'E' || ch == 'I' || ch == 'O' || ch == 'U') vowelCount++;
                else
                    consonantsCount++;
            }

        }
        System.out.println("Vowels count: " + vowelCount + " Consonants Count : " + consonantsCount);
    }


    private static void printVowelsConsonantsCountSimple(String str){
        int vowelCount =0, consonantsCount = 0;
        String vowelSets = "aeiouAEIOU";
        for(char ch : str.toCharArray()){
            if(!Character.isLetter(ch)) continue;
            if(vowelSets.indexOf(ch) != -1) vowelCount++;
            else
             consonantsCount++;
        }
        System.out.println("Vowels count: " + vowelCount + " Consonants Count : " + consonantsCount);
    }

    public static void main(String[] args) {
        printVowelsAndConsonantsCount("hello World");
        printVowelsConsonantsCountSimple("hello World");
    }
}

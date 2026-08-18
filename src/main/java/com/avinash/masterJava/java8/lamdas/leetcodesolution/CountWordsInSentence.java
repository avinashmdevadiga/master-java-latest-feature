package com.avinash.masterJava.java8.lamdas.leetcodesolution;
/*
* W8. Count Words in a Sentence
Problem: Count the number of words in a sentence. Example:

Input: s = "  The quick  brown fox  "
Output: 4
* */
public class CountWordsInSentence {

    private static int countWordsInSentence(String str){
        return  str.trim().split("\\s+").length;

    }

    public static void main(String[] args) {
        System.out.println(countWordsInSentence("  The quick  brown fox  "));
    }
}

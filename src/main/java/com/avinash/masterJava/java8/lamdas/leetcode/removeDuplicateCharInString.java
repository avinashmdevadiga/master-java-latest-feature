package com.avinash.masterJava.java8.lamdas.leetcode;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
/*
* Problem: Remove duplicate characters, keeping only the first occurrence of each. Example:

Input: s = "programming"
Output: "progamin"
* */
public class removeDuplicateCharInString {
    private static void removeDuplicateChar(String str){

        Set<Character> characterSet = new LinkedHashSet<>();
        for (Character ch : str.toCharArray()){
            characterSet.add(ch);
        }
        StringBuilder sb = new StringBuilder();
        for(Character ch : characterSet){
            sb.append(ch);
        }
        System.out.println(sb);

    }
    public static void main(String[] args) {
        removeDuplicateChar("programming");
    }
}

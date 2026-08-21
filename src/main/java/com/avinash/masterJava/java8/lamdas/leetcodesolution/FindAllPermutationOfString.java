package com.avinash.masterJava.java8.lamdas.leetcodesolution;

import java.util.ArrayList;
import java.util.List;

/*
* W13. Find All Permutations of a String
Problem: Print/return all permutations of a string. Example:

Input: s = "abc"
Output: ["abc","acb","bac","bca","cab","cba"]
*
* */
public class FindAllPermutationOfString {

    private static List<String> getAllPermutationString(String str){
        ArrayList<String> permutations =  new ArrayList<>();
        backTrack(str.toCharArray(),0,permutations);
        return permutations;

    }

    private static void backTrack(char[] charArray, int start, ArrayList<String> permutations) {
        if(start ==  charArray.length){
            permutations.add(new String(charArray));
            return;
        }
        for (int i = start; i < charArray.length; i++) {
            swap(charArray,start,i);
            backTrack(charArray,start+1,permutations);
            swap(charArray,start,i);
        }

    }

    private static void swap(char[] chars,int i,int j ){
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }

    public static void main(String[] args) {
        System.out.println(getAllPermutationString("abc"));
    }
}

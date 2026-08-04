package com.avinash.masterJava.java8.lamdas.leetcodesolution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
* W7. Check if Two Strings are Anagrams
Problem: Determine if two strings are anagrams of each other (same letters, same counts, any order). Example:

Input: s1 = "listen", s2 = "silent"
Output: true
* */
public class CheckStringAnagram {

    public static void main(String[] args) {
        System.out.println(checkStringAnagram("listen","silent"));
        System.out.println(checkStringAnagram("listem","silent"));

        System.out.println(checkStringAnagramSimple("listen","silent"));
        System.out.println(checkStringAnagramSimple("listem","silent"));
        System.out.println(checkStringAnagramSimple("listenjjjj","silent"));

        System.out.println(isAnagram("listen","silent"));
        System.out.println(isAnagram("listem","silent"));
        System.out.println(isAnagram("listenjjjj","silent"));
    }

    private static boolean checkStringAnagram(String str1, String str2) {
        if(str1.length() != str2.length()) return false;
        Map<Character, Integer> str1Map =  new HashMap<>();
        Map<Character, Integer> str2Map =  new HashMap<>();
        for (Character  ch:  str1.toCharArray()){
            str1Map.put(ch,str1Map.get(ch) == null? 1: str1Map.get(ch)+1);
        }
        for (Character  ch:  str2.toCharArray()){
            str2Map.put(ch,str2Map.get(ch) == null? 1: str2Map.get(ch)+1);
        }
       for(Character ch : str1Map.keySet()){
           if(!Objects.equals(str1Map.get(ch), str2Map.get(ch))) return false;
       }
       return true;
    }

    private static boolean checkStringAnagramSimple(String str1, String str2){
        if(str1.length() != str2.length()) return false;
        char[] str1Chars =  str1.toCharArray();
        char[] str2Chars =  str2.toCharArray();

        Arrays.sort(str1Chars);
        Arrays.sort(str2Chars);

        return Arrays.equals(str1Chars, str2Chars);
    }

    // more eficientWay

    private static boolean isAnagram(String str1, String str2){
        int[] alphabetCounts =  new int[26];
        if(str1.length() != str2.length()) return false;
        for(int i =0; i <str1.length();i++) {
            alphabetCounts[str1.charAt(i)-'a']++;
            alphabetCounts[str2.charAt(i)-'a']--;
        }
        for (int i: alphabetCounts){
            if(i != 0) return false;
        }
        return true;
    }
}

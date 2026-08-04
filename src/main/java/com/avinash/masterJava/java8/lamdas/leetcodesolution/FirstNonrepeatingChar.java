package com.avinash.masterJava.java8.lamdas.leetcodesolution;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/*
* W6. Find the First Non-Repeating Character
Problem: Find the first character in a string that doesn't repeat. Example:
* Input: s = "swiss"
Output: 'w'
* */
public class FirstNonrepeatingChar {

    private static Character findFirstNonrepeatingChar(String str){
       Map<Character,Integer> countMap =  new LinkedHashMap<>();
       for (Character ch  : str.toCharArray()){
           countMap.put(ch, countMap.get(ch) == null? 1: countMap.get(ch)+1);
       }
        Optional<Map.Entry<Character, Integer>> first = countMap.entrySet().stream().filter(ch -> ch.getValue() == 1).findFirst();
       return first.isPresent()? first.get().getKey() : null;

    }

    private static Character findFirstNonRepeatedCharSimpleWay(String str){
        for (Character ch : str.toCharArray()){
            if (str.indexOf(ch) == str.lastIndexOf(ch)){
                return ch;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(findFirstNonrepeatingChar("SWISS"));
        System.out.println(findFirstNonRepeatedCharSimpleWay("SWISS"));
    }
}

package com.avinash.masterJava.java8.lamdas.leetcodesolution;

/*
W12. Check if a String is a Rotation of Another
Problem: Given two strings, determine if one is a rotation of the other. Example:

Input: s1 = "waterbottle", s2 = "erbottlewat"
Output: true
* */
public class StringRotationCheck {

    private static boolean isRotationalString(String first, String second){
        String temp  = first;
        if(first.length() != second.length()) return  false;
        for (int i = 0; i < first.toCharArray().length-1 ; i++) {
            temp = new StringBuilder(temp.substring(1)).append(temp.charAt(0)).toString();
            if(temp.equals(second))
                return true;
        }
        return false;
    }

    private static boolean isRotationString(String first,String second){
        if(first.length() != second.length()) return false;
         return (first+first).contains(second);
    }


    public static void main(String[] args) {
        System.out.println(isRotationalString("waterbottle","erbottlewat"));
        System.out.println(isRotationString("waterbottle","erbottlewat"));
    }
}




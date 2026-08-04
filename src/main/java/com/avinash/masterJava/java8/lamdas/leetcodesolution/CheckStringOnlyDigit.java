package com.avinash.masterJava.java8.lamdas.leetcodesolution;

/*
* W10. Check if a String Contains Only Digits
Problem: Determine if a string consists only of numeric digits. Example:

Input: s = "12345"
Output: true

Input: s = "123a5"
Output: false
* */
public class CheckStringOnlyDigit {

    private static boolean isOnlyDigitInString(String str){

        if(str.isEmpty()) return false;
        for (Character  ch: str.toCharArray()){
            if(!Character.isDigit(ch)) return false;
        }
        return true;
    }
    public static void main(String[] args) {
        System.out.println("is Digit only "+ isOnlyDigitInString("12345"));
        System.out.println("is Digit only "+ isOnlyDigitInString("123a5"));
    }
}

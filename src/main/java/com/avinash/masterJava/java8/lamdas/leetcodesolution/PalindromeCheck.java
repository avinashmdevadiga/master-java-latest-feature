package com.avinash.masterJava.java8.lamdas.leetcodesolution;
/*
* Problem: Determine if a string reads the same forwards and backwards. Example:

Input: s = "madam"
Output: true
* */

public class PalindromeCheck {
    private static boolean isPalindromeByManual(String str){
        String reverseString = "";
        for(char ch: str.toCharArray()){
            reverseString = ch + reverseString;
        }
        return str.equals(reverseString);
    }

    private static boolean isPalindromeBySb(String str){
        return new StringBuilder(str).reverse().toString().equals(str);
    }

    private static boolean isPalindromeByManualSwap(String str){
        char[] chars = str.toCharArray();
        int left = 0, right = chars.length-1;
        if(left<right){
            char temp =  chars[left]; chars[left]= chars[right];chars[right]= temp;
            left++; right--;
        }
        return new String(chars).equals(str);
    }

    private static boolean isPalindromeSimple(String str){
        if(str.length() < 3) return false;
        int left = 0, right = str.length()-1;
        while(left < right){
            if(str.charAt(left) != str.charAt(right)) return false;
            left ++; right--;

        }
        return true;

    }

    public static void main(String[] args) {
        System.out.println("madam palindrome : "+isPalindromeByManual("madam"));
        System.out.println("avinash palindrome : "+isPalindromeByManual("avinash"));
        System.out.println("kanak palindrome : "+isPalindromeByManual("kanak"));

        System.out.println("-------------------------------");

        System.out.println("madam palindrome : "+isPalindromeBySb("madam"));
        System.out.println("avinash palindrome : "+isPalindromeBySb("avinash"));
        System.out.println("kanak palindrome : "+isPalindromeBySb("kanak"));

        System.out.println("-------------------------------");

        System.out.println("madam palindrome : "+isPalindromeByManualSwap("madam"));
        System.out.println("avinash palindrome : "+isPalindromeByManualSwap("avinash"));
        System.out.println("kanak palindrome : "+isPalindromeByManualSwap("kanak"));


        System.out.println("-------------------------------");

        System.out.println("madam palindrome : "+isPalindromeSimple("madam"));
        System.out.println("avinash palindrome : "+isPalindromeSimple("avinash"));
        System.out.println("kanak palindrome : "+isPalindromeSimple("kk"));
    }
}

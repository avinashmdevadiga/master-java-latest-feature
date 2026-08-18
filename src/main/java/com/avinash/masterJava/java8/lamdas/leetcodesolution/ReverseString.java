package com.avinash.masterJava.java8.lamdas.leetcodesolution;

public class ReverseString {

    private static String reverseString(String str){
        String reveresedString = "";
        for(char ch : str.toCharArray()){
            reveresedString = ch + reveresedString;
        }

        return reveresedString;
    }


    private static String reverseStringBySb(String string){
        return new StringBuilder(string).reverse().toString();
    }

    // manual shift method

    private static String reverseByManualSwap(String string){
        char[] chars = string.toCharArray();
        int left =0, right =chars.length-1;
        if(left<right){
            char temp = chars[left]; chars[left] = chars[right]; chars[right] = temp;
            left++; right--;
        }
        return new String(chars);
    }
    public static void main(String[] args) {
        System.out.println(reverseString("hello"));
        System.out.println(reverseStringBySb("hello"));
        System.out.println(reverseByManualSwap("hello"));
    }
}

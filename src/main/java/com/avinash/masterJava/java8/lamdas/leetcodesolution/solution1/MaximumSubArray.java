package com.avinash.masterJava.java8.lamdas.leetcodesolution.solution1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
* Problem Statement:
Given an integer array nums, find the contiguous subarray (containing at least one number) which has the largest sum, and return its sum.

Examples:

Example 1:
Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6
Explanation: The subarray [4,-1,2,1] has the largest sum = 6.

Example 2:
Input: nums = [1]
Output: 1
Explanation: The subarray [1] itself has the largest sum.

Example 3:
Input: nums = [5,4,-1,7,8]
Output: 23
Explanation: The subarray [5,4,-1,7,8] has the largest sum = 23.
* */
public class MaximumSubArray {

    private static void maxSubArray(int[] arry) {
        if (arry.length == 0) {
            System.out.println("Array Is Empty!!!");
            return;
        }
        if (arry.length == 1) {
            System.out.println("Sum: " + arry[0] + " Sub array: " + arry);
            return;
        }
        int answer = 0;
        int sum = 0;
        List<Integer> intList =  new ArrayList<>();
        for (int i = 0; i < arry.length; i++) {
            sum += arry[i];

            if (sum < 0) {
                sum = 0;
                intList.removeAll(intList);
            }else{
                intList.add(arry[i]);
            }
            if (sum > answer) {
                answer = sum;
            }
        }
        System.out.println(answer);
        System.out.println(intList);
    }
    public static void main(String[] args) {
        int[] inputs = {-2,1,-3,4,-1,2,1,-5,4};
        maxSubArray(inputs);
    }
}

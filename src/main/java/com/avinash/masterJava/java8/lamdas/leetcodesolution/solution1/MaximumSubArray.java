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
//my solution not upto mark :(
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

    private static void printMaxSunSubArray(int[] inputArray){
        int maxSoFar = inputArray[0];
        int currentMax = inputArray[0];
        int start = 0,end = 0,temp = 0;

        for (int i = 1; i < inputArray.length; i++) {
            if(currentMax+inputArray[i]< inputArray[i]){
                currentMax = inputArray[i];
                temp = i;
            }else{
                currentMax += inputArray[i];
            }

            if(currentMax > maxSoFar){
                maxSoFar = currentMax;
                start = temp;
                end =i;
            }

        }

        System.out.println("max sub array :: "+ Arrays.toString(Arrays.copyOfRange(inputArray, start, end)));
        System.out.println(" max sum of sub array"+ maxSoFar);

    }
    public static void main(String[] args) {
        int[] inputs = {-2,1,-3,4,-1,2,1,-5,4};
        int[] inputs1 = {1,1,2,-2,1,3};
        int[] inputs2 = {5,4,-1,7,8};
        maxSubArray(inputs);// not recomended
        printMaxSunSubArray(inputs); // see this solution
        printMaxSunSubArray(inputs1);
        printMaxSunSubArray(inputs2);
    }
}

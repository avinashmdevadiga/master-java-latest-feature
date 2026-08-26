package com.avinash.masterJava.java8.lamdas.leetcodesolution.solution1;

import java.util.Arrays;

/*
*
* Problem Statement:
Given an integer array nums, rotate the array to the right by k steps, where k is non-negative.

Explanation:
- Rotating the array means shifting elements to the right.
- The last k elements move to the front.
- You must do this in-place with O(1) extra space if possible.

Examples:

Example 1:
Input: nums = [1,2,3,4,5,6,7], k = 3
Output: [5,6,7,1,2,3,4]
Explanation: Rotate 1 step → [7,1,2,3,4,5,6]
Rotate 2 steps → [6,7,1,2,3,4,5]
Rotate 3 steps → [5,6,7,1,2,3,4]

Example 2:
Input: nums = [-1,-100,3,99], k = 2
Output: [3,99,-1,-100]
Explanation: Rotate 1 step → [99,-1,-100,3]
Rotate 2 steps → [3,99,-1,-100]

Example 3:
Input: nums = [10,20,30,40,50], k = 1
Output: [50,10,20,30,40]
Explanation: Rotate 1 step → [50,10,20,30,40]

* */
public class RotateArray {

    private static int[] rotateArray(int[] inputs, int key){
        if(inputs == null || inputs.length==0) return null;
        int n = inputs.length;
        int k = key% n;
        reverse(inputs,0,n-1);
        reverse(inputs,0,k-1);
        reverse(inputs,k,n-1);

        return inputs;


    }

    private static void reverse(int[] arr,  int start, int end){
        while(start<end){
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }

    public static void main(String[] args) {
        System.out.println("rotate array :"+Arrays.toString(rotateArray(new int[]{1,2,3,4,5,6,7},3)));
    }
}

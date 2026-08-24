package com.avinash.masterJava.java8.lamdas.leetcodesolution.solution1;
/*
* Problem Statement:
Given an integer array nums, find the contiguous subarray within the array (containing at least one number) that has the largest product, and return the product.

Explanation:
- Unlike the maximum sum subarray problem, here negative numbers can flip the product.
- You need to keep track of both maximum and minimum products at each step, because a negative number can turn a small minimum into a large maximum.
- The goal is to find the maximum product among all contiguous subarrays.

Examples:

Example 1:
Input: nums = [2,3,-2,4]
Output: 6
Explanation: The subarray [2,3] has the largest product = 6.

Example 2:
Input: nums = [-2,0,-1]
Output: 0
Explanation: The subarray [0] has the largest product = 0.
Note that [-2,-1] gives product 2, but they are not contiguous in this case.

Example 3:
Input: nums = [-2,3,-4]
Output: 24
Explanation: The subarray [3,-4] has product = -12, but including [-2,3,-4] gives product = 24, which is the maximum.

* */
public class MaxProductSubArray {

    private static int maxSubArrayProduct(int[] inputArray){
       if(inputArray == null || inputArray.length ==0){
           return 0;
       }
       int maxProduct = inputArray[0];
       int minProduct = inputArray[0];
       int answer = inputArray[0];


        for (int i = 1; i <inputArray.length ; i++) {
            if(inputArray[i]>=0){
                maxProduct = Math.max(inputArray[i],maxProduct*inputArray[i]);
                minProduct = Math.min(inputArray[i],minProduct*inputArray[i]);
            }else{
                int  temp = maxProduct;
                maxProduct = Math.max(inputArray[i],minProduct*inputArray[i]);
                minProduct = Math.min(inputArray[i],temp*inputArray[i]);
            }
        }
      return Math.max(answer,maxProduct);
    }

    private static int betterMaxsubArrayProduct(int[] nums){
        if(nums==null || nums.length == 0) return 0;

        int maxProduct= nums[0], minProduct = nums[0], result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if((nums[i]<0)){
                int temp = maxProduct;
                maxProduct = minProduct;
                minProduct = temp;
            }

            maxProduct =  Math.max(nums[i],maxProduct*nums[i]);
            minProduct =  Math.min(nums[i],minProduct*nums[i]);

        }
        return Math.max(result,maxProduct);

    }

    public static void main(String[] args) {
        /*System.out.println("max sub arrray product : "+maxSubArrayProduct(new int[]{2,3,-2,4}));
        System.out.println("max sub arrray product : "+maxSubArrayProduct(new int[]{-2,0,-1}));
        System.out.println("max sub arrray product : "+maxSubArrayProduct(new int[]{-2,3,-4}));
        System.out.println("max sub arrray product : "+maxSubArrayProduct(new int[]{1,1,1}));
        System.out.println("max sub arrray product : "+maxSubArrayProduct(new int[]{-1,-3,-1}))*/;


        System.out.println("max sub arrray product : "+betterMaxsubArrayProduct(new int[]{2,3,-2,4}));
        System.out.println("max sub arrray product : "+betterMaxsubArrayProduct(new int[]{-2,0,-1}));
        System.out.println("max sub arrray product : "+betterMaxsubArrayProduct(new int[]{-2,3,-4}));
        System.out.println("max sub arrray product : "+betterMaxsubArrayProduct(new int[]{1,1,1}));
        System.out.println("max sub arrray product : "+betterMaxsubArrayProduct(new int[]{-1,-3,-1}));
    }
}

package com.avinash.masterJava.java8.lamdas.leetcodesolution.solution1;

import java.util.Arrays;

/*
* Problem Statement:
Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].
The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.
You must write an algorithm that runs in O(n) time and without using the division operation.

Explanation:
- For each index i, compute the product of all elements except nums[i].
- Division is not allowed, so you need to use prefix and suffix products.
- Prefix product = product of all elements before i.
- Suffix product = product of all elements after i.
- answer[i] = prefix[i] * suffix[i].

Examples:

Example 1:
Input: nums = [1,2,3,4]
Output: [24,12,8,6]
Explanation:
answer[0] = 2*3*4 = 24
answer[1] = 1*3*4 = 12
answer[2] = 1*2*4 = 8
answer[3] = 1*2*3 = 6

Example 2:
Input: nums = [-1,1,0,-3,3]
Output: [0,0,9,0,0]
Explanation:
answer[0] = 1*0*-3*3 = 0
answer[1] = -1*0*-3*3 = 0
answer[2] = -1*1*-3*3 = 9
answer[3] = -1*1*0*3 = 0
answer[4] = -1*1*0*-3 = 0

Example 3:
Input: nums = [2,3,4,5]
Output: [60,40,30,24]
Explanation:
answer[0] = 3*4*5 = 60
answer[1] = 2*4*5 = 40
answer[2] = 2*3*5 = 30
answer[3] = 2*3*4 = 24

* */
public class ProductOfArrayExceptSelf {

    // solution o(n2) not recomended
    private static int[] productOfArrayExceptSelf(int[] inputs){
        if(inputs==null || inputs.length == 0) return null;
        if(inputs.length ==1) return inputs;
        int[] outputArray =  new int[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            int temp= 1;
            for (int j=0; j<inputs.length; j++){
                if(i==j) continue;
                temp*=inputs[j];
            }
            outputArray[i]= temp;

        }
        return outputArray;
    }

    private static int[] maxSubArrayProductExceptSelfBest(int[] inputs){
        if(inputs==null || inputs.length == 0) return null;
        int n = inputs.length;
        int[] leftArray = new int[n];
        int[] rightArray = new int[n];
        int[] finalArray = new int[n];
        leftArray[0]=1;
        rightArray[n-1]=1;

        for (int i = 1; i <n ; i++) {
            leftArray[i]= leftArray[i-1]*inputs[i-1];
        }
        for (int i = n-2; i >=0 ; i--) {
            rightArray[i]= rightArray[i+1]*inputs[i+1];
        }
        for (int i = 0; i <n ; i++) {
            finalArray[i] = leftArray[i]*rightArray[i];

        }
        return finalArray;


    }

    private static int[] simplerSolution(int[] inputs){
        if(inputs==null || inputs.length == 0) return null;
        int n = inputs.length;
       int[] outputArray = new int[n];
       outputArray[0] =1;
        for (int i = 1; i <n ; i++) {
            outputArray[i]=outputArray[i-1]*inputs[i-1];

        }
        int suffix =1;
        for(int i=n-1;i>=0;i--){
            outputArray[i]*= suffix;
            suffix*=inputs[i];
        }
        return outputArray;


    }

    public static void main(String[] args) {
        /*System.out.println("product of array Except itself: "+ Arrays.toString(productOfArrayExceptSelf(new int[]{1,2,3,4})));
        System.out.println("product of array Except itself: "+ Arrays.toString(productOfArrayExceptSelf(new int[]{-1,1,0,-3,3})));
        System.out.println("product of array Except itself: "+ Arrays.toString(productOfArrayExceptSelf(new int[]{2,3,4,5})));*/

        /*System.out.println("product of array Except itself: "+ Arrays.toString(maxSubArrayProductExceptSelfBest(new int[]{1,2,3,4})));
        System.out.println("product of array Except itself: "+ Arrays.toString(maxSubArrayProductExceptSelfBest(new int[]{-1,1,0,-3,3})));
        System.out.println("product of array Except itself: "+ Arrays.toString(maxSubArrayProductExceptSelfBest(new int[]{2,3,4,5})));*/


        System.out.println("product of array Except itself: "+ Arrays.toString(simplerSolution(new int[]{1,2,3,4})));
        System.out.println("product of array Except itself: "+ Arrays.toString(simplerSolution(new int[]{-1,1,0,-3,3})));
        System.out.println("product of array Except itself: "+ Arrays.toString(simplerSolution(new int[]{2,3,4,5})));
    }
}

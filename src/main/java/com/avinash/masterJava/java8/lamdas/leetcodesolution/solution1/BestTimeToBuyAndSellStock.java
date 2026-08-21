package com.avinash.masterJava.java8.lamdas.leetcodesolution.solution1;
/*
* Problem Statement:
You are given an array prices where prices[i] is the price of a given stock on the i-th day.
You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.
Return the maximum profit you can achieve from this transaction. If no profit is possible, return 0.

Explanation:
- You must buy before you sell.
- The goal is to find the maximum difference (prices[j] - prices[i]) where j > i.
- If prices are always decreasing, the maximum profit is 0.

Examples:

Example 1:
Input: prices = [7,1,5,3,6,4]
Output: 5
Explanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6 - 1 = 5.

Example 2:
Input: prices = [7,6,4,3,1]
Output: 0
Explanation: In this case, no transactions are possible because prices keep decreasing, so the maximum profit = 0.

Example 3:
Input: prices = [2,4,1]
Output: 2
Explanation: Buy on day 1 (price = 2) and sell on day 2 (price = 4), profit = 4 - 2 = 2.

* */
public class BestTimeToBuyAndSellStock {

    // my solution not recomended
    private static void printMaxProfitEarned(int[] priceArray){
        int maxSoFar =0;
        int minSofar =priceArray[0];
        int minindex =0, maxIndex =0;
        if (priceArray.length == 1) System.out.println("Zero profit");
        for (int i = 1; i <priceArray.length ; i++) {
            if(minSofar > priceArray[i]){
                minSofar = priceArray[i];
                minindex =i;
            }
            if(maxSoFar < priceArray[i] ){
                maxSoFar = priceArray[i];
                maxIndex = i;
            }

        }
        if(maxSoFar > minSofar && minindex < maxIndex){
            System.out.println(" buy a stock on day "+ (minindex++)+" sell on day "+maxIndex++);
            System.out.println("profit earned "+ (maxSoFar-minSofar));
        }else{
            System.out.println("In this case, no transactions are possible because prices keep decreasing, so the maximum profit = 0.");
        }

    }

    private static void printMaxProfit(int[] inputArray){
        int minSofar = inputArray[0];
        int ans = 0,sellDay =0, buyDay =0;

        for (int i = 1; i <inputArray.length ; i++) {
            int profit  =  inputArray[i] - minSofar;
            if(profit > ans){
                ans = profit;
                sellDay =i;
            }

            if(inputArray[i] < minSofar){
                minSofar =inputArray[i];
                        buyDay = i;
            }


        }
        System.out.println("buy at day : "+buyDay+" sell at day : "+sellDay+" max profit ::"+ans);
    }
    public static void main(String[] args) {
//        printMaxProfitEarned(new int[]{7,1,5,3,6,4});
//        printMaxProfitEarned(new int[]{7,6,4,3,1});
//        printMaxProfitEarned(new int[]{2,4,1});


        printMaxProfit(new int[]{7,1,5,3,6,4});
        printMaxProfit(new int[]{7,6,4,3,1});
        printMaxProfit(new int[]{2,4,1});
    }
}

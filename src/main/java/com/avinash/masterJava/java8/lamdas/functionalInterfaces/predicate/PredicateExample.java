package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import java.util.function.Predicate;

public class PredicateExample {
    /*
    * Predicate is a Funtional interface which accept the values and return the boolean type.
    * it has test() SAM.
    * it have and, or, negate default method.
    * */
    public static void main(String[] args) {
        Predicate<Integer> isEvenNumber = (number)-> number%2 ==0;
        System.out.println("is even :" +isEvenNumber.test(10) );

        Predicate<Integer> devisibleBy5 = (number)-> number%5==0;
        System.out.println("even numberDevissible by 5 with And :"+isEvenNumber.and(devisibleBy5).test(10));
        System.out.println("even numberDevissible by 5 with And :"+isEvenNumber.and(devisibleBy5).test(8));

        System.out.println("even numberDevissible by 5 with or :"+isEvenNumber.or(devisibleBy5).test(10));
        System.out.println("even numberDevissible by 5 with or :"+isEvenNumber.or(devisibleBy5).test(8));


        System.out.println("even numberDevissible by 5 with negate :"+isEvenNumber.and(devisibleBy5).test(8));

    }
}

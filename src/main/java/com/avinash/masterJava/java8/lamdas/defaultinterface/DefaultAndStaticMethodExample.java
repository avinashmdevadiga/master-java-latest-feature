package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.List;

public class DefaultAndStaticMethodExample {

    public static void main(String[] args) {
         Multiplier multiplier = new MultiplierImpl();
            System.out.println("multify :: "+multiplier.multify(List.of(1,2,8)));
            System.out.println("sizeOfList :: "+multiplier.sizeOfList(List.of(1,2,8)));
            System.out.println("isEmpty :: "+Multiplier.isEmpty(List.of()));
    }
}

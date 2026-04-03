package com.avinash.masterJava.java8.lamdas.defaultinterface;

public interface Interface2 extends InterFace1{
    default void method2(){
        System.out.println("inside the InterFace2 method2");
    }

    default void method1(){
        System.out.println("inside the InterFace2 method1");
    }
}

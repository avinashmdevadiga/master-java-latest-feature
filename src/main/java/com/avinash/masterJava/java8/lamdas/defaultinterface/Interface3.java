package com.avinash.masterJava.java8.lamdas.defaultinterface;

public interface Interface3 extends Interface2 {
        default void method3(){
            System.out.println("inside the InterFace3 method3");
        }

    default void method2(){
        System.out.println("inside the InterFace3 method2");
    }
}

package com.avinash.masterJava.java8.lamdas.defaultinterface;

public class InterfaceImpl implements InterFace1,Interface2,Interface3{
    public void method1(){
        System.out.println("inside the InterfaceImpl method1");
    }
    public static void main(String[] args) {
        InterfaceImpl interfaceImpl = new InterfaceImpl();
        //Java always takes the the implemetation from the child interface if there are multiple default method with same signature in the parent interfaces
        interfaceImpl.method1();
        interfaceImpl.method2();
        interfaceImpl.method3();
    }
}

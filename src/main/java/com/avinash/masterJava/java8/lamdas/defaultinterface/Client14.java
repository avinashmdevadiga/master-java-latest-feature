package com.avinash.masterJava.java8.lamdas.defaultinterface;

public class Client14 implements InterFace1,Interface4{
    //here we are getting error since both the interface have same method name and same signature and both are default method so we need to override the method in the client class to resolve the conflict.
    @Override
    public void method1() {
        System.out.println("inside the Client14 method1");
    }

    public static void main(String[] args) {
            Client14 client14 = new Client14();
            client14.method1();
    }
}

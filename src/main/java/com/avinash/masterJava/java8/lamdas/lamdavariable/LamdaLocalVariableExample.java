package com.avinash.masterJava.java8.lamdas.lamdavariable;

import java.util.function.Consumer;

public class LamdaLocalVariableExample {

    public static void main(String[] args) {
        int i =0;
        Consumer<String> consumer = (a)-> {//'i' cannot be used, not allowed to use same name as local variable
            //i= i++;// local varible modification not allowed. we need to make fine
            System.out.println(i);
        };
    }
}

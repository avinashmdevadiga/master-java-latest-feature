package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import java.util.function.Function;

public class FunctionExample {
    /*
    * Function is a Functional Interface which ha SAM apply()
    * it has other default method such as compose(), andThen()
    * */

    // here in Function first string indicate type which we passing, second String is a return type
    private static Function<String ,String> uppercaseFunction = name -> name.toUpperCase();

    private static Function<String,String> concatFunction =  name ->name.concat("default");

    private static  Function<String,String>  conactFunction1 = name ->name.concat("function1");

    public static void main(String[] args) {
        System.out.println("uppercase of the string : "+uppercaseFunction.apply("java8"));

        // andThen method excute the function in the same order. here uppercaseFunction is executed first  the concatFunction will Execute
        // output: JAVA8default
        System.out.println("andThen() example : "+uppercaseFunction.andThen(concatFunction).apply("java8"));

        // in compose method execute the function in the reverse order.here conactFuction is executed first then uppercaseFunction will execute.
        // output: JAVA8DEFAULT
        System.out.println("compose() example : "+uppercaseFunction.compose(concatFunction).apply("java8"));
        // here concatFunction1 will execute first then concatFunction and then uppercaseFunction.
        // output: JAVA8FUNCTION1DEFAULT
        System.out.println("compose() example : "+uppercaseFunction.compose(concatFunction).compose(conactFunction1).apply("java8"));

    }
}

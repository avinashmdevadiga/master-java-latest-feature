package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.Instant;

public class InstantExample {
    public static void main(String[] args) {
        Instant instant = Instant.now();
        System.out.println("instant now : "+instant);
        //jan 1st 1970 first epoch second started and it will give what second running now
        System.out.println("instant epoch second : "+instant.getEpochSecond());

        Instant instant1 = Instant.ofEpochSecond(0);
        System.out.println(instant1);
    }
}

package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ComparingTimesDurationExample {
    public static void main(String[] args) {
        LocalTime localTime =  LocalTime.of(5,30);
        LocalTime localTime1 = LocalTime.of(7,10);

        long diff =  localTime.until(localTime1, ChronoUnit.MINUTES);
        System.out.println("Difference in minutes : "+diff);

        Duration duration = Duration.between(localTime, localTime1);
        System.out.println("Duration in minutes : "+duration.toMinutes());


    }
}

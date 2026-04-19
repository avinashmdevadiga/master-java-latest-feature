package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeExample {
    public static void main(String[] args) {

        //localdate
        LocalDate localDate = LocalDate.now();
        System.out.println("localDate :: "+localDate);
        //localtime
        LocalTime localTime = LocalTime.now();
        System.out.println("LocalTime :: "+localTime);
        //localdatetime
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("Localdatetime :: "+localDateTime);
    }
}

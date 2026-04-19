package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DatetoDateTimeConverter {
    public static void main(String[] args) {
        /*
        * java.util.Date to dateTime converter
        * */
        Date date = new Date();
        System.out.println("date: "+date);

        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.of("America/Chicago"));

        System.out.println("localDate : "+localDate);

        Date date1 = new Date().from(localDate.atTime(LocalTime.now()).atZone( ZoneId.of("America/Chicago")).toInstant());

        System.out.println("date: "+date1);
    }
}

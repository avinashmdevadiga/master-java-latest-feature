package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;

public class LocalDateExample {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        System.out.println("Local Date : "+localDate);

        LocalDate localDate1 =  LocalDate.of(2026,3,26);
        System.out.println("Local Date1 : "+localDate1);

        LocalDate  localDate2 = LocalDate.ofYearDay(2026,300);
        System.out.println("Local Date2 : "+localDate2);

        Month month = localDate.getMonth();
        System.out.println("month :: "+month);
        int monthValue = localDate.getMonthValue();
        System.out.println("monthvalue :: "+monthValue);
        IsoChronology chronology = localDate.getChronology();
        System.out.println("chronology :: "+ chronology);

        IsoEra era = localDate.getEra();
        System.out.println("Era :: "+era);
        int dayOfMonth = localDate.getDayOfMonth();
        System.out.println("dayoftheMonth :: "+dayOfMonth);
        int i = localDate.get(ChronoField.DAY_OF_MONTH);
        System.out.println("day of the month :: "+i);

        LocalDate localDate3 = localDate.plusMonths(2);
        System.out.println("plus month :: "+localDate3);

        LocalDate localDate4 = localDate.minusMonths(3);
        System.out.println("minus month :: "+localDate4);

        LocalDate localDate5 = localDate.withYear(2023);
        System.out.println("localdate with year :: "+localDate5);
    }
}

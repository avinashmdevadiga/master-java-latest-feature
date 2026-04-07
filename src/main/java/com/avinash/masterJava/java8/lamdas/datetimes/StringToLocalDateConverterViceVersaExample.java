package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringToLocalDateConverterViceVersaExample {

    public static void main(String[] args) {
        /*
        * convert string date to Local Date
        * */
        String stringDate  ="2026-03-26";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate parsedDate = LocalDate.parse(stringDate,dateTimeFormatter);
        System.out.println("parsedDate :: "+parsedDate);

        String stringDate1 = "20260326";
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(stringDate1,dateTimeFormatter1);
        System.out.println("parsedDate :: "+localDate);

        String stringDate2 = "2026*03*26";
        DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy*MM*dd");
        LocalDate localDate2 = LocalDate.parse(stringDate1,dateTimeFormatter1);
        System.out.println("parsedDate :: "+localDate2);

        /*
        * convert LocalDate to String
        * */
        String formattedDate = localDate.format(dateTimeFormatter);
        System.out.println("string date :: "+formattedDate);

        System.out.println("String Date :: "+localDate2.format(dateTimeFormatter2));




    }
}

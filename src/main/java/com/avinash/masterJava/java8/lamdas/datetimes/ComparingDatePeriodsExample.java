package com.avinash.masterJava.java8.lamdas.datetimes;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

/*
*
* */
public class ComparingDatePeriodsExample {

    public static void main(String[] args) {

        LocalDate localDate1 = LocalDate.of(2024, 1, 1);
        LocalDate localDate2 = LocalDate.of(2024, 12, 31);

        Period period = localDate1.until(localDate2);
        System.out.println("getDays : "+period.getDays());// performs 31-1 =30
        System.out.println("getMonths : "+period.getMonths());// performs 12-1 =11
        System.out.println("getYears : "+period.getYears());// performs 2024-2024 =0


        Period period1 = Period.ofDays(10);
        System.out.println("period1 getDays : "+period1.getDays());

        Period period2 = Period.ofYears(20);
        System.out.println("period2 getYears : "+period2.getYears());
        System.out.println("period2 getMonths : "+period2.toTotalMonths());

        Period between = Period.between(localDate1, localDate2);
        System.out.println("between getDays : "+between.getDays()+" getMonths : "+between.getMonths()+" getYears : "+between.getYears());



    }
}

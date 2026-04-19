package com.avinash.masterJava.java8.lamdas.datetimes;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeExample {
    public static void main(String[] args) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println("zonedDatetimevalue : "+zonedDateTime);

        System.out.println("zoned off set value : "+zonedDateTime.getOffset());
        System.out.println("zoned id : "+zonedDateTime.getZone());

        //System.out.println("Available Zones : "+ ZoneId.getAvailableZoneIds());

        System.out.println("chicago : "+ZonedDateTime.now(ZoneId.of("America/Chicago")));
        System.out.println("detroit : "+ZonedDateTime.now(ZoneId.of("America/Detroit")));
        System.out.println("los-angeles : "+ZonedDateTime.now(ZoneId.of("America/Los_Angeles")));
        System.out.println("denver : "+ZonedDateTime.now(ZoneId.of("America/Denver")));

        System.out.println("zoned date time using clock : "
        + ZonedDateTime.now(Clock.system(ZoneId.of("America/Denver"))));

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("America/Denver"));
        System.out.println("local Date time using zone :"+localDateTime);
    }
}

package com.avinash.masterJava.java8.lamdas.streamsapi;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class StreamLimitSkipExample {
    private static Optional<Integer> limitExample(List<Integer> integerList){
        return integerList.stream()
                .limit(3)
                .reduce(Integer::sum);
    }

    private static Optional<Integer> skipExample(List<Integer> integerList){
        return integerList.stream()
                .skip(3)
                .reduce(Integer::sum);
    }

    public static void main(String[] args) {
        List<Integer> integerList = List.of(2,4,5,2,6);

        limitExample(integerList).ifPresent(System.out::println);
        skipExample(integerList).ifPresent(System.out::println);
    }
}

package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.List;

public class MultiplierImpl implements Multiplier{
    @Override
    public int multify(List<Integer> integers) {
        return integers.stream()
                .reduce(1,(a,b)->a*b);
    }

    @Override
    public int sizeOfList(List<Integer> integers) {
        System.out.printf("inside the MultiplierImpl class sizeOfList method :: %s\n",integers);
        return integers.size();
    }
}

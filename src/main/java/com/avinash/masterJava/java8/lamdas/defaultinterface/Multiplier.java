package com.avinash.masterJava.java8.lamdas.defaultinterface;

import java.util.List;

public interface Multiplier {
    int multify(List<Integer> integers);

    default int sizeOfList(List<Integer> integers){
        System.out.printf("inside the Multiplier interface sizeOfList method :: %s\n",integers);
        return integers.size();
    }

    static boolean isEmpty(List<Integer> integers){
        //we can directly call this method using the interface name without creating an instance of the interface, as it is a static method.
        //we cannot override static methods in the implementing class, as they are not inherited by the implementing class.
        return integers.isEmpty();
    }
}

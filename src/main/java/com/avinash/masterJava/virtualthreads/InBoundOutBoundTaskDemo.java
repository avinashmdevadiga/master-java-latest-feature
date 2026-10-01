package com.avinash.masterJava.virtualthreads;


public class InBoundOutBoundTaskDemo {

    private static final int MAX_PLATFORM = 50_000;

    private static void platformDemo(){
        for (int i = 0; i < MAX_PLATFORM; i++) {
            int finalI = i;
            Thread thread =  new Thread(()-> Task.ioIntensive(finalI));
            thread.start();
        }
    }
    public static void main(String[] args) {
        platformDemo();
    }
}

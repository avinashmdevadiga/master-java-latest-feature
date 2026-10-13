package com.avinash.masterJava.virtualthreads;


import java.util.concurrent.CountDownLatch;

public class InBoundOutBoundTaskDemo {

    private static final int MAX_PLATFORM = 10;


    private static void platformDemo() {
        for (int i = 0; i < MAX_PLATFORM; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> Task.ioIntensive(finalI));
            thread.start();
        }
    }

    private static void platFomThreadDemo2() {
        var builder = Thread.ofPlatform().name("avi");
        for (int i = 0; i < MAX_PLATFORM; i++) {
            int finalI = i;
            Thread thread = builder.unstarted(() -> Task.ioIntensive(finalI));
            thread.start();
        }
    }

    private static void platFomThreadDemo3() throws InterruptedException {
        var builder = Thread.ofPlatform().daemon().name("avi");
        var latch = new CountDownLatch(MAX_PLATFORM);
        for (int i = 0; i < MAX_PLATFORM; i++) {
            int finalI = i;
            Thread thread = builder.unstarted(() -> {
                Task.ioIntensive(finalI);
                latch.countDown();
            });
            thread.start();

        }
        latch.await();
    }

    private static void virtualThreadDemo() throws InterruptedException {
        var builder = Thread.ofVirtual().name("virtual-",1);
        var latch  = new CountDownLatch(MAX_PLATFORM);
        for (int i = 0; i < MAX_PLATFORM; i++) {
            int finalI = i;
            Thread thread = builder.unstarted(() -> {
                Task.ioIntensive(finalI);
                latch.countDown();
            });
            thread.start();
        }
        latch.await();
    }


    public static void main(String[] args) throws InterruptedException {
        virtualThreadDemo();
    }
}

package com.avinash.masterJava.java8.lamdas;

public class RunnableLamdaExample {
    public static void main(String[] args) {
        /*
        * prior or java 8
        * */

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("inside the runnable run method");
            }
        };

        new Thread(runnable).start();

        /*
         * java 8 lamda expression
         * () -> {}
         * lot of boilerplate code can be reduced
         * () -> single statement;// curly braces not required/ its optional.
         * () -> {multiple statement};// Curly braces are mandatory.
         *  lamda gives more concise and clear code
         * */

        Runnable runnableLamda1 = ()-> {
            System.out.println("inside runnable by java 8 lamda");
        };
        new Thread(runnableLamda1).start();

        // if the single statement inside the lamda no need of curly braces.
        Runnable runnableLamda2 = ()-> System.out.println("inside runnable by java 8 lamda");
        new Thread(runnableLamda2).start();

        new Thread(()->System.out.println("inside runnable by java 8 lamda with inline implementation")).start();
    }

}

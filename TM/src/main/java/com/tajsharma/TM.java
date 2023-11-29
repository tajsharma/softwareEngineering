package com.tajsharma;

public class TM {
    public static void main(String[] args) {
        if(args.length ==0){
            System.out.println("no command entered");
            return;
        }
        String command = args[0].toLowerCase();

        switch(command){
            case "start":
                startTask(args);
                break;
            case "stop":
                stopTask(args);
                break;
        }
    }

    private static void startTask(String[] args){
        System.out.println("Entered start task");
        return;
    }

    private static void stopTask(String[] args){
        System.out.println("Entered Stop task");
    }


}
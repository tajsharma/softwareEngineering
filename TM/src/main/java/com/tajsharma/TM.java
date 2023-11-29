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
            case "describe":
                describeTask(args);
                break;
            case "size":
                defineSizeTask(args);
                break;
            case "rename":
                renameTask(args);
                break;
            case "delete":
                deleteTask(args);
                break;
            case "summary":
                summaryOfTask(args);
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

    private static void describeTask(String[] args){
        System.out.println("Entered Stop task");
    }

    private static void defineSizeTask(String[] args){
        System.out.println("Entered size task");
    }

    private static void renameTask(String[] args){
        System.out.println("Entered rename task");
    }

    private static void deleteTask(String[] args){
        System.out.println("Entered delete task");
    }

    private static void summaryOfTask(String[] args){
        System.out.println("Entered summary task");
    }


}
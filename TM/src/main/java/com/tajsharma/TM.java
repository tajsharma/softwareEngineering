package com.tajsharma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

    //private methods for each command
    private static void startTask(String[] args){
        //check if an argument for the task name was provided
        if(args.length < 2){
            System.out.println("No task name provided");
            return;
        }
        String taskName = args[1];
        logTaskAction("START", taskName);

    }

    private static void stopTask(String[] args){
        if(args.length < 2){
            System.out.println("No task name provided");
        }
        String taskName = args[1];
        logTaskAction("STOP", taskName);

    }

    private static void describeTask(String[] args){
        System.out.println("Entered describe task");
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

    private static void logTaskAction(String action, String taskName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt", true))) {
            long timestamp = System.currentTimeMillis();
            writer.write(timestamp + " " + action + " " + taskName + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
        }
    }

}
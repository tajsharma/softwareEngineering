package com.tajsharma;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
public class TM {
    public static void main(String[] args) {
        TaskCommands taskCommands = new TaskCommands();

        if(args.length == 0){
            System.out.println("no command entered");
            return;
        }
        String command = args[0].toLowerCase();

        switch(command){
            case "start":
                taskCommands.startTask(args[1]);
                break;
            case "stop":
                taskCommands.stopTask(args[1]);
                break;
         /*   case "describe":
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
                break;   */
            default:
                System.out.println("Invalid command. Please use a valid command.");
        }
    }

    //class encapsulating all the commands we want to allow
    public static class TaskCommands {
        Helpers helper = new Helpers();

        public void startTask(String taskName) {
            String record = LocalDateTime.now() + " start " + taskName;
            helper.logToDataStore(record);
        }

        public void stopTask(String taskName) {
            String record = LocalDateTime.now() + " stop " + taskName;
            helper.logToDataStore(record);
        }

        // ... other methods for different commands like describe, size, rename, delete, summary
    }

    public static class Helpers{
        private void logToDataStore(String record) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("datastore.txt", true))) {
                writer.write(record);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Error writing to data store: " + e.getMessage());
            }
        }
    }


}
package com.tajsharma;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.*;
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
            case "describe":
                taskCommands.describeTask(args);
                break;
            case "size":
                taskCommands.defineSizeTask(args);
                break;
            case "rename":
                taskCommands.renameTask(args);
                break;
            case "delete":
                taskCommands.deleteTask(args);
                break;
            case "summary":
                taskCommands.summaryOfTasks();
                break;
            default:
                System.out.println("Invalid command. Please use a valid command.");
        }
    }

    //class encapsulating all the commands we want to allow
    public static class TaskCommands {
        Helpers helper = new Helpers();
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private Map<String, Boolean> taskStatus;

        public TaskCommands() {
            taskStatus = helper.loadTaskStatusFromDataStore();
        }

        public void startTask(String taskName) {
            if (taskStatus.getOrDefault(taskName, false)) {
                System.out.println("Task '" + taskName + "' is already started.");
                return;
            }

            String record = LocalDateTime.now().format(formatter) + " START " + taskName;
            helper.logToDataStore(record);
            taskStatus.put(taskName, true);
        }

        public void stopTask(String taskName) {
            if (!taskStatus.getOrDefault(taskName, false)) {
                System.out.println("Task '" + taskName + "' is not started.");
                return;
            }

            String record = LocalDateTime.now().format(formatter) + " STOP " + taskName;
            helper.logToDataStore(record);
            taskStatus.put(taskName, false);
        }

        public void describeTask(String[] args) {
            if (!helper.checkArgLength(args, 3, "describe")) {
                return;
            }

            String taskName = args[1];
            String description = args[2];
            String size = args.length > 3 ? args[3] : "";

            // Validate if size is one of S, M, L, XL or empty
            if (!size.isEmpty() && !helper.isValidSize(size)) {
                System.out.println("Invalid size. Please use one of {S|M|L|XL}.");
                return;
            }

            String record = LocalDateTime.now().format(formatter) + " DESCRIBE " + taskName + " " + description + (size.isEmpty() ? "" : " " + size);
            helper.logToDataStore(record);
        }


        public void defineSizeTask(String[] args) {
            if (!helper.checkArgLength(args, 3, "size")) {
                return;
            }

            String taskName = args[1];
            String size = args[2];

            // Validate if size is one of S, M, L, XL
            if (!helper.isValidSize(size)) {
                System.out.println("Invalid size. Please use one of {S|M|L|XL}.");
                return;
            }

            String record = LocalDateTime.now().format(formatter) + " SIZE " + taskName + " " + size;
            helper.logToDataStore(record);
        }

        public void renameTask(String[] args) {
            if (!helper.checkArgLength(args, 3, "rename")) {
                return;
            }

            String oldTaskName = args[1];
            String newTaskName = args[2];

            String record = LocalDateTime.now().format(formatter) + " RENAME " + oldTaskName + " " + newTaskName;
            helper.logToDataStore(record);
            // add functionality to not rename a task that hasent been crated yet
        }

        public void deleteTask(String[] args) {
            if (!helper.checkArgLength(args, 2, "delete")) {
                return;
            }

            String taskName = args[1];
            // add functionality to not delete a task that hasent been crated yet
            String record = LocalDateTime.now().format(formatter) + " DELETE " + taskName;
            helper.logToDataStore(record);
        }

        public void summaryOfTasks() {
            Map<String, List<String>> taskRecords = helper.loadTaskRecordsFromDataStore();
            Map<String, String> currentNames = new HashMap<>();
            Map<String, Long> timeSpent = new HashMap<>();

            for (String key : taskRecords.keySet()) {
                List<String> records = taskRecords.get(key);
                String currentName = key;

                for (String record : records) {
                    String[] parts = record.split(" ");
                    String command = parts[2];
                    String taskName = parts[3];

                    switch (command) {
                        case "RENAME":
                            String newName = parts[4];
                            currentNames.put(taskName, newName);
                            currentName = newName;
                            break;
                        case "DELETE":
                            timeSpent.remove(currentName);
                            break;
                        case "START":
                        case "STOP":
                            updateTaskTime(timeSpent, parts, currentName, command);
                            break;
                    }
                }

                if (currentNames.containsKey(key)) {
                    String finalName = currentNames.get(key);
                    timeSpent.put(finalName, timeSpent.getOrDefault(key, 0L));
                    timeSpent.remove(key);
                }
            }

            for (Map.Entry<String, Long> entry : timeSpent.entrySet()) {
                System.out.println("Task: " + entry.getKey() + ", Time Spent: " + formatDuration(entry.getValue()));
            }

        }

        private void updateTaskTime(Map<String, Long> timeSpent, String[] parts, String taskName, String command) {
            String dateTime = parts[0] + " " + parts[1];
            LocalDateTime time = LocalDateTime.parse(dateTime, formatter);
            if (command.equals("START")) {
                helper.startTimes.put(taskName, time);
            } else {
                LocalDateTime startTime = helper.startTimes.getOrDefault(taskName, time);
                long duration = Duration.between(startTime, time).getSeconds();
                timeSpent.put(taskName, timeSpent.getOrDefault(taskName, 0L) + duration);
            }
        }

        private String formatDuration(long seconds) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
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

        public Map<String, Boolean> loadTaskStatusFromDataStore() {
            Map<String, Boolean> taskStatus = new HashMap<>();
            Path path = Paths.get("datastore.txt");
            if (!Files.exists(path)) {
                return taskStatus;
            }

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split(" ");
                    if (parts.length >= 4) {
                        String taskName = parts[3]; // Assuming task name is the fourth part
                        boolean isStart = parts[2].equalsIgnoreCase("START"); // The third part is the status
                        taskStatus.put(taskName, isStart);

                        // Debugging output
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading data store: " + e.getMessage());
            }
            return taskStatus;
        }

        public boolean isValidSize(String size) {
            return size.matches("S|M|L|XL");
        }

        public boolean checkArgLength(String[] args, int requiredLength, String commandName) {
            if (args.length < requiredLength) {
                System.out.println("Insufficient arguments for " + commandName + " command.");
                return false;
            }
            return true;
        }

        public Map<String, LocalDateTime> startTimes = new HashMap<>();

        public Map<String, List<String>> loadTaskRecordsFromDataStore() {
            Map<String, List<String>> taskRecords = new HashMap<>();
            Path path = Paths.get("datastore.txt");
            if (!Files.exists(path)) {
                return taskRecords;
            }

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split(" ");
                    if (parts.length >= 3) {
                        String command = parts[2];
                        if (command.equals("START") || command.equals("STOP") || command.equals("RENAME") || command.equals("DELETE")) {
                            String taskName = parts[3];
                            taskRecords.computeIfAbsent(taskName, k -> new ArrayList<>()).add(line);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading data store: " + e.getMessage());
            }
            return taskRecords;
        }
    }

}
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
                taskCommands.summaryOfTasks(args);
                break;
            default:
                System.out.println("Invalid command. Please use a valid command.");
        }
    }

    //class encapsulating all the commands we want to allow
    public static class TaskCommands {
        Helpers helper = new Helpers();
        private static final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                System.out.println("Invalid size. " +
                        "Please use one of {S|M|L|XL}.");
                return;
            }

            String record = LocalDateTime.now().format(formatter) +
                    " DESCRIBE " + taskName + " "
                    + description + (size.isEmpty() ? "" : " " + size);
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

            String record = LocalDateTime.now().format(formatter) +
                    " RENAME " + oldTaskName + " " + newTaskName;
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

        public void summaryOfTasks(String[] args) {
            String filterTask = determineFilterTask(args);
            String filterSize = determineFilterSize(args);

            Map<String, List<String>> taskRecords = helper.loadTaskRecordsFromDataStore();
            Map<String, String> taskSizes = new HashMap<>();
            Map<String, Long> timeSpent = new HashMap<>();
            Map<String, String> latestTaskNames = new HashMap<>();
            Map<String, String> taskDescriptions = new HashMap<>();
            Set<String> deletedTasks = new HashSet<>();  // Initialize the set for deleted tasks

            processTaskRecords(taskRecords, taskSizes, timeSpent, latestTaskNames, taskDescriptions, deletedTasks);

            printTaskSummaries(timeSpent, taskSizes, taskDescriptions, filterTask, filterSize);
            printSizeBasedStatistics(timeSpent, taskSizes);
        }

        private String determineFilterTask(String[] args) {
            if (args.length > 1 && !helper.isValidSize(args[1])) {
                return args[1];
            }
            return null;
        }

        private String determineFilterSize(String[] args) {
            if (args.length > 1 && helper.isValidSize(args[1])) {
                return args[1];
            }
            return null;
        }

        private void processTaskRecords(Map<String, List<String>> taskRecords, Map<String, String> taskSizes,
                                        Map<String, Long> timeSpent, Map<String, String> latestTaskNames,
                                        Map<String, String> taskDescriptions, Set<String> deletedTasks) {

            for (String key : taskRecords.keySet()) {
                List<String> records = taskRecords.get(key);
                String currentName = key;

                for (String record : records) {
                    String[] parts = record.split(" ");
                    String command = parts[2];
                    String taskName = parts[3];

                    // Use the latest task name if it has been renamed
                    taskName = latestTaskNames.getOrDefault(taskName, taskName);

                    switch (command) {
                        case "RENAME":
                            String newName = parts[4];
                            latestTaskNames.put(taskName, newName);
                            if (deletedTasks.contains(taskName)) {
                                deletedTasks.add(newName);
                                deletedTasks.remove(taskName);
                            }
                            if (timeSpent.containsKey(taskName)) {
                                timeSpent.put(newName, timeSpent.get(taskName));
                                timeSpent.remove(taskName);
                            }
                            if (taskSizes.containsKey(taskName)) {
                                taskSizes.put(newName, taskSizes.get(taskName));
                                taskSizes.remove(taskName);
                            }
                            if (taskDescriptions.containsKey(taskName)) {
                                taskDescriptions.put(newName, taskDescriptions.get(taskName));
                                taskDescriptions.remove(taskName);
                            }
                            break;
                        case "SIZE":
                            String size = parts[4];
                            // Use the latest name for size assignment
                            String latestNameForSize = latestTaskNames.getOrDefault(taskName, taskName);
                            taskSizes.put(latestNameForSize, size);
                            break;
                        case "DELETE":
                            deletedTasks.add(taskName); // Mark this task as deleted
                            timeSpent.remove(taskName);
                            taskSizes.remove(taskName);
                            taskDescriptions.remove(taskName);
                            break;
                        case "DESCRIBE":
                            String description = String.join(" ",
                                    Arrays.copyOfRange(parts, 4, parts.length));

                            taskDescriptions.put(taskName, description);
                            break;
                        case "START":
                        case "STOP":
                            updateTaskTime(timeSpent, parts, currentName, command);
                            break;
                    }
                }
            }
        }
        private void printTaskSummaries(Map<String, Long> timeSpent, Map<String,
                String> taskSizes, Map<String, String> taskDescriptions,
                                        String filterTask, String filterSize) {

            long totalTimeSpent = 0;

            for (Map.Entry<String, Long> entry : timeSpent.entrySet()) {
                String task = entry.getKey();
                String size = taskSizes.getOrDefault(task, "");
                String description = taskDescriptions.getOrDefault(task, "No description");
                long timeSpentOnTask = entry.getValue();

                if ((filterTask != null && !filterTask.equals(task))
                        || (filterSize != null && !filterSize.equals(size))) {
                    continue;
                }
                System.out.println("Task: " + task + ", Size:" +
                        size + ", Time Spent: " + formatDuration(timeSpentOnTask) +
                        ", Description: " + description);
                totalTimeSpent += timeSpentOnTask;
            }

            System.out.println("Total time spent on tasks: " + formatDuration(totalTimeSpent));
        }

        private void printSizeBasedStatistics(Map<String, Long> timeSpent, Map<String, String> taskSizes) {
            Map<String, Long> sizeTotalTime = new HashMap<>();
            Map<String, Long> sizeMinTime = new HashMap<>();
            Map<String, Long> sizeMaxTime = new HashMap<>();
            Map<String, Integer> sizeTaskCount = new HashMap<>();

            for (Map.Entry<String, Long> entry : timeSpent.entrySet()) {
                String size = taskSizes.getOrDefault(entry.getKey(), "");
                long time = entry.getValue();

                sizeTotalTime.put(size, sizeTotalTime.getOrDefault(size, 0L) + time);
                sizeMinTime.put(size, Math.min(sizeMinTime.getOrDefault(size, Long.MAX_VALUE), time));
                sizeMaxTime.put(size, Math.max(sizeMaxTime.getOrDefault(size, Long.MIN_VALUE), time));
                sizeTaskCount.put(size, sizeTaskCount.getOrDefault(size, 0) + 1);
            }

            for (String size : new String[]{"S", "M", "L", "XL"}) {
                if (sizeTaskCount.getOrDefault(size, 0) >= 2) {
                    long total = sizeTotalTime.get(size);
                    long min = sizeMinTime.get(size);
                    long max = sizeMaxTime.get(size);
                    double avg = (double) total / sizeTaskCount.get(size);
                    System.out.println("Size " + size + " - Min: " + formatDuration(min) +
                            ", Max: " + formatDuration(max) + ", Avg: " + formatDuration((long) avg));
                }
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
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter("datastore.txt", true))) {
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
                        String taskName = parts[3];
                        boolean isStart = parts[2].equalsIgnoreCase("START");
                        taskStatus.put(taskName, isStart);

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
                    // Split the line into date-time and the rest
                    String[] parts = line.split(" ", 3); // Split into 3 parts: date, time, and the rest
                    if (parts.length < 3) {
                        continue; // Skip lines that don't have at least 3 parts
                    }
                    String commandLine = parts[2]; // The rest of the line containing the command and other details
                    String[] commandParts = commandLine.split(" "); // Split the command line into parts
                    if (commandParts.length < 2) {
                        continue; // Skip lines that don't have a command and task name
                    }
                    String command = commandParts[0];
                    String taskName = commandParts[1];

                    taskRecords.computeIfAbsent(taskName, k -> new ArrayList<>()).add(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading data store: " + e.getMessage());
            }
            return taskRecords;
        }
    }

}
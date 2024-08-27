package main.java;
import exception.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CasperBot {
    private enum CommandType {
        CREATE, TASK;
    }
    private enum CreateCommand {
        EVENT, TODO, DEADLINE;
    }

    private enum TaskCommand {
        MARK, UNMARK, DELETE;
    }
    private static List<Task> list = new ArrayList<>();
    public static void main(String[] args) throws CasperBotException {
        openFile();
        line();
        System.out.println("Hello! I'm CasperBot.\n" +
                "What can I do for you?");
        line();
        echo();
    }

    private static void line() {
        System.out.println("------------------------------------------");
    }

    private static void echo() throws CasperBotException {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (!input.equalsIgnoreCase("bye")) {
            input = scanner.nextLine();
            String[] inputArray = splitInputIntoTwo(input);
            line();
            try {
                if (inputArray[0].equalsIgnoreCase("list")) {
                    if (list.isEmpty()) {
                        System.out.println("You currently have no tasks in your list!");
                    } else {
                        System.out.println("Here are the tasks in your list:");
                        for (int i = 0; i < list.size(); i++) {
                            System.out.printf("%d. %s%n", i + 1, list.get(i));
                        }
                    }
                }
                else if (isValidCommand(inputArray[0], CommandType.TASK)) {
                    try {
                        int index = Integer.parseInt(inputArray[1]) - 1;
                        if (index >= list.size()) {
                            throw new CasperBotOutOfBoundsException();
                        }
                        TaskCommand taskCommand = TaskCommand.valueOf(inputArray[0].trim().toUpperCase());
                        Task task = list.get(index);
                        switch (taskCommand) {
                            case MARK:
                                task.markAsDone();
                                System.out.println("Nice! I've marked this task as done:");
                                break;
                            case UNMARK:
                                task.markAsDone();
                                System.out.println("OK, I've marked this task as not done yet:");
                                break;
                            case DELETE:
                                list.remove(task);
                                System.out.println("Noted. I've removed this task:");
                                break;
                        }
                        System.out.println("  " + task);
                        if (taskCommand == TaskCommand.DELETE) {
                            printTaskListLength();
                        }
                    } catch (NumberFormatException e) {
                        throw new CasperBotNumberFormatException();
                    }
                }
                else if (isValidCommand(inputArray[0], CommandType.CREATE)) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    parseBySlash(inputArray[1], hashMap);
                    CreateCommand command = CreateCommand.valueOf(inputArray[0].trim().toUpperCase());
                    switch (command) {
                        case TODO:
                            String todoDescription = hashMap.get("description");
                            if (todoDescription.isEmpty()) {
                                throw new CasperBotMissingInputException("description", "ToDo");
                            }
                            ToDo newToDo = new ToDo(todoDescription, false);
                            list.add(newToDo);
                            System.out.println("Got it. I've added this task:");
                            System.out.println("  " + newToDo);
                            break;
                        case DEADLINE:
                            String deadlineDescription = hashMap.get("description");
                            if (deadlineDescription.isEmpty()) {
                                throw new CasperBotMissingInputException("description", "Deadline");
                            }
                            String deadline = hashMap.get("by");
                            if (deadline == null || deadline.isEmpty()) {
                                throw new CasperBotMissingInputException("/by", "Deadline");
                            }
                            Deadline newDeadline = new Deadline(deadlineDescription, false, deadline);
                            list.add(newDeadline);
                            System.out.println("Got it. I've added this task:");
                            System.out.println("  " + newDeadline);
                            break;
                        case EVENT:
                            String eventDescription = hashMap.get("description");
                            if (eventDescription.isEmpty()) {
                                throw new CasperBotMissingInputException("description", "Event");
                            }
                            String start = hashMap.get("from");
                            if (start == null || start.isEmpty()) {
                                throw new CasperBotMissingInputException("/from", "Event");
                            }
                            String end = hashMap.get("to");
                            if (end == null || end.isEmpty()) {
                                throw new CasperBotMissingInputException("/to", "Event");
                            }
                            Event newEvent = new Event(eventDescription, false, start, end);
                            list.add(newEvent);
                            System.out.println("Got it. I've added this task:");
                            System.out.println("  " + newEvent);
                            break;
                    }
                    printTaskListLength();
                } else if (inputArray[0].equalsIgnoreCase("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                } else {
                    throw new CasperBotInvalidCommandException();
                }
            } catch (CasperBotException e) {
                System.out.println(e.getMessage());
            } finally {
                line();
            }
        }
        scanner.close();
    }

    private static String[] splitInputIntoTwo(String input) {
        int firstSpaceIndex = input.indexOf(" ");

        // If there is no space, return the original string as the first element
        if (firstSpaceIndex == -1) {
            return new String[] { input, "" };
        }

        // Split the string into two parts
        String part1 = input.substring(0, firstSpaceIndex).trim();  // Before the first space
        String part2 = input.substring(firstSpaceIndex + 1).trim(); // After the first space

        return new String[] { part1, part2 };
    }

    private static void parseBySlash(String input, HashMap<String, String> hashMap) {
        String[] parsedInput = input.split("/");
        hashMap.put("description", parsedInput[0].trim());
        for (int i = 1; i < parsedInput.length; i++) {
            String[] inputPart = splitInputIntoTwo(parsedInput[i]);
            hashMap.put(inputPart[0].trim(), inputPart[1].trim());
        }
    }

    private static boolean isValidCommand(String command, CommandType commandType) {
        try {
            switch (commandType) {
                case CREATE -> {
                    CreateCommand.valueOf(command.trim().toUpperCase());
                }
                case TASK -> {
                    TaskCommand.valueOf(command.trim().toUpperCase());
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void printTaskListLength() {
        if (list.size() == 1) {
            System.out.println("Now you have 1 task in the list.");
        } else {
            System.out.printf("Now you have %d tasks in the list.\n", list.size());
        }
    }

    private static void openFile() throws CasperBotIOException {
        String filePath = "chatbot.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {  // Read each line until end of file
                String[] values = line.split("\\|");
                boolean isDone = Boolean.parseBoolean(values[1]);
                String description = values[2];
                switch (values[0]) {
                case "T":
                    list.add(new ToDo(description, isDone));
                    break;
                case "D":
                    String deadline = values[3];
                    list.add(new Deadline(description, isDone, deadline));
                    break;
                case "E":
                    String start = values[3];
                    String end = values[4];
                    list.add(new Event(description, isDone, start, end));
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            File file = new File(filePath);
            System.out.println("Created a new file in the directory!");
        } catch (IOException e) {
            throw new CasperBotIOException();
        }

    }
}

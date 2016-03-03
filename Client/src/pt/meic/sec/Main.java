package pt.meic.sec;

import java.util.Scanner;

import static java.lang.System.out;

public class Main {

    private static Client client;

    public static void main(String[] args) {
        displayCommands();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.next();
            handleCommand(s);
        }
    }

    private static void handleCommand(String s) {
        int index = s.indexOf(' ');
        if(index == -1){
            index = s.length();
        }
        String command = s.substring(0, index);
        switch (command){
            case "fs_init":
                client = new Client("localhost", 1111);
                client.init();
                break;
            case "fs_write":

                break;

            case "fs_read":

                break;
            default:
                out.println("Command not found");
                break;
        }
    }

    private static void displayCommands() {
        out.println("COMMANDS:");
        out.println("fs_init\nCreate new filesystem user\n");
        out.println("fs_write <pos> <size> <contents>\nWrite a file\n");
        out.println("fs_read <id> <pos> <size> <contents>\nRead a file\n");
    }
}

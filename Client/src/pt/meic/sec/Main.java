package pt.meic.sec;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class Main {

    private static Client client;

    public static void main(String[] args) {
        displayCommands();
        client = new Client(null, 64535);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            out.print(">");
            String s = scanner.nextLine();
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
            case "fs_list":
                listUsers();
                break;
            case "fs_init":
                init();
                break;
            case "fs_write":
                executeWrite(s);
                break;
            case "fs_read":
                executeRead(s);
                break;
            default:
                out.println("Command not found");
                break;
        }
    }

    private static void listUsers() {
        List<String> list = client.list();
        if(list != null) {
            for (String key : list) {
                out.println(key);
            }
        }else{
            out.println("Erro a obter lista de chaves");
        }


    }

    private static void init() {
        String clientFileId = null;
		try {
            clientFileId = client.init();
		} catch (DependabilityException e) {
			out.println("Dependability fault or attack: "+e.getMessage());
		}
        if(clientFileId != null) {
            out.println("File created with id: " + clientFileId);
        }else{
            out.println("Init error");
        }
    }

    private static void executeWrite(String s) {
        //fs_write <pos> <size> <contents>
        String[] tokens = s.split(" ");
        if(tokens.length != 4){
            out.println("Invalid parameters");
            return;
        }
        int position = Integer.parseInt(tokens[1]);
        int size = Integer.parseInt(tokens[2]);
        String contents = tokens[3];
        try {
			client.write(position, size, contents.getBytes());
		} catch (DependabilityException e) {
			out.println("Dependability fault or attack: "+e.getMessage());
		}
        out.println("Write success");
    }

    private static void executeRead(String s) {
        //fs_read <id> <pos> <size>
        String[] tokens = s.split(" ");
        if(tokens.length != 4){
            out.println("Invalid parameters");
            return;
        }
        String blockId = tokens[1];
        int position = Integer.parseInt(tokens[2]);
        int size = Integer.parseInt(tokens[3]);
        byte[] contents = null;
		try {
			contents = client.read(blockId, position, size);
			out.println(contents.length+" bytes read");
		} catch (DependabilityException e) {
			out.println("Dependability fault or attack: "+e.getMessage());
		}
        out.println(new String(contents));
    }

    private static void displayCommands() {
        out.println("COMMANDS:");
        out.println("fs_list\nList fs users\n");
        out.println("fs_init\nCreate new filesystem user\n");
        out.println("fs_write <pos> <size> <contents>\nWrite to file\n");
        out.println("fs_read <id> <pos> <size> \nRead file\n");
    }
}

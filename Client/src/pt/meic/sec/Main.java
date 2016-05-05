package pt.meic.sec;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class Main {

    private static Client client;

    public static void main(String[] args) {
        displayCommands();

        client = new Client("localhost", args);
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
        try {
            switch (command) {
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
        }catch (Throwable t){
            out.println("Error");
        }
    }

    private static void init() {
		try {
            client.init();
            out.println("Filesystem initialized.");
		} catch (DependabilityException e) {
			out.println("Dependability fault or attack: "+e.getMessage());
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
            out.println("Write success");
		} catch (DependabilityException e) {
			out.println("Dependability fault or attack: "+e.getMessage());
		}
    }

    private static void executeRead(String s) {
        //fs_read <id> <pos> <size>
        String[] tokens = s.split(" ");
        if(tokens.length != 4){
            out.println("Invalid parameters");
            return;
        }
        String publicKey = tokens[1];
        int position = Integer.parseInt(tokens[2]);
        int size = Integer.parseInt(tokens[3]);
        byte[] contents = null;
		try {
			contents = client.read(publicKey, position, size);
			out.println(contents.length+" bytes read");
		} catch (DependabilityException e) {
			out.println("Dependability fault or attack: "+e.getMessage());
		}
        out.println(new String(contents));
    }

    private static void listUsers() throws NoSuchAlgorithmException {
        List<X509Certificate> list = null;
        try {
            list = client.list();
        } catch (DependabilityException e) {
            out.println(e.getMessage());
        }
        if(list != null) {
            if(list.isEmpty()){
                out.println("No users registered");
            }else {
                for (X509Certificate key : list) {
                    PublicKey publicKey = key.getPublicKey();
                    out.println(key.getSubjectDN().getName() + "\n" +SecurityUtils.byteToHex(publicKey.getEncoded()));
                    out.println();
                }
            }
        }else{
            out.println("Error obtaining file keys");
        }
    }

    private static void displayCommands() {
        out.println("COMMANDS:");
        out.println("fs_list\nList fs users\n");
        out.println("fs_init\nCreate new filesystem user\n");
        out.println("fs_write <pos> <size> <contents>\nWrite to file\n");
        out.println("fs_read <publicKey> <pos> <size> \nRead file\n");
    }
}

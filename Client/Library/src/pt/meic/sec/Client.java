/**
 * Created by Tiago on 29-02-2016.
 */
package pt.meic.sec;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;

public class Client {

    private static final String PUT_PUBLIC_KEY_BLOCK = "put_k";

    private final String hostname;
    private final int portNumber;

    private PublicKey publicKey;
    private Socket socket;
    private ObjectOutputStream socketStream;

    public Client(String hostname, int portNumber){
        this.hostname = hostname;
        this.portNumber = portNumber;

    }

    public void init() {
        KeyPair keyPair = generateKeyPair();
        publicKey = keyPair.getPublic();
        try {
            socket = new Socket(hostname, portNumber);
            socketStream = new ObjectOutputStream(socket.getOutputStream());
            writePublicKeyBlock(publicKey);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    private void writePublicKeyBlock(PublicKey publicKey) throws IOException {
        CommunicationParameters parameters = new CommunicationParameters(null, null, publicKey);
        socketStream.writeObject(PUT_PUBLIC_KEY_BLOCK);
        socketStream.writeObject(publicKey);
        socketStream.flush();
    }

    private KeyPair generateKeyPair() {
        // Create a key pair generator
        KeyPairGenerator keyGen;
        // Initialize the key pair generator
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            keyGen.initialize(1024, random);
            // Generate the key pair
            KeyPair pair = keyGen.generateKeyPair();
            return pair;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }



    }

}

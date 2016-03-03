/**
 * Created by Tiago on 29-02-2016.
 */
package pt.meic.sec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;



public class Client {

    private static final String PUT_PUBLIC_KEY_BLOCK = "put_k";

    private final String hostname;
    private final int portNumber;

    private KeyPair keyPair;
    private Socket socket;
    private ObjectInputStream socketInputStream;
    private ObjectOutputStream socketOutputStream;
    private String publicKeyBlockId;


    public Client(String hostname, int portNumber){
        this.hostname = hostname;
        this.portNumber = portNumber;
    }

    public void init() {
        try {
            keyPair = SecurityUtils.GenerateKeyPair();
            socket = new Socket(hostname, portNumber);
            socketInputStream = new ObjectInputStream(socket.getInputStream());
            socketOutputStream = new ObjectOutputStream(socket.getOutputStream());
            writePublicKeyBlock(keyPair.getPublic());
            publicKeyBlockId = (String)socketInputStream.readObject();
        } catch (NoSuchAlgorithmException | IOException |SignatureException | InvalidKeyException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void writePublicKeyBlock(PublicKey publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        socketOutputStream.writeObject(PUT_PUBLIC_KEY_BLOCK);
        byte[] data = publicKey.getEncoded();
        socketOutputStream.writeObject(data);
        socketOutputStream.writeObject(SecurityUtils.Sign(data, keyPair));
        socketOutputStream.writeObject(publicKey);
        socketOutputStream.flush();
    }

    public void write(int position, int size, String contents) {

    }

    public String read(int id, int position, int size) {

        return "";
    }
}

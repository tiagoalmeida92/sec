/**
 * Created by Tiago on 29-02-2016.
 */
package pt.meic.sec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;


public class Client {

    private static final String PUT_PUBLIC_KEY_BLOCK = "put_k";
    private static final String PUT_FILE_CONTENT_BLOCK = "put_h";
    private static final String READ_BLOCK = "get";

    private final String hostname;
    private final int portNumber;

    private KeyPair keyPair;
    private Socket socket;
    private ObjectInputStream socketInputStream;
    private ObjectOutputStream socketOutputStream;
    private String publicKeyBlockId;


    public Client(String hostname, int portNumber) {
        this.hostname = hostname;
        this.portNumber = portNumber;
    }

    public void init() {
        try {
            keyPair = SecurityUtils.GenerateKeyPair();
            socket = new Socket(hostname, portNumber);
            socketOutputStream = new ObjectOutputStream(socket.getOutputStream());
            socketInputStream = new ObjectInputStream(socket.getInputStream());
            writePublicKeyBlock(keyPair.getPublic(), "");
            publicKeyBlockId = (String) socketInputStream.readObject();
        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(int position, int size, String contents) {
        try {
            String contentBlockId = writeContentBlock(contents);
            byte[] headerBlock = readBlock(publicKeyBlockId);
            //headerBlockString is a comma separated list
            String headerBlockString = new String(headerBlock);
            int blockPosition = position / Constants.CBLOCKLENGTH;
            String[] ids = headerBlockString.split(",");
            if (blockPosition >= ids.length) {
                headerBlockString += "," + contentBlockId;
            }else{
                ids[blockPosition] = contentBlockId;
                headerBlockString = String.join(",", ids);
            }
            writePublicKeyBlock(keyPair.getPublic(), headerBlockString);

        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | ClassNotFoundException  ex) {
            throw new RuntimeException(ex);
        }
    }

    private String writeContentBlock(String contents) throws IOException, ClassNotFoundException {
        socketOutputStream.writeObject(PUT_FILE_CONTENT_BLOCK);
        socketOutputStream.writeObject(contents.getBytes());
        return (String) socketInputStream.readObject();
    }

    public byte[] read(String id, int position, int size) {
        try {
            byte[] bytes = readBlock(id);
            return bytes;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBlock(String id) throws IOException, ClassNotFoundException {
        socketOutputStream.writeObject(READ_BLOCK);
        socketOutputStream.writeObject(id);
        return (byte[]) socketInputStream.readObject();
    }

    private void writePublicKeyBlock(PublicKey publicKey, String content) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        socketOutputStream.writeObject(PUT_PUBLIC_KEY_BLOCK);
        byte[] data = content.getBytes();
        socketOutputStream.writeObject(data);
        byte[] signature = SecurityUtils.Sign(data, keyPair);
        socketOutputStream.writeObject(signature);
        socketOutputStream.writeObject(publicKey);
        socketOutputStream.flush();
    }


}

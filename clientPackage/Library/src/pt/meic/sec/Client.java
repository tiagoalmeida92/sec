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
            publicKeyBlockId = writePublicKeyBlock(keyPair.getPublic(), new String[0]);
            System.out.println("File created with id: "+publicKeyBlockId);
        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(int position, int size, byte[] contents) {
        try {
            String[] ids = getContentBlockReferences(publicKeyBlockId);

            int startIndex = position / Constants.CBLOCKLENGTH;
            int endIndex = startIndex + (size / Constants.CBLOCKLENGTH);

            if (endIndex >= ids.length) {
                ids = Utils.concat(ids, new String[endIndex - ids.length + 1]);
            }

            for (int i = startIndex, pos = 0; i <= endIndex && pos < size; i++, pos += Constants.CBLOCKLENGTH) {
                int posEnd = pos + Constants.CBLOCKLENGTH;
                byte[] contentChunk = Arrays.copyOfRange(contents, pos, posEnd);
                String contentBlockId = writeContentBlock(contentChunk);
                ids[i] = contentBlockId;
            }
            //TODO add signature to header block

            writePublicKeyBlock(keyPair.getPublic(), ids);

        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }


    //TODO missing calculation to cut beginning and ending of byte[]
    //Tip use modulus
    public byte[] read(String id, int position, int size) {
        try {
            String[] contentBlockIds = getContentBlockReferences(id);
            int startIndex = position / Constants.CBLOCKLENGTH;
            int endIndex = startIndex + (size / Constants.CBLOCKLENGTH);
            if (startIndex < contentBlockIds.length) {
                byte[] result = new byte[0];
                for (int i = startIndex; i < contentBlockIds.length && i <= endIndex; ++i) {
                    String blockHash = contentBlockIds[i];
                    byte[] bytes = readBlock(blockHash);
                    if (SecurityUtils.verifyHash(bytes, blockHash)) {
                        result = Utils.concat(result, bytes);
                    }

                }
                return result;
            }
            return null;
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException|SignatureException|InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBlock(String id) throws IOException, ClassNotFoundException {
        socketOutputStream.writeObject(READ_BLOCK);
        socketOutputStream.writeObject(id);
        return (byte[]) socketInputStream.readObject();
    }

    private String writeContentBlock(byte[] contents) throws IOException, ClassNotFoundException {
        socketOutputStream.writeObject(PUT_FILE_CONTENT_BLOCK);
        socketOutputStream.writeObject(contents);
        return (String) socketInputStream.readObject();
    }

    /*
                            PUBLIC KEY BLOCK STRUCTURE
             ---------------------------------------------------------
            |   SIGNATURE OF HASHES , CONTENT_HASH_1, CONTENT_HASH_2, etc |
             ---------------------------------------------------------
     */
    private String writePublicKeyBlock(PublicKey publicKey, String[] ids) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ClassNotFoundException {
        socketOutputStream.writeObject(PUT_PUBLIC_KEY_BLOCK);
        String content = String.join(",", ids);
        byte[] data = content.getBytes();
        socketOutputStream.writeObject(data);
        byte[] signature = SecurityUtils.Sign(data, keyPair);
        socketOutputStream.writeObject(signature);
        socketOutputStream.writeObject(publicKey);
        return (String) socketInputStream.readObject();
    }

    private String[] getContentBlockReferences(String publicKeyBlockId) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] pkBlock = readBlock(publicKeyBlockId);
        String pkBlockStr = new String(pkBlock);
        String[] ids = pkBlockStr.split(",");
        return ids;
//TODO
//        String signature = ids[0];
//        if(SecurityUtils.Verify(pkBlock, signature.getBytes(), publicKeyBlockId)){
//            return Arrays.copyOfRange(ids, 1, ids.length);
//        }
//        return new String [0];
    }


}

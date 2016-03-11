package pt.meic.sec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.*;


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
    
    //Public because of dependability testing
    public String publicKeyBlockId;


    public Client(String hostname, int portNumber) {
        this.hostname = hostname;
        this.portNumber = portNumber;
    }

    private void connectToServer() throws IOException {
        socket = new Socket(hostname, portNumber);
        socketOutputStream = new ObjectOutputStream(socket.getOutputStream());
        socketInputStream = new ObjectInputStream(socket.getInputStream());
    }
    
    public String init() throws DependabilityException {
        try {
            keyPair = SecurityUtils.GenerateKeyPair();
            connectToServer();
            publicKeyBlockId = writePublicKeyBlock(keyPair.getPublic(), new ArrayList<>());
            if(publicKeyBlockId.equals(SecurityUtils.Hash(keyPair.getPublic().getEncoded()))){
                return publicKeyBlockId;
            }else{
                return null;
            }

        } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(int position, int size, byte[] contents) throws DependabilityException {
        try {
            List<String> ids = getContentBlockReferences(publicKeyBlockId);

            int startIndex = position / Constants.CBLOCKLENGTH;
            int endIndex = startIndex + (size / Constants.CBLOCKLENGTH);

            //Pad file with 0s
            if(endIndex >= ids.size()){
                String contentBlockId = writeContentBlock(new byte[0]);
                while(endIndex>= ids.size()){
                    ids.add(contentBlockId);
                }
            }

            int contentsIdx = 0;
            int blockIdx = position % Constants.CBLOCKLENGTH;
             for (int i = startIndex; i <= endIndex; i++) {
                byte[] block = readBlock(ids.get(i));

                while ((blockIdx < block.length && contentsIdx < contents.length)){
                    block[blockIdx++] = contents[contentsIdx++];
                }
                String contentBlockId = writeContentBlock(block);
                if(!contentBlockId.equals(SecurityUtils.Hash(block)))
                	throw new DependabilityException(Constants.TAMPEREDWITHCONTENTBLOCKEXCEPTIONMESSAGE);
                ids.set(i, contentBlockId);
                blockIdx = 0;
            }
            
            writePublicKeyBlock(keyPair.getPublic(), ids);

        } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }


    public byte[] read(String id, int position, int readSize) throws DependabilityException {
        try {
            List<String> contentBlockIds = getContentBlockReferences(id);
            int startIndex = position / Constants.CBLOCKLENGTH;
            int endIndex = startIndex + (readSize / Constants.CBLOCKLENGTH);
            if (startIndex < contentBlockIds.size()) {
                byte[] result = new byte[0];
                for (int i = startIndex; i < contentBlockIds.size() && i <= endIndex; ++i) {
                    String blockHash = contentBlockIds.get(i);
                    byte[] bytes = readBlock(blockHash);
                    if (SecurityUtils.verifyHash(bytes, blockHash)) {
                        result = Utils.concat(result, bytes);
                    }
                }
                //Check for weird position cases
                int modulus = position % Constants.CBLOCKLENGTH;
                if(modulus != 0 ){
                    if(modulus <= result.length){
                        result = Arrays.copyOfRange(result, modulus, result.length);
                    }else{
                        result = new byte[0];
                    }
                }
                //last block is to be cut
                if(result.length > readSize){
                    result = Arrays.copyOfRange(result, 0, readSize);
                }
                return result;
            }
            return null;
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBlock(String id) throws IOException, ClassNotFoundException {
    	connectToServer();
        socketOutputStream.writeObject(READ_BLOCK);
        socketOutputStream.writeObject(id);
        return (byte[]) socketInputStream.readObject();
    }

    private String writeContentBlock(byte[] contents) throws IOException, ClassNotFoundException {
    	connectToServer();
    	socketOutputStream.writeObject(PUT_FILE_CONTENT_BLOCK);
        socketOutputStream.writeObject(Arrays.copyOf(contents, Constants.CBLOCKLENGTH));
        return (String) socketInputStream.readObject();
    }

    /*
                            PUBLIC KEY BLOCK STRUCTURE
             ---------------------------------------------------------
            |   BLOCK SIGNATURE 128 bytes
            |   PUBLIC KEY 162 bytes
            |   BLOCK IDS size is multiple of 64 bytes
             ---------------------------------------------------------
     */
    private String writePublicKeyBlock(PublicKey publicKey, List<String> ids) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, DependabilityException {
    	connectToServer();
    	socketOutputStream.writeObject(PUT_PUBLIC_KEY_BLOCK);
        byte[] data;
        byte[] publicKeyBytes = publicKey.getEncoded();
        if(ids.size() == 0){
            data = publicKeyBytes;
        }else{
            data = Utils.concat(publicKeyBytes, String.join("", ids).getBytes());
        }

        byte[] signature;
		try {
			signature = SecurityUtils.Sign(data, keyPair);
	
	        byte[] pkBlock = Utils.concat(signature, data);
	        socketOutputStream.writeObject(pkBlock);
	
	        socketOutputStream.writeObject(SecurityUtils.Sign(pkBlock, keyPair));
	        socketOutputStream.writeObject(publicKey);
	        String id = (String) socketInputStream.readObject();
	        if(id.indexOf("[Integrity]") != -1)
	        	throw new DependabilityException(id);
	        
	        return id;
		} catch (InvalidKeyException e) {
			throw new DependabilityException(Constants.TAMPEREDAKEYEXCEPTIONMESSAGE);
		} catch (SignatureException e) {
			throw new DependabilityException(Constants.TAMPEREDSIGNATUREEXCEPTIONMESSAGE);
		}
    }

    private List<String> getContentBlockReferences(String publicKeyBlockId) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, DependabilityException {
        byte[] pkBlock = readBlock(publicKeyBlockId);
        if(pkBlock == null)
        	throw new DependabilityException("Block not found OR " + Constants.TAMPEREDAKEYEXCEPTIONMESSAGE);
        byte[] signature = Arrays.copyOfRange(pkBlock, 0, Constants.SIGNATURE_SIZE);
        int publicKeyEndPos = Constants.SIGNATURE_SIZE + Constants.PUBLIC_KEY_SIZE;
        byte[] publicKeyBytes = Arrays.copyOfRange(pkBlock, Constants.SIGNATURE_SIZE, publicKeyEndPos);
        PublicKey key = SecurityUtils.getKey(publicKeyBytes);

        List<String> idsList = new ArrayList<>();
        try {
			if(SecurityUtils.verifyHash(key.getEncoded(), publicKeyBlockId)
			        && SecurityUtils.Verify(Arrays.copyOfRange(pkBlock, Constants.SIGNATURE_SIZE, pkBlock.length), signature, key)){
			    byte[] ids = Arrays.copyOfRange(pkBlock, publicKeyEndPos, pkBlock.length);

			    for (int i = 0; i < ids.length; i+= Constants.BLOCK_HASH_SIZE) {
			        String blockId = new String(Arrays.copyOfRange(ids, i, i+ Constants.BLOCK_HASH_SIZE));
			        idsList.add(blockId);
			    }
			}
		} catch (InvalidKeyException e) {
			throw new DependabilityException(Constants.TAMPEREDAKEYEXCEPTIONMESSAGE);
		} catch (SignatureException e) {
			throw new DependabilityException(Constants.TAMPEREDSIGNATUREEXCEPTIONMESSAGE);
		}
        return idsList;
    }


}

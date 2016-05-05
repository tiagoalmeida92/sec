package pt.meic.sec;

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublicKeyBlock {

    /*
                PUBLIC KEY BLOCK STRUCTURE
         -----------------------------------------------
        |   BLOCK SIGNATURE 128 bytes                    |
        |   PUBLIC KEY 162 bytes                         |
        |   TIMESTAMP  4 bytes                          |
        |   BLOCK IDS size is multiple of 64 bytes       |
         -----------------------------------------------
    */

    private byte[] signature;
    public final PublicKey publicKey;
    public final int timestamp;
    public final List<String> contentBlocks;
    private byte[] bytes;

    public PublicKeyBlock(byte[] signature, PublicKey publicKey, int timestamp, List<String> contentBlocks, byte[] bytes) throws NoSuchAlgorithmException, DependabilityException {
        this.signature = signature;
        this.publicKey = publicKey;
        this.timestamp = timestamp;
        this.contentBlocks = contentBlocks;
        this.bytes = bytes;
    }

    public PublicKeyBlock(PublicKey publicKey, int timestamp, List<String> contentBlocks) throws NoSuchAlgorithmException, DependabilityException {
        this.publicKey = publicKey;
        this.timestamp = timestamp;
        this.contentBlocks = contentBlocks;
    }


    public byte[] toBytes(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] timestampBytes = Utils.intToBytes(timestamp);
        byte[] data = Utils.concat(publicKeyBytes, timestampBytes);
        if (!contentBlocks.isEmpty()) {
            data = Utils.concat(data, String.join("", contentBlocks).getBytes());
        }
        signature = SecurityUtils.Sign(data, privateKey);
        bytes = Utils.concat(signature, data);
        return bytes;
    }

    public boolean verifySignature(String publicKeyId) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if(!SecurityUtils.verifyHash(publicKey.getEncoded(), publicKeyId) ||
                !SecurityUtils.Verify(Arrays.copyOfRange(bytes, Constants.SIGNATURE_SIZE, bytes.length), signature, publicKey)){
            return false;
        }
        return true;
    }

    public static PublicKeyBlock createFromBytes(byte[] pkBlockBytes) throws DependabilityException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        byte[] signature = Arrays.copyOfRange(pkBlockBytes, 0, Constants.SIGNATURE_SIZE);

        int publicKeyEndPos = Constants.SIGNATURE_SIZE + Constants.PUBLIC_KEY_SIZE;
        byte[] publicKeyBytes = Arrays.copyOfRange(pkBlockBytes, Constants.SIGNATURE_SIZE, publicKeyEndPos);
        PublicKey key = SecurityUtils.getKey(publicKeyBytes);

        byte[] timestampBytes = Arrays.copyOfRange(pkBlockBytes, publicKeyEndPos, publicKeyEndPos + Constants.TIME_STAMP_SIZE);
        int timestamp = Utils.bytesToInt(timestampBytes);

        ArrayList<String> contentBlocks = new ArrayList<>();

        int timestampEndPos = publicKeyEndPos + Constants.TIME_STAMP_SIZE;
        byte[] ids = Arrays.copyOfRange(pkBlockBytes, timestampEndPos, pkBlockBytes.length);

        for (int i = 0; i < ids.length; i += Constants.BLOCK_HASH_SIZE) {
            String blockId = new String(Arrays.copyOfRange(ids, i, i + Constants.BLOCK_HASH_SIZE));
            contentBlocks.add(blockId);
        }
        return new PublicKeyBlock(signature, key, timestamp, contentBlocks, pkBlockBytes);
    }

}

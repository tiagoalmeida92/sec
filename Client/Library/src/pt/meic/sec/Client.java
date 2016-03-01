/**
 * Created by Tiago on 29-02-2016.
 */
package pt.meic.sec;

import java.security.*;

public class Client {

    private PublicKey publicKey;

    public void init() {
        KeyPair keyPair = generateKeyPair();
        publicKey = keyPair.getPublic();

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

package pt.meic.sec;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.util.*;


public class Client {

    private static final String PUT_PUBLIC_KEY_BLOCK = "put_k";
    private static final String PUT_FILE_CONTENT_BLOCK = "put_h";
    private static final String READ_BLOCK = "get";
    private static final String READ_PUBLIC_KEYS = "readPubKeys";
    private static final String STORE_PUBLIC_KEY = "storePubKey";
    private static final int INITIAL_TIMESTAMP = 1;
    public static final int MAX_FAULTS = 2;

    private final ByzantineRegularRegister byzantineRegularRegister;
    private final List<Integer> replicas;

    //Public because of dependability testing
    public String publicKeyBlockId;
    private X509Certificate certificate;
    private PrivateKey privateKey;


    public Client(List<Integer> blockServerPorts, int maxFaults) {
        replicas = blockServerPorts;
        byzantineRegularRegister = new ByzantineRegularRegister(blockServerPorts, maxFaults);
    }

    public void init() throws DependabilityException {
        try {

            if (certificate != null) return;
            createSelfCertificate();
            PublicKeyBlock publicKeyBlock = new PublicKeyBlock(certificate.getPublicKey(), INITIAL_TIMESTAMP, new ArrayList<>());
            publicKeyBlockId = writePublicKeyBlock(publicKeyBlock);
            System.out.println("User created with id " + publicKeyBlockId);
            if (!publicKeyBlockId.equals(SecurityUtils.Hash(certificate.getPublicKey().getEncoded()))) {
                throw new DependabilityException(Constants.CERTIFICATE_TAMPERED);
            }
        } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException
                | NoSuchProviderException | SignatureException | CertificateException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(int position, int size, byte[] contents) throws DependabilityException {
        try {
            if (publicKeyBlockId == null) {
                throw new DependabilityException("Filesystem is not initialized");
            }
            PublicKeyBlock publicKeyBlock = getPublicKeyBlock(publicKeyBlockId);

            int startIndex = position / Constants.CBLOCKLENGTH;
            int endIndex = startIndex + (size / Constants.CBLOCKLENGTH);

            List<String> contentBlocks = publicKeyBlock.contentBlocks;

            //Pad file with 0s
            if (endIndex >= contentBlocks.size()) {
                String contentBlockId = writeContentBlock(new byte[Constants.CBLOCKLENGTH]);
                while (endIndex >= contentBlocks.size()) {
                    contentBlocks.add(contentBlockId);
                }
            }

            int contentsIdx = 0;
            int blockIdx = position % Constants.CBLOCKLENGTH;
            for (int i = startIndex; i <= endIndex; i++) {
                byte[] block = readBlock(ReadType.ContentBlock, contentBlocks.get(i));

                while ((blockIdx < block.length && contentsIdx < contents.length)) {
                    block[blockIdx++] = contents[contentsIdx++];
                }
                String contentBlockId = writeContentBlock(block);
                if (!contentBlockId.equals(SecurityUtils.Hash(block)))
                    throw new DependabilityException(Constants.TAMPEREDWITHCONTENTBLOCKEXCEPTIONMESSAGE);
                contentBlocks.set(i, contentBlockId);
                blockIdx = 0;
            }
            PublicKeyBlock newPublicKeyBlock = new PublicKeyBlock(publicKeyBlock.publicKey, publicKeyBlock.timestamp + 1, contentBlocks);
            writePublicKeyBlock(newPublicKeyBlock);

        } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException |SignatureException | InvalidKeyException ex) {
            throw new RuntimeException(ex);
        }
    }


    public byte[] read(String id, int position, int readSize) throws DependabilityException {
        try {
            PublicKeyBlock publicKeyBlock = getPublicKeyBlock(id);
            List<String> contentBlockIds = publicKeyBlock.contentBlocks;
            int startIndex = position / Constants.CBLOCKLENGTH;
            int endIndex = startIndex + (readSize / Constants.CBLOCKLENGTH);
            if (startIndex < contentBlockIds.size()) {
                byte[] result = new byte[0];
                for (int i = startIndex; i < contentBlockIds.size() && i <= endIndex; ++i) {
                    String blockHash = contentBlockIds.get(i);
                    byte[] bytes = readBlock(ReadType.ContentBlock, blockHash);
                    if (SecurityUtils.verifyHash(bytes, blockHash)) {
                        result = Utils.concat(result, bytes);
                    }
                }
                //Check for weird position cases
                int modulus = position % Constants.CBLOCKLENGTH;
                if (modulus != 0) {
                    if (modulus <= result.length) {
                        result = Arrays.copyOfRange(result, modulus, result.length);
                    } else {
                        result = new byte[0];
                    }
                }
                //last block is to be cut
                if (result.length > readSize) {
                    result = Arrays.copyOfRange(result, 0, readSize);
                }
                return result;
            }
            return null;
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBlock(ReadType readType, String id) throws IOException, ClassNotFoundException {
        byte[] readResult = byzantineRegularRegister.read(readType, id);
        return readResult;
    }

    private String writeContentBlock(byte[] contents) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String header = PUT_FILE_CONTENT_BLOCK;
        String hash = SecurityUtils.Hash(contents);
        contents = (header + Constants.DELIMITER + SecurityUtils.byteToHex(contents)).getBytes();
        byte[] write = byzantineRegularRegister.write(header, contents, hash, 0);
        return write != null ? new String(write) : null;
    }


    private String writePublicKeyBlock(PublicKeyBlock publicKeyBlock) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, DependabilityException, SignatureException, InvalidKeyException {
        String header = PUT_PUBLIC_KEY_BLOCK;
        byte[] publicKeyBlockBytes = publicKeyBlock.toBytes(privateKey);
        byte[] signature = SecurityUtils.Sign(publicKeyBlockBytes, privateKey);
        String contents = header + Constants.DELIMITER
                +  SecurityUtils.byteToHex(publicKeyBlockBytes) + Constants.DELIMITER
                +  SecurityUtils.byteToHex(signature) + Constants.DELIMITER
                + SecurityUtils.byteToHex(certificate.getPublicKey().getEncoded());

        byte[] write = byzantineRegularRegister.write(header, contents.getBytes(), null, publicKeyBlock.timestamp);
        String id = new String(write);
        if (id.contains("[Integrity]"))
            throw new DependabilityException(id);
        return id;
    }

    private PublicKeyBlock getPublicKeyBlock(String publicKeyBlockId) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, DependabilityException, SignatureException, InvalidKeyException {
        byte[] pkBlockBytes = readBlock(ReadType.PublicKeyBlock, publicKeyBlockId);
        if (pkBlockBytes == null) {
            throw new DependabilityException(Constants.TAMPERED_KEY_MESSAGE);
        }
        PublicKeyBlock publicKeyBlock = PublicKeyBlock.createFromBytes(pkBlockBytes);

        return publicKeyBlock;
    }

    private String registerCertificate(X509Certificate certificate) {
        try {
            Socket socket = new Socket((String)null, replicas.get(0));
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(STORE_PUBLIC_KEY.getBytes());
            outputStream.writeObject(certificate);
            String result = (String) inputStream.readObject();
            return result;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public List<X509Certificate> list() throws DependabilityException {
//        try {
//            Socket socket = new Socket((String)null, replicas.get(0));
//            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
//            outputStream.writeObject(READ_PUBLIC_KEYS);
//            List<String> result = (List<String>) inputStream.readObject();
//            if (result == null || result.size() < 2) {
//                return new ArrayList<>();
//            }
//            String hash = result.get(0);
//            result.remove(0);
//            byte[] data = Utils.toByteArray(result);
//            boolean valid = SecurityUtils.verifyHash(data, hash);
//            if (!valid) {
//                throw new DependabilityException("Certificates Tampered");
//            }
//            List<X509Certificate> certificates = new ArrayList<>();
//            CertificateFactory factory = CertificateFactory.getInstance("X.509");
//            for (String certHexa : result) {
//                byte[] certBytes = SecurityUtils.hexStringToByteArray(certHexa);
//                X509Certificate certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
//                certificates.add(certificate);
//            }
//            return certificates;
//        } catch (IOException | ClassNotFoundException e) {
//            return new ArrayList<>();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        } catch (CertificateException e) {
//            e.printStackTrace();
            return new ArrayList<>();
//        }
    }

    private void createSelfCertificate() throws NoSuchProviderException, NoSuchAlgorithmException, IOException, InvalidKeyException, CertificateException, SignatureException {

        CertAndKeyGen keypair = new CertAndKeyGen("RSA", "SHA1WithRSA", null);

        String username = System.getProperty("user.name");
        X500Name x500Name = new X500Name(username, "Tecnico", "SEC", "Porto Salvo", "Oeiras", "Portugal");

        keypair.generate(1024);

        certificate = keypair.getSelfCertificate(x500Name, 60 * 60 * 24 * 365);
        privateKey = keypair.getPrivateKey();
    }

}

package Utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.*;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import sun.security.x509.X509CertImpl;

/*
 * Reference to the certificates methods 
 * http://www.nakov.com/blog/2009/12/01/x509-certificate-validation-in-java-build-and-verify-chain-and-verify-clr-with-bouncy-castle/
 * 
 */
public class Security {

    public static KeyPair GenerateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        return kpg.genKeyPair();
    }

    public static boolean Verify(byte[] data, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    public static byte[] Sign(byte[] data, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(data);
        return sig.sign();
    }

    public static String GetPublicKeyHash(PublicKey publicK) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(publicK.getEncoded());
        return ByteToHex(md.digest());
    }

    public static String Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return ByteToHex(md.digest());
    }

    public static PublicKey GetKeyByBytes(byte[] publicKeyBytes) {
        try {
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String ByteToHex(final byte[] byteArr) {
        Formatter formatter = new Formatter();
        for (byte b : byteArr) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static KeyPair GetKeyPair(String username, String password) {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            String path = Files.GetPath(username + Constants.KEYSTOREEXTENSION);
            if (path != null) {
                FileInputStream fis = new FileInputStream(path);
                ks.load(fis, password.toCharArray());
                fis.close();

                //Get private key
                ProtectionParameter paramPassword =
                        new KeyStore.PasswordProtection(password.toCharArray());
                KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                        ks.getEntry("certSelfSignedAlias", paramPassword);
                PrivateKey privateKey = pkEntry.getPrivateKey();
                //Get public key
                PublicKey publicKey = ks.getCertificate("certSelfSignedAlias").getPublicKey();
                return new KeyPair(publicKey, privateKey);
            } else
                return null;
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | CertificateException
                | IOException
                | UnrecoverableEntryException e) {
            return null;
        }
    }

    public static boolean CreateKeyStore(String username, String password) {
        Process p = null;
        char[] charArrayPassword = password.toCharArray();

        //Generate self-signed certificate
        ArrayList<String> command = new ArrayList<String>();
        command.add("cmd");
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            p = pb.start();

            BufferedWriter stdOutput = new BufferedWriter(new
                    OutputStreamWriter(p.getOutputStream()));
            stdOutput.write("keytool -genkey -alias certSelfSignedAlias -keyalg RSA -validity 365 -keystore " + username + Constants.KEYSTOREEXTENSION);
            stdOutput.newLine();
            stdOutput.write(password);
            stdOutput.newLine();
            stdOutput.write(password);
            stdOutput.newLine();
            stdOutput.write(username);
            stdOutput.newLine();
            stdOutput.newLine();
            stdOutput.newLine();
            stdOutput.newLine();
            stdOutput.newLine();
            stdOutput.newLine();
            stdOutput.write("yes");
            stdOutput.newLine();
            stdOutput.newLine();
            stdOutput.close();
            return Files.Exists(username + Constants.KEYSTOREEXTENSION);
        } catch (IOException e) {
            return false;
        }
    }

    public static PublicKey getKey(byte[] publicKeyBytes) {
        try {
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Set<X509Certificate> GetCACertificates(String... paths) throws CertificateException, FileNotFoundException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Set<X509Certificate> certificates = new HashSet<X509Certificate>();
        for (String path : paths) {
            FileInputStream in = new FileInputStream(path);
            certificates.add((X509Certificate) cf.generateCertificate(in));
        }
        return certificates;
    }

    /*
     * S2
     */
    public static boolean VerifyCertificate(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException, FileNotFoundException {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            Set<X509Certificate> CAs = GetCACertificates(Constants.CCCA1,
            		Constants.CCCA2,Constants.CCCA3,Constants.CCCA4,Constants.CCCA5,
            		Constants.CCCA6,Constants.CCCA7,Constants.CCCA8,Constants.CCCA9,
            		Constants.CCCA10);
            Set<TrustAnchor> trustAnchors = new HashSet<>();
            for(X509Certificate trust : CAs)
            {
            	if(cert.getIssuerDN().getName().equals(trust.getSubjectDN().getName())){
	                cert.verify(trust.getPublicKey());
	
	                /* Construct a CertPathBuilder */
	                TrustAnchor anchor = new TrustAnchor(trust, null);                
	                trustAnchors.add(anchor);
            	}
            }
            
            X509CertSelector certSelector = new X509CertSelector();
            certSelector.setCertificate(cert);


            PKIXBuilderParameters params = new PKIXBuilderParameters(trustAnchors, certSelector);
            CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");

            /* Enable usage of revocation lists */
            PKIXRevocationChecker rc = (PKIXRevocationChecker) cpb.getRevocationChecker();
            rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.PREFER_CRLS));
            params.addCertPathChecker(rc);


            CertPathBuilderResult cpbr = cpb.build(params);

            /* Now Validate the Certificate Path */

            CertPath cp = cpbr.getCertPath();
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            CertPathValidatorResult cpvr = cpv.validate(cp, params);
            return true;

        } catch (Throwable e) {
            System.out.println("[Catch] Exception: " + e.getMessage());
            return false;

        }


//		Set<X509Certificate> additionalCerts =
//				GetCACertificates(Constants.CCCA1, Constants.CCCA2, Constants.CCCA3);
//
//		// Check for self-signed certificate
//        if (IsSelfSigned(cert))
//        	return false;
//
//        // Prepare a set of trusted root CA certificates
//        // and a set of intermediate certificates
//        Set<X509Certificate> trustedRootCerts = new HashSet<X509Certificate>();
//        Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
//        for (X509Certificate additionalCert : additionalCerts) {
//            if (IsSelfSigned(additionalCert)) {
//                trustedRootCerts.add(additionalCert);
//            } else {
//                intermediateCerts.add(additionalCert);
//            }
//        }
//
//        try{
//
//        	// Attempt to build the certification chain and verify it
//        	VerifyCertificate(cert, trustedRootCerts, intermediateCerts);
//
//            // Check whether the certificate is revoked by the CRL
//            // given in its CRL distribution point extension
//            return VerifyCertificateCRLs(cert,Constants.CCCRL1,
//            		Constants.CCCRL2, Constants.CCCRL3);
//
//        }catch(GeneralSecurityException | IOException ex){
//        	return false;
//        }
    }

    private static boolean VerifyCertificateCRLs(X509Certificate cert, String... crlsURLs)
            throws MalformedURLException, CertificateException, CRLException, IOException {
        for (String crlURL : crlsURLs) {
            X509CRL crl = DownloadCRLFromWeb(crlURL);
            if (crl.isRevoked(cert)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Downloads a CRL from given HTTP/HTTPS/FTP URL, e.g.
     * http://crl.infonotary.com/crl/identity-ca.crl
     */
    private static X509CRL DownloadCRLFromWeb(String crlURL)
            throws MalformedURLException, IOException, CertificateException,
            CRLException {
        URL url = new URL(crlURL);
        InputStream crlStream = url.openStream();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
            return crl;
        } finally {
            crlStream.close();
        }
    }

    /**
     * Checks whether given X.509 certificate is self-signed.
     */
    public static boolean IsSelfSigned(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException {
        try {
            // Try to verify certificate signature with its own public key
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException sigEx) {
            // Invalid signature --> not self-signed
            return false;
        } catch (InvalidKeyException keyEx) {
            // Invalid key --> not self-signed
            return false;
        }
    }

    /**
     * Attempts to build a certification chain for given certificate and to verify
     * it. Relies on a set of root CA certificates (trust anchors) and a set of
     * intermediate certificates (to be used as part of the chain).
     *
     * @param cert              - certificate for validation
     * @param trustedRootCerts  - set of trusted root CA certificates
     * @param intermediateCerts - set of intermediate certificates
     * @return the certification chain (if verification is successful)
     * @throws GeneralSecurityException - if the verification is not successful
     *                                  (e.g. certification path cannot be built or some certificate in the
     *                                  chain is expired)
     */
    private static PKIXCertPathBuilderResult VerifyCertificate(X509Certificate cert, Set<X509Certificate> trustedRootCerts,
                                                               Set<X509Certificate> intermediateCerts) throws GeneralSecurityException {

        // Create the selector that specifies the starting certificate
        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(cert);

        // Create the trust anchors (set of root CA certificates)
        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (X509Certificate trustedRootCert : trustedRootCerts) {
            trustAnchors.add(new TrustAnchor(trustedRootCert, null));
        }

        // Configure the PKIX certificate builder algorithm parameters
        PKIXBuilderParameters pkixParams =
                new PKIXBuilderParameters(trustAnchors, selector);

        // Disable CRL checks (this is done manually as additional step)
        pkixParams.setRevocationEnabled(false);

        // Specify a list of intermediate certificates
        CertStore intermediateCertStore = CertStore.getInstance("Collection",
                new CollectionCertStoreParameters(intermediateCerts), "BC");
        pkixParams.addCertStore(intermediateCertStore);

        // Build and verify the certification chain
        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");
        PKIXCertPathBuilderResult result =
                (PKIXCertPathBuilderResult) builder.build(pkixParams);
        return result;
    }
}

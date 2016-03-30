package pt.meic.sec;

import pteidlib.*;
import sun.security.pkcs11.wrapper.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Tiago on 28-03-2016.
 */
public class SmartCardSession {

    private final long p11_session;
    private PKCS11 pkcs11;

    public SmartCardSession() throws PteidException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, PKCS11Exception {
        System.loadLibrary("pteidlibj");

        pteid.Init(""); // Initializes the eID Lib
        pteid.SetSODChecking(false); // Don't check the integrity of the ID, address and photo (!)
        showInfo();
        String osName = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");
        String libName = "libbeidpkcs11.so";
        if (-1 != osName.indexOf("Windows"))
            libName = "pteidpkcs11.dll";
        else if (-1 != osName.indexOf("Mac"))
            libName = "pteidpkcs11.dylib";
        Class pkcs11Class = Class.forName("sun.security.pkcs11.wrapper.PKCS11");
        if (javaVersion.startsWith("1.5.")) {
            Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[]{String.class, CK_C_INITIALIZE_ARGS.class, boolean.class});
            pkcs11 = (PKCS11) getInstanceMethode.invoke(null, new Object[]{libName, null, false});
        } else {
            Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[]{String.class, String.class, CK_C_INITIALIZE_ARGS.class, boolean.class});
            pkcs11 = (PKCS11) getInstanceMethode.invoke(null, new Object[]{libName, "C_GetFunctionList", null, false});
        }
        p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);
        pkcs11.C_Login(p11_session, 1, null);
        CK_SESSION_INFO info = pkcs11.C_GetSessionInfo(p11_session);
    }

    public X509Certificate getCertificate(){
        X509Certificate cert = null;
        try {
            cert = getCertFromByteArray(getCertificateInBytes(0));
        } catch (CertificateException e) {
            return null;
        }
        return cert;
    }

    public void sign(byte[] data) throws PKCS11Exception {

        CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[1];
        attributes[0] = new CK_ATTRIBUTE();
        attributes[0].type = PKCS11Constants.CKA_CLASS;
        attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY);

        pkcs11.C_FindObjectsInit(p11_session, attributes);
        long[] keyHandles = pkcs11.C_FindObjects(p11_session, 5);

        // points to auth_key
        System.out.println("            //points to auth_key. No. of keys:" + keyHandles.length);

        long signatureKey = keyHandles[0];        //test with other keys to see what you get
        pkcs11.C_FindObjectsFinal(p11_session);
        CK_MECHANISM mechanism = new CK_MECHANISM();
        mechanism.mechanism = PKCS11Constants.CKM_SHA256_RSA_PKCS;
        mechanism.pParameter = null;
        pkcs11.C_SignInit(p11_session, mechanism, signatureKey);
    }

    public static void showInfo() {
        try {

            int cardtype = pteid.GetCardType();
            switch (cardtype) {
                case pteid.CARD_TYPE_IAS07:
                    System.out.println("IAS 0.7 card\n");
                    break;
                case pteid.CARD_TYPE_IAS101:
                    System.out.println("IAS 1.0.1 card\n");
                    break;
                case pteid.CARD_TYPE_ERR:
                    System.out.println("Unable to get the card type\n");
                    break;
                default:
                    System.out.println("Unknown card type\n");
            }

            // Read ID Data
            PTEID_ID idData = pteid.GetID();
            if (null != idData)
                PrintIDData(idData);


        } catch (PteidException e) {
            e.printStackTrace();
        }
    }

    private static void PrintIDData(PTEID_ID idData) {
        System.out.println("DeliveryEntity : " + idData.deliveryEntity);
        System.out.println("PAN : " + idData.cardNumberPAN);
        System.out.println("...");
    }


    //Returns the CITIZEN AUTHENTICATION CERTIFICATE
    public static byte[] getCitizenAuthCertInBytes() {
        return getCertificateInBytes(0); //certificado 0 no Cartao do Cidadao eh o de autenticacao
    }

    // Returns the n-th certificate, starting from 0
    private static byte[] getCertificateInBytes(int n) {
        byte[] certificate_bytes = null;
        try {
            PTEID_Certif[] certs = pteid.GetCertificates();
            System.out.println("Number of certs found: " + certs.length);
            int i = 0;
            for (PTEID_Certif cert : certs) {
                System.out.println("-------------------------------\nCertificate #" + (i++));
                System.out.println(cert.certifLabel);
            }

            certificate_bytes = certs[n].certif; //gets the byte[] with the n-th certif

            //pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD); // OBRIGATORIO Termina a eID Lib
        } catch (PteidException e) {
            e.printStackTrace();
        }
        return certificate_bytes;
    }

    public static X509Certificate getCertFromByteArray(byte[] certificateEncoded) throws CertificateException {
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(certificateEncoded);
        X509Certificate cert = (X509Certificate) f.generateCertificate(in);
        return cert;
    }

}

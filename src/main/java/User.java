import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class User {

    private static RSAPublicKeySpec rsaPublicKeySpec;
    private static RSAPrivateKeySpec rsaPrivateKeySpec;
    private static RSAPrivateCrtKey rsaPrivateCrtKey;

    public static void key() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        rsaPrivateCrtKey = createCrtKey(rsaPublicKeySpec, rsaPrivateKeySpec);
    }

    private static RSAPrivateCrtKey createCrtKey(RSAPublicKeySpec rsaPublicKeySpec, RSAPrivateKeySpec rsaPrivateKeySpec) throws NoSuchAlgorithmException, InvalidKeySpecException {

        BigInteger e = rsaPublicKeySpec.getPublicExponent();
        BigInteger d = rsaPrivateKeySpec.getPrivateExponent();
        BigInteger n = rsaPublicKeySpec.getModulus();
        BigInteger p = findFactor(e, d, n);
        BigInteger q = n.divide(p);

        if (p.compareTo(q) > 0) {
            BigInteger t = p;
            p = q;
            q = t;
        }

        BigInteger exp1 = d.mod(p.subtract(BigInteger.ONE));
        BigInteger exp2 = d.mod(q.subtract(BigInteger.ONE));
        BigInteger coefficient = q.modInverse(p);
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(n, e, d, p, q, exp1, exp2, coefficient);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return (RSAPrivateCrtKey) kf.generatePrivate(keySpec);
    }

    private static BigInteger findFactor(BigInteger e, BigInteger d, BigInteger n) {
        BigInteger edMinus1 = e.multiply(d).subtract(BigInteger.ONE);
        int s = edMinus1.getLowestSetBit();
        BigInteger t = edMinus1.shiftRight(s);

        BigInteger aPow;
        BigInteger aPowSquared;
        for (int aInt = 2; true; aInt++) {
            aPow = BigInteger.valueOf(aInt).modPow(t, n);
            for (int i = 1; i <= s; i++) {
                if (aPow.equals(BigInteger.ONE)) {
                    break;
                }

                if (aPow.equals(n.subtract(BigInteger.ONE))) {
                    break;
                }

                aPowSquared = aPow.multiply(aPow).mod(n);
                if (aPowSquared.equals(BigInteger.ONE)) {
                    return aPow.subtract(BigInteger.ONE).gcd(n);
                }

                aPow = aPowSquared;
            }
        }
    }

    public static void createUser(String name) throws ParserConfigurationException, TransformerException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        key();

        //create keys directory
        File keys = new File("c://keys");
        keys.mkdir();

        //create key files
        File filePub = new File(keys.getPath() + "//" + name + ".pub.xml");
        File file = new File(keys.getPath() + "//" + name + ".xml");

        if (file.exists() || filePub.exists()) {
            System.out.println("Wrong! " + name + " key exist.");
//            System.exit(1);
        }

        createUserTxt(name);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document docPub = writePublicKeyInDoc(rsaPublicKeySpec, docBuilder.newDocument());
        DOMSource sourcePub = convertTxtToXml(docPub);

        Document doc = writePrivateKeyInDoc(rsaPrivateCrtKey, docBuilder.newDocument());
        DOMSource source = convertTxtToXml(doc);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        System.out.println(file.getPath() + " private key is created.");

        StreamResult resultPub = new StreamResult(filePub);
        transformer.transform(sourcePub, resultPub);
        System.out.println(filePub.getPath() + " public key is created.");
    }

    public static void createUserTxt(String name) throws FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        //Create directory
        File directory = new File("c://U");
        directory.mkdir();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Password: ");
        String password = scanner.next();

        if (password.length() < 6) {
            System.out.println("Wrong! Password should contain at least six letters!");
//            System.exit(1);
        }

        String symbols = "0123456789!@#$%^&*()_+[];',./?><|:}{";
        int countSymbols = 0;
        for (int i = 0; i < password.toCharArray().length; i++) {
            for (int j = 0; j < symbols.toCharArray().length; j++) {
                if (((password.toCharArray())[i] == symbols.toCharArray()[j])) {
                    countSymbols++;
                }
            }
        }

        if (countSymbols == 0) {
            System.out.println("Wrong! Password should contain at least one number or symbol!");
//            System.exit(1);
        }

        System.out.print("Repeat password: ");
        String repeatPassword = scanner.next();
        scanner.close();

        if (!password.equals(repeatPassword)) {
            System.out.println("Wrong! Repeated password doesn't match the first password!");
//            System.exit(1);
        }

        String hash = generateStrongPasswordHash(password);
        File inputFile = new File(directory.getPath() + "//" + name + ".txt");
        PrintWriter pw = new PrintWriter(inputFile);

        pw.println(hash);
        System.out.println("User " + name + " is created.");
        pw.close();
    }

    private static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //It indicates how many iterations that this algorithm run for, increasing the time it takes to produce the hash.
        int iterations = 65536;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec pbeKeySpec = new PBEKeySpec(chars, salt, iterations, 128);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        Base64.Encoder encoder = Base64.getEncoder();

        return encoder.encodeToString(salt) + "." + encoder.encodeToString(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public static Document writePublicKeyInDoc(RSAPublicKeySpec rsaPublicKeySpec, Document docPub) {
        //Chinese remainder theorem coefficients for public key
        Element rootElementPub = docPub.createElement("RSAKeyValue");
        docPub.appendChild(rootElementPub);

        Base64.Encoder encoder = Base64.getEncoder();

        Element ModulusPub = docPub.createElement("Modulus");
        ModulusPub.appendChild(docPub.createTextNode(encoder.encodeToString(rsaPublicKeySpec.getModulus().toByteArray())));
        rootElementPub.appendChild(ModulusPub);

        Element ExponentPub = docPub.createElement("Exponent");
        ExponentPub.appendChild(docPub.createTextNode(encoder.encodeToString(rsaPublicKeySpec.getPublicExponent().toByteArray())));
        rootElementPub.appendChild(ExponentPub);

        return docPub;
    }

    public static Document writePrivateKeyInDoc(RSAPrivateCrtKey rsaPrivateCrtKey, Document doc) {
        //Chinese remainder theorem coefficients for private key
        Element rootElement = doc.createElement("RSAKeyValue");
        doc.appendChild(rootElement);

        Base64.Encoder encoder = Base64.getEncoder();

        Element Modulus = doc.createElement("Modulus");
        Modulus.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getModulus().toByteArray())));
        rootElement.appendChild(Modulus);

        Element Exponent = doc.createElement("Exponent");
        Exponent.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getPublicExponent().toByteArray())));
        rootElement.appendChild(Exponent);

        Element P = doc.createElement("P");
        P.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getPrimeP().toByteArray())));
        rootElement.appendChild(P);

        Element Q = doc.createElement("Q");
        Q.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getPrimeQ().toByteArray())));
        rootElement.appendChild(Q);

        Element DP = doc.createElement("DP");
        DP.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getPrimeExponentP().toByteArray())));
        rootElement.appendChild(DP);

        Element DQ = doc.createElement("DQ");
        DQ.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getPrimeExponentQ().toByteArray())));
        rootElement.appendChild(DQ);

        Element InverseQ = doc.createElement("InverseQ");
        InverseQ.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getCrtCoefficient().toByteArray())));
        rootElement.appendChild(InverseQ);

        Element D = doc.createElement("D");
        D.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivateCrtKey.getPrivateExponent().toByteArray())));
        rootElement.appendChild(D);

        return doc;
    }

    public static DOMSource convertTxtToXml(Document doc) throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return new DOMSource(doc);
    }

    public static void deleteUser(String name) {
        delete(name);

        File directory = new File("c://keys");
        directory.mkdir();

        File filePub = new File(directory.getPath() + "//" + name + ".pub.xml");
        File file = new File(directory.getPath() + "//" + name + ".xml");

        if (file.exists() && filePub.exists()) {
            if (filePub.delete() && file.delete()) {
                System.out.println(file.getPath() + " private key was deleted");
                System.out.println(filePub.getPath() + " public key was deleted");
            } else {
                System.out.println("Couldn't delete the keys");
            }
        } else if (filePub.exists()) {
            if (filePub.delete()) {
                System.out.println(filePub.getPath() + " public key was deleted");
            } else {
                System.out.println("Couldn't delete the key");
            }
        } else if (file.exists()) {
            if (file.delete()) {
                System.out.println(file.getPath() + " private key was deleted");
            } else {
                System.out.println("Couldn't delete the key");
            }
        } else {
            System.out.println("Wrong! The" + name + " keys doesn't exist!");
        }
    }

    public static void delete(String name) {
        File directory = new File("c://U");
        directory.mkdir();

        File file = new File(directory.getPath() + "//" + name + ".txt");

        if (!file.exists()) {
            System.out.println(name + " user doesn't exist");
            System.exit(1);
        }

        if (file.delete()) {
            System.out.println(name + " user was deleted");
        }
    }
}


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MessageWriter {
    public static void encrypt(String name, String message, String path, String token) throws ParserConfigurationException, SAXException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        Base64.Encoder encoder = Base64.getEncoder();
        String[] partsOfToken;
        String pl;
        JSONObject payload;
        String sender = null;
        RSAPrivateKeySpec SECRET_KEY = null;

        if (token.length() != 0) {
            //get user name
            partsOfToken = token.split("\\.");
            pl = new String(Base64.getUrlDecoder().decode(partsOfToken[1]));
            payload = new JSONObject(pl);
            sender = payload.getString("sub");

            //get user private key
            SECRET_KEY = getPublicKey(sender);
        }

        SecureRandom sr = new SecureRandom();
        byte[] IV = new byte[8];
        sr.nextBytes(IV);
        IvParameterSpec iv = new IvParameterSpec(IV);

        SecretKey key = KeyGenerator.getInstance("DES").generateKey();

        File keys = new File("c://keys");
        keys.mkdir();

        File fromFile = new File(keys.getPath() + "//" + name + ".pub.xml");

        if (!fromFile.exists()) {
            System.out.println("Wrong! The public key " + name + " doesn't exist!");
            System.exit(1);
        }

        String EncryptedMessage = encoder.encodeToString(encryptWithDes(message, iv, key));
        RSAPublicKeySpec PubKey = getPublicKey(fromFile);
        String EncryptedKey = encoder.encodeToString(encryptWithRsa(PubKey, key));
        String part1 = encoder.encodeToString(name.getBytes(StandardCharsets.UTF_8));
        String part2 = encoder.encodeToString(IV);
        String part5 = "";
        String part6 = "";

        if (token.length() != 0) {
            part5 = encoder.encodeToString(sender.getBytes(StandardCharsets.UTF_8));
            part6 = encoder.encodeToString(signatureWithRsa(SECRET_KEY, encryptWithDes(message, iv, key)));
        }

        if (path.contains(".txt")) {
            File file = new File(path);
            PrintWriter input = new PrintWriter(file);
            input.println(part1);               //name
            input.println(part2);               //DES iv
            input.println(EncryptedKey);        //DES key
            input.println(EncryptedMessage);    //message
            input.println(part5);               //sender
            input.println(part6);               //signature
            input.close();
            System.out.println("The encrypted message was saved in " + path + ".");
        } else {
            System.out.println(part1 + "." + part2 + "." + EncryptedKey + "." + EncryptedMessage + "." + part5 + "." + part6);
        }
    }

    private static byte[] encryptWithDes(String message, AlgorithmParameterSpec iv, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, ParserConfigurationException, SAXException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        //determine the algorithm and algorithm mode (encryption or decryption)
        Cipher encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, iv);

        //get bytes of message
        byte[] input = message.getBytes("UTF8");

        //encrypt data
        return encryptCipher.doFinal(input);
    }

    private static byte[] encryptWithRsa(RSAPublicKeySpec RSAPubKey, SecretKey desKey) throws InvalidKeyException, ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        //determine the algorithm and algorithm mode (encryption or decryption)
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //generate key
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(RSAPubKey);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        //get bytes of des key
        byte[] input = desKey.getEncoded();

        //populate algorithm with data
        cipher.update(input);

        //encrypt des key
        return cipher.doFinal();
    }

    private static byte[] signatureWithRsa(RSAPrivateKeySpec rsaPrivateKeySpec, byte[] desMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivateKeySpec);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        cipher.update(desMessage);

        return cipher.doFinal();
    }

    private static RSAPrivateKeySpec getPublicKey(String name) throws ParserConfigurationException, SAXException, IOException {
        File keys = new File("c://keys");
        keys.mkdir();

        File fromFile = new File(keys.getPath() + "//" + name + ".xml");
        if (!fromFile.exists()) {
            System.out.println("Wrong! The private key " + fromFile.getPath() + " doesn't exist!");
            System.exit(1);
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document fromDoc = dBuilder.parse(fromFile);

        String modulus = fromDoc.getElementsByTagName("Modulus").item(0).getTextContent();
        String exponent = fromDoc.getElementsByTagName("D").item(0).getTextContent();

        byte[] decodedModulus = Base64.getDecoder().decode(modulus.getBytes(StandardCharsets.UTF_8));
        byte[] decodedExponent = Base64.getDecoder().decode(exponent.getBytes(StandardCharsets.UTF_8));

        BigInteger Modulus = new BigInteger(decodedModulus);
        BigInteger D = new BigInteger(decodedExponent);

        return new RSAPrivateKeySpec(Modulus, D);
    }

    private static RSAPublicKeySpec getPublicKey(File fromFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document fromDoc = dBuilder.parse(fromFile);

        String modulus = fromDoc.getElementsByTagName("Modulus").item(0).getTextContent();
        String exponent = fromDoc.getElementsByTagName("Exponent").item(0).getTextContent();

        byte[] decodedModulus = Base64.getDecoder().decode(modulus.getBytes(StandardCharsets.UTF_8));
        byte[] decodedExponent = Base64.getDecoder().decode(exponent.getBytes(StandardCharsets.UTF_8));

        BigInteger Modulus = new BigInteger(decodedModulus);
        BigInteger Exponent = new BigInteger(decodedExponent);

        return new RSAPublicKeySpec(Modulus, Exponent);
    }
}

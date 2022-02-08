import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MessageReader {
    private static int a;

    public static void decrypt(String encryptedMessage) throws ParserConfigurationException, SAXException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        byte[] decodedNameByte;
        String name;
        byte[] decodedIvByte;
        byte[] decodedEncryptedKeyByte;
        byte[] decodedEncryptedMessageByte;
        byte[] decodedSenderByte;
        String sender = null;
        byte[] decodedSignatureByte = new byte[0];
        RSAPublicKeySpec rsaPublicKeySpec;

        if (encryptedMessage.contains(".txt")) {
            File file = new File(encryptedMessage);
            if (!file.exists()) {
                System.out.println("Wrong! The given file doesn't exist!");
                System.exit(1);
            }

            Scanner scanner = new Scanner(file);
            ArrayList<String> partsOfEncryptedMessage = new ArrayList<>();
            while (scanner.hasNextLine()) {
                partsOfEncryptedMessage.add(scanner.nextLine());
            }
            scanner.close();

            decodedNameByte = Base64.getDecoder().decode(partsOfEncryptedMessage.get(0));
            name = new String(decodedNameByte);
            decodedIvByte = Base64.getDecoder().decode(partsOfEncryptedMessage.get(1));
            decodedEncryptedKeyByte = Base64.getDecoder().decode(partsOfEncryptedMessage.get(2));
            decodedEncryptedMessageByte = Base64.getDecoder().decode(partsOfEncryptedMessage.get(3));
            if (partsOfEncryptedMessage.size() > 4) {
                decodedSenderByte = Base64.getDecoder().decode(partsOfEncryptedMessage.get(4));
                sender = new String(decodedSenderByte);
                decodedSignatureByte = Base64.getDecoder().decode(partsOfEncryptedMessage.get(5));
            }
        } else {
            String[] partsOfEncryptedMessage = encryptedMessage.split("\\.");
            decodedNameByte = Base64.getDecoder().decode(partsOfEncryptedMessage[0]);
            name = new String(decodedNameByte);
            decodedIvByte = Base64.getDecoder().decode(partsOfEncryptedMessage[1]);
            decodedEncryptedKeyByte = Base64.getDecoder().decode(partsOfEncryptedMessage[2]);
            decodedEncryptedMessageByte = Base64.getDecoder().decode(partsOfEncryptedMessage[3]);
            if (partsOfEncryptedMessage.length > 4) {
                decodedSenderByte = Base64.getDecoder().decode(partsOfEncryptedMessage[4]);
                sender = new String(decodedSenderByte);
                decodedSignatureByte = Base64.getDecoder().decode(partsOfEncryptedMessage[5]);
            }
        }
        RSAPrivateKeySpec rsaPrivateKeySpec = getPrivateKey(name);
        byte[] desKey = decryptWithRsa(rsaPrivateKeySpec, decodedEncryptedKeyByte); //decodedEncryptedKey
        byte[] decryptedMessageByte = encryptWithDes(decodedEncryptedMessageByte, decodedIvByte, desKey); //decodedEncryptedMessage, decodediv, DesKey
        String plainMessage = new String(decryptedMessageByte, StandardCharsets.UTF_8);

        System.out.println("Receiver: " + name);
        System.out.println("Message: " + plainMessage);

        assert sender != null;
        if (sender.length() != 0) {
            System.out.println("Sender: " + sender);
            rsaPublicKeySpec = getPublicKey(sender);
            if (a == 1) {
                System.out.print("Signature: " + sender + " public key is missing.");
            } else {
                byte[] signature = signatureWithRsa(rsaPublicKeySpec, decodedSignatureByte);
                byte[] decryptedS = encryptWithDes(signature, decodedIvByte, desKey);
                String decryptedMessage = new String(decryptedS, StandardCharsets.UTF_8);
                if (plainMessage.equals(decryptedMessage)) {
                    System.out.print("Signature: valid!");
                } else {
                    System.out.print("Signature: not valid!");
                }
            }
        }
    }

    private static byte[] decryptWithRsa(RSAPrivateKeySpec rsaPrivateKeySpec, byte[] desKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        //determine the algorithm
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //determine algorithm mode (encryption or decryption)
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivateKeySpec);
        cipher.init(Cipher.DECRYPT_MODE, key);

        //encrypt des key
        return cipher.doFinal(desKey);
    }

    private static byte[] signatureWithRsa(RSAPublicKeySpec rsaPublicKeySpec, byte[] desMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //determine the algorithm and algorithm mode (encryption or decryption)
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(rsaPublicKeySpec);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(desMessage);
    }

    private static byte[] encryptWithDes(byte[] message, byte[] IV, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec iv = new IvParameterSpec(IV);
        Key desKey = new SecretKeySpec(key, "DES");

        //determine the algorithm and algorithm mode (encryption or decryption)
        Cipher decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, desKey, iv);

        //decrypt message
        return decryptCipher.doFinal(message);
    }

    private static RSAPublicKeySpec getPublicKey(String sender) throws ParserConfigurationException, SAXException, IOException {
        File keys = new File("c://keys");
        keys.mkdir();
        File fromFile = new File(keys.getPath() + "//" + sender + ".pub.xml");

        if (!fromFile.exists()) {
            a = 1;
            return null;
        } else {
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

    private static RSAPrivateKeySpec getPrivateKey(String name) throws ParserConfigurationException, SAXException, IOException {
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
}

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Scanner;

public class Login {
    public static void getToken(String name) throws NoSuchAlgorithmException, InvalidKeySpecException, ParserConfigurationException, SAXException, IOException {
        String saltAndHash = getHashPassword(name);
        boolean same = checkPassword(saltAndHash);

        RSAPrivateKeySpec SECRET = getPrivateKey(name);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey SECRET_KEY = keyFactory.generatePrivate(SECRET);

        if (same) {
            String token = createJWT(name, SECRET_KEY);
            System.out.print("Token: " + token);
        } else {
            System.out.println("Wrong! Username or password is invalid!");
        }
    }

    public static String getHashPassword(String name) throws FileNotFoundException {
        //user directory
        File U = new File("c://U");
        U.mkdir();

        File fromFile = new File(U.getPath() + "//" + name + ".txt");
        if (!fromFile.exists()) {
            System.out.println("Wrong! Username or password is invalid!");
            System.exit(1);
        }

        String hash = null;
        Scanner scanner = new Scanner(fromFile);
        while (scanner.hasNextLine()) {
            hash = scanner.nextLine();
        }

        scanner.close();

        return hash;
    }

    public static boolean checkPassword(String hash) throws FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        String[] b = hash.split("\\.");
        String salt = b[0];
        String hashPassword = b[1];

        Scanner scanner = new Scanner(System.in);
        System.out.print("Password: ");
        String password = scanner.next();
        scanner.close();

        //hash and salt current password
        int iterations = 65536;
        char[] chars = password.toCharArray();
        byte[] saltB = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(chars, saltB, iterations, 128);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] newHashPwB = skf.generateSecret(spec).getEncoded();

        Base64.Encoder encoder = Base64.getEncoder();
        String newHashPw = encoder.encodeToString(newHashPwB);

        return hashPassword.equals(newHashPw);
    }

    private static RSAPrivateKeySpec getPrivateKey(String name) throws ParserConfigurationException, SAXException, IOException {
        //keys directory
        File keys = new File("c://keys");
        keys.mkdir();

        File fromFile = new File(keys.getPath() + "//" + name + ".xml");

        if (!fromFile.exists()) {
            System.out.println("Wrong! " + fromFile.getPath() + " private key doesn't exist!");
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

    public static String createJWT(String subject, PrivateKey SECRET_KEY) throws UnsupportedEncodingException {
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        Instant now = Instant.now();
        String jwt = Jwts.builder()
                .setIssuedAt(Date.from(now))
                .setSubject(subject)
                .setExpiration(Date.from(now.plus(20, ChronoUnit.MINUTES)))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();

        return jwt;
    }
}

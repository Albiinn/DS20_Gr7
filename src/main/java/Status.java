import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class Status {
    public static void verifySignature(String token) throws SAXException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, ParserConfigurationException {
        String[] parts = token.split("\\.");
        String pl = new String(Base64.getUrlDecoder().decode(parts[1]));

        JSONObject payload = new JSONObject(pl);
        String name = payload.getString("sub");
        System.out.println("User: " + name);
        checkUser(name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");

        RSAPublicKeySpec SECRET = getPublicKey(name);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey SECRET_KEY = keyFactory.generatePublic(SECRET);

        try {
            Date now = new Date();
            Date date = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token).getBody().getExpiration();

            if (now.after(date)) {
                System.out.println("Valid: No");
                System.exit(1);
            }

            String formattedDate = dateFormat.format(date);
            System.out.println("Valid: Yes");
            System.out.println("Expire: " + formattedDate);
        } catch (SignatureException e) {
            System.out.println("Valid: No");
            System.exit(1);
        }
    }

    private static void checkUser(String name) {
        //user directory
        File U = new File("c://U");
        U.mkdir();

        File fromFile = new File(U.getPath() + "//" + name + ".txt");
        if (!fromFile.exists()) {
            System.out.println("Is valid?: No");
            System.exit(1);
        }
    }

    private static RSAPublicKeySpec getPublicKey(String emri) throws ParserConfigurationException, SAXException, IOException {
        //keys directory
        File keys = new File("c://keys");
        keys.mkdir();

        File fromFile = new File(keys.getPath() + "//" + emri + ".pub.xml");
        if (!fromFile.exists()) {
            System.out.println("Wrong!" + fromFile.getPath() + " public key doesn't exist!");
            System.exit(1);
        }

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

package JWTs;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.jsonwebtoken.*;

import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

public class login {
	
	//get private key from file
	private static RSAPrivateKeySpec getPrivKey(String emri) throws ParserConfigurationException, SAXException, IOException {
		
		//gjeje file-n
		File keys = new File("c://keys");
		keys.mkdir();
		File fromfile = new File(keys.getPath()+"//"+emri+".xml");
				
		if(!fromfile.exists()) {
			System.out.println("Gabim: Celesi privat "+fromfile.getPath()+" nuk ekziston");
			System.exit(1);
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document fromdoc = dBuilder.parse(fromfile);

		String modulus = fromdoc.getElementsByTagName("Modulus").item(0).getTextContent();
		String exponent = fromdoc.getElementsByTagName("D").item(0).getTextContent();
		
		byte[] decodedString = Base64.getDecoder().decode(new String(modulus).getBytes("UTF-8"));
		byte[] decodedStringu = Base64.getDecoder().decode(new String(exponent).getBytes("UTF-8"));
		
		BigInteger Modulus = new BigInteger(decodedString);
		BigInteger D = new BigInteger(decodedStringu);
		
		RSAPrivateKeySpec SECRET_KEY = new RSAPrivateKeySpec(Modulus, D);
		
		return SECRET_KEY;
	}
	
	//create JWT
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

	//get hashPw from file
	public static String getHashPw(String emri) throws FileNotFoundException {
		
		//krijimi i folderit users
		File U = new File("c://U");
		U.mkdir();
		File fromfile = new File(U.getPath()+"//"+emri+".txt");
				
		//nese nuk ekziston, mbylle programin
		if(!fromfile.exists()) {
		System.out.println("Gabim: Shfrytezuesi ose fjalekalimi i gabuar.");
		System.exit(1);
		}
		
		String hash=null;
		Scanner input = new Scanner(fromfile);
		
		while (input.hasNextLine()) {
			hash = input.nextLine();
		}
		input.close();
		
		return hash;
	}

	//check for correct password
	public static boolean Check(String hash) throws FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		String[] b = hash.split("\\.");
		String salt = b[0];
		String HashPw = b[1];
		
		Scanner input = new Scanner(System.in);
		System.out.print("Jepni fjalekalimin: ");
		String Pw = input.next();
		input.close();
		
		//hash and salt current password
		int iterations = 65536;
	    char[] chars = Pw.toCharArray();
	    byte[] saltB = Base64.getDecoder().decode(salt);
	         
	    PBEKeySpec spec = new PBEKeySpec(chars, saltB, iterations, 128);
	    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        
	    byte[] newHashPwB = skf.generateSecret(spec).getEncoded();
	    
	    Base64.Encoder encoder = Base64.getEncoder();
	    String newHashPw = encoder.encodeToString(newHashPwB);
	    
	    if(HashPw.equals(newHashPw)) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	    
	}
	
	//call this function
	public static void token(String emri) throws NoSuchAlgorithmException, InvalidKeySpecException, ParserConfigurationException, SAXException, IOException {
		
		String SaltAndHash = getHashPw(emri);
		boolean same = Check(SaltAndHash);
		
		RSAPrivateKeySpec SECRET = getPrivKey(emri);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey SECRET_KEY = keyFactory.generatePrivate(SECRET);
		
		if(same) {
			String token = createJWT(emri, SECRET_KEY);
			System.out.print("Token: "+token);
		}
		else {
			System.out.println("Gabim: Shfrytezuesi ose fjalekalimi i gabuar.");
		}
	}
}
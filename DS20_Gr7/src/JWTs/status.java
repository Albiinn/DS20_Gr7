package JWTs;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import org.json.*;

public class status {
	
	//check if user still exist
	public static void check_for_user(String emri) {
		
		//gjeje file-n
		File U = new File("c://U");
		U.mkdir();
		File fromfile = new File(U.getPath()+"//"+emri+".txt");
		
		if(!fromfile.exists()) {
			System.out.println("Valid: jo");
			System.exit(1);
		}
		
	}
	
	//get public key of user
	private static RSAPublicKeySpec getPubKey(String emri) throws ParserConfigurationException, SAXException, IOException {

		//gjeje file-n
		File keys = new File("c://keys");
		keys.mkdir();
		File fromfile = new File(keys.getPath()+"//"+emri+".pub.xml");
						
		if(!fromfile.exists()) {
			System.out.println("Gabim: Celesi publik "+fromfile.getPath()+" nuk ekziston");
			System.exit(1);
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document fromdoc = dBuilder.parse(fromfile);
		
		String modulus = fromdoc.getElementsByTagName("Modulus").item(0).getTextContent();
		String exponent = fromdoc.getElementsByTagName("Exponent").item(0).getTextContent();
		
		byte[] decodedString = Base64.getDecoder().decode(new String(modulus).getBytes("UTF-8"));
		byte[] decodedStringu = Base64.getDecoder().decode(new String(exponent).getBytes("UTF-8"));
		
		BigInteger Modulus = new BigInteger(decodedString);
		BigInteger Exponent = new BigInteger(decodedStringu);
		
		return new RSAPublicKeySpec(Modulus, Exponent);
	}

	//verification of signature
	public static void Check(String token) throws SAXException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, ParserConfigurationException {
		
		String[] parts = token.split("\\.");
		String pl = new String(Base64.getUrlDecoder().decode(parts[1]));
		
		JSONObject payload = new JSONObject(pl);
		String emri = payload.getString("sub");
		System.out.println("User: "+emri);
		check_for_user(emri);
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
	
		RSAPublicKeySpec SECRET = getPubKey(emri);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey SECRET_KEY = keyFactory.generatePublic(SECRET);
		
		try {
			
			Date now = new Date();
			Date date = Jwts.parser()
				.setSigningKey((PublicKey)SECRET_KEY)
				.parseClaimsJws(token).getBody().getExpiration();
			
			if(now.after(date)) {
				System.out.println("Valid: jo");
				System.exit(1);
			}
			
			String date_f = formatter.format(date);
			System.out.println("Valid: po");
			System.out.println("Skadimi: "+date_f);
			
		} catch (SignatureException  e) {
			
			System.out.println("Valid: jo");
			System.exit(1);
			
		}
	}
	
}
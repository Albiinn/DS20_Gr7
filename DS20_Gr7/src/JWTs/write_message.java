package JWTs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
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

public class write_message {

	private static Cipher encryptCipher;
	
	public static void Encrypt(String emri, String mesazhi, String shtegu, String token) throws ParserConfigurationException, SAXException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		
		Base64.Encoder encoder = Base64.getEncoder(); 
		
		//get user name
		String[] parts;
		String pl;
				
		JSONObject payload;
		String sender = null;
				
		//get user private key
		RSAPrivateKeySpec SECRET_KEY = null;
		
		if(token.length() != 0 || !token.isEmpty()) {
		
		//get user name
		parts = token.split("\\.");
		pl = new String(Base64.getUrlDecoder().decode(parts[1]));
		
		payload = new JSONObject(pl);
		sender = payload.getString("sub");
		
		//get user private key
		SECRET_KEY = getPrivKey(sender);
		
		}
		
		SecureRandom sr = new SecureRandom();
		byte[] IV = new byte[8];
		sr.nextBytes(IV);

		IvParameterSpec iv = new IvParameterSpec(IV);

		SecretKey key = KeyGenerator.getInstance("DES").generateKey();

		//gjeje file-n
		File keys = new File("c://keys");
		keys.mkdir();
		File fromfile = new File(keys.getPath()+"//"+emri+".pub.xml");
		
		//nese nuk ekziston, mbylle programin
		if(!fromfile.exists()) {
			System.out.println("Gabim: Celesi publik "+emri+" nuk ekziston.");
			System.exit(1);
		}
		
		//kthej te dhenat ne base64
		String EncryptedMessage = encoder.encodeToString(DESencrypt(mesazhi, iv, key));
		//merr celesin publik nga file
		RSAPublicKeySpec PubKey = getPubKey(fromfile);
		String EncryptedKey = encoder.encodeToString(RSAencrypt(PubKey, key));
		String part1 = encoder.encodeToString(emri.getBytes("UTF8"));
		String part2 = encoder.encodeToString(IV);
		String part5 = "";
		String part6 = "";
		
		if(token.length() != 0 || !token.isEmpty()) {
		
		part5 = encoder.encodeToString(sender.getBytes("UTF8"));
		part6 = encoder.encodeToString(RSASignature(SECRET_KEY, DESencrypt(mesazhi, iv, key)));
		
		}
		
		if(shtegu.contains(".txt")) {
			
			File file = new File(shtegu);
			PrintWriter input = new PrintWriter(file);
			
			input.println(part1);				//emri
			input.println(part2);				//iv tek DES
			input.println(EncryptedKey);		//celesi i DES
			input.println(EncryptedMessage);	//mesazhi
			input.println(part5);				//sander
			input.println(part6);				//signature
			
			input.close();
			System.out.println("Mesazhi i enkriptuar u ruajt ne fajllin "+shtegu+".");
			
		}
		
		else {
			System.out.println(part1+"."+part2+"."+EncryptedKey+"."+EncryptedMessage+"."+part5+"."+part6);
		}
	}
	
	private static byte[] DESencrypt(String mesazhi, AlgorithmParameterSpec iv, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, ParserConfigurationException, SAXException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		
		//zgjedh algoritmin dhe moden (encrypt ose decrypt)
		encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key, iv);

		//merr byte e mesazhit
		byte[] input = mesazhi.getBytes("UTF8");
				
		//encrypt
		byte[] cipherText = encryptCipher.doFinal(input);
		return cipherText;
	}

	private static byte[] RSAencrypt(RSAPublicKeySpec RSAPubKey, SecretKey DESKey) throws  InvalidKeyException, ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
	
		//cakto algoritmin dhe moden (encrypt apo decrypt)
		Cipher cipher = Cipher.getInstance("RSA");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		//gjenero celesin
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(RSAPubKey);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		//merr byte-t e celesit se desit
		byte[] input = DESKey.getEncoded();

		//fut te dhenat ne algoritem
		cipher.update(input);
		
		//encrypto celesin e desit
		byte[] cipherText = cipher.doFinal();
		
		return cipherText;
	}
	
	private static byte[] RSASignature(RSAPrivateKeySpec RSAPrivKey, byte[] DesMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance("RSA");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(RSAPrivKey);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		cipher.update(DesMessage);
		
		byte[] cipherText = cipher.doFinal();
		
		return cipherText;
	}
	
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
		
		return new RSAPrivateKeySpec(Modulus, D);
	}
	
	private static RSAPublicKeySpec getPubKey(File fromfile) throws ParserConfigurationException, SAXException, IOException {
		
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
	
}

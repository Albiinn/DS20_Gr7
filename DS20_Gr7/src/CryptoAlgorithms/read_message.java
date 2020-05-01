package CryptoAlgorithms;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

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

public class read_message {
	
private static Cipher decryptCipher;
	
	public static void Decrypt(String mesazhi) throws ParserConfigurationException, SAXException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
		
		byte[] decodedemri;
		String emri;
		
		byte[] decodediv;
		
		byte[] decodedEncryptedKey;

		byte[] decodedEncryptedMessage;
	
		
		if(mesazhi.contains(".txt")) {
			
			File file = new File(mesazhi);
			
			if(!file.exists()) {
				System.out.println("Gabim: Nuk ekziston fajlli i tille");
				System.exit(1);
			}
			
			Scanner input = new Scanner(file);
			ArrayList<String> sa = new ArrayList<String>();
			
			while(input.hasNextLine()) {
				sa.add(input.nextLine());
			}	
			
			decodedemri = Base64.getDecoder().decode(sa.get(0));
			emri = new String(decodedemri);
			
			decodediv = Base64.getDecoder().decode(sa.get(1));
			
			decodedEncryptedKey = Base64.getDecoder().decode(sa.get(2));
	
			decodedEncryptedMessage = Base64.getDecoder().decode(sa.get(3));
		
		}
		
		else {
		
			String[] a = mesazhi.split("\\.");

			decodedemri = Base64.getDecoder().decode(a[0]);
			emri = new String(decodedemri);
			
		    decodediv = Base64.getDecoder().decode(a[1]);
			
			decodedEncryptedKey = Base64.getDecoder().decode(a[2]);
	
			decodedEncryptedMessage = Base64.getDecoder().decode(a[3]);
		
		}
		
		RSAPrivateKeySpec RSAPrivKey = getPrivKey(emri);

		byte[] DesKey = RSAdecrypt(RSAPrivKey, decodedEncryptedKey); //decodedEncryptedKey

		byte[] decryptedmesazh = DESencrypt(decodedEncryptedMessage, decodediv, DesKey); //decodedEncryptedMessage, decodediv, DesKey

		String M = new String(decryptedmesazh, StandardCharsets.UTF_8);
		System.out.println("Marresi: "+emri);
		System.out.print("Mesazhi: "+M);
	}
	
	private static byte[] DESencrypt(byte[] mesazhi, byte[] IV, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, ParserConfigurationException, SAXException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		AlgorithmParameterSpec iv = new IvParameterSpec(IV);
		Key desKey = new SecretKeySpec(key, "DES");
		
		//zgjedh algoritmin dhe moden (encrypt ose decrypt)
		decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		decryptCipher.init(Cipher.DECRYPT_MODE, desKey, iv);
				
		//decrypt
		byte[] decipherText = decryptCipher.doFinal(mesazhi);
				
		return decipherText;
	}
	
	private static byte[] RSAdecrypt(RSAPrivateKeySpec RSAPrivKey, byte[] DESKey) throws  InvalidKeyException, ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		 
		//cakto algoritmin dhe moden (encrypt apo decrypt)
		Cipher cipher = Cipher.getInstance("RSA");
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		//gjenero celesin
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(RSAPrivKey);
		cipher.init(Cipher.DECRYPT_MODE, key );

		//encrypto celesin e desit
		byte[] cipherText = cipher.doFinal(DESKey);
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
	
}

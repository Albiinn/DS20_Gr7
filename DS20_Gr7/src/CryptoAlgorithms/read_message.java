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
import java.util.Base64;

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
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, ParserConfigurationException, SAXException, IOException {
		read_message.Decrypy("YWxiaW4=.fm6FcmloKps=.WB/0RWEE2bve/P81Zo9dzpnRfx9GLclHC5gkAOKJqfu6neL3Fa"
				+ "GinnCXhy65rd4t6fV9go5SnCdFDMpoD6cDCymZuBDRWb91Vvf30pA4pD/0xP4zG8o5yDi/2/fUyL6NRbfn/73COTRW8dj9sv63KPm"
				+ "JwmhbV57t95fXzBwyfMx7N7fgqzW2iGjl0huJlwkFFmOseY3LCW//0bHVCiZ8zplcZeAPGnhxdtkw0I1N0qHIxMzKRvi0HJNeGO9TgoG5SW15"
				+ "v1McLrpCKLmg6yUcLJDuFkiuQNPXg8lyV7+SkTwCty7zAqp"
				+ "6KT29g8yZkPduPMtO3tyTUTtwthZ5ry/sLw==.rHQxJS4DCtpRr+puRuBmZw==");
	}
	
	private static Cipher decryptCipher;
	
	public static void Decrypy(String mesazhi) throws ParserConfigurationException, SAXException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
		String[] a = mesazhi.split("\\.");
;
		byte[] decodedemri = Base64.getDecoder().decode(a[0]);
		String emri = new String(decodedemri);
		
		byte[] decodediv = Base64.getDecoder().decode(a[1]);
		String iv = new String(decodediv);
		
		byte[] decodedEncryptedKey = Base64.getDecoder().decode(a[2]);
		String EncryptedKey = new String(decodedEncryptedKey);
		
		byte[] decodedEncryptedMessage = Base64.getDecoder().decode(a[3]);
		String EncryptedMessage = new String(decodedEncryptedMessage);
		
		RSAPrivateKeySpec RSAPrivKey = getPrivKey(emri);

		byte[] DesKey = RSAdecrypt(RSAPrivKey, decodedEncryptedKey);

		byte[] decryptedmesazh = DESencrypt(decodedEncryptedMessage, decodediv, DesKey);
		
		String M = decryptedmesazh.toString();
		System.out.println(M);
		
		
		
	}
	
	private static byte[] DESencrypt(byte[] mesazhi, byte[] IV, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, ParserConfigurationException, SAXException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		AlgorithmParameterSpec iv = new IvParameterSpec(IV);
		Key desKey = new SecretKeySpec(key, "DES");
		
		//zgjedh algoritmin dhe moden (encrypt ose decrypt)
		decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		decryptCipher.init(Cipher.DECRYPT_MODE, desKey, iv);
	

		//merr byte e mesazhit
		//byte[] input = mesazhi.getBytes();
				
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

		//System.out.println(encoder.encodeToString(key.getPrivateExponent().toByteArray()));
		//fut te dhenat ne algoritem
		//cipher.update(DESKey);

		//encrypto celesin e desit
		byte[] cipherText = cipher.doFinal(DESKey);
		System.out.println(cipherText.length);
		return cipherText;
	}
	
	private static RSAPrivateKeySpec getPrivKey(String emri) throws ParserConfigurationException, SAXException, IOException {
		
		//gjeje file-n
		File keys = new File("c://keys");
		keys.mkdir();
		File fromfile = new File(keys.getPath()+"//"+emri+".xml");
				
		if(!fromfile.exists()) {
			System.out.println("Gabim: Celesi privat +"+fromfile.getPath()+" nuk ekziston");
			System.exit(1);
		}
		
		Base64.Encoder encoder = Base64.getEncoder();
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

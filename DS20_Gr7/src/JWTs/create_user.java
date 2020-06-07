package JWTs;

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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class create_user {

	private static RSAPublicKeySpec rsaPubKeySpec = null;
	private static RSAPrivateKeySpec rsaPrivKeySpec = null;
	private static RSAPrivateCrtKey rsaPrivCrtKey = null;
	
	//gjenerimi i salt
	private static byte[] getSalt() throws NoSuchAlgorithmException {
		
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt;
	}
	
	//hash passwordi
	private static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
        int iterations = 65536;	//It indicates how many iterations that this algorithm run for, increasing the time it takes to produce the hash.
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();
         
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 128);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        
        byte[] hash = skf.generateSecret(spec).getEncoded();
        
        Base64.Encoder encoder = Base64.getEncoder();
        
        return encoder.encodeToString(salt)+"."+encoder.encodeToString(hash);
        
    }
	
	//krijo userin me username dhe password
	public static void user(String emri) throws FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		//krijimi i folderit users
		File U = new File("c://U");
		U.mkdir();
		
		Scanner input = new Scanner(System.in);
		
		System.out.print("Jepni fjalekalimin: ");
		String password = input.next();
		
		String symbols = "0123456789!@#$%^&*()_+[];',./?><|:}{";
		int k=0;
		
		if(password.length()<6) {
			System.out.println("Gabim: Fjalekalimi duhet te permbaje se paku 6 karaktere.");
			System.exit(1);
		}
		
		for(int i=0; i<password.toCharArray().length; i++) {
			
			for(int j=0; j<symbols.toCharArray().length; j++) {
				
				if(((password.toCharArray())[i] == symbols.toCharArray()[j])) {
					
					k++;
					
					}
			}
		}
		
		
		System.out.print("Perserit fjalekalimin: ");
		String repassword = input.next();
		
		input.close();
		
		if(!password.equals(repassword)) {
			System.out.println("Gabim: Fjalekalimet nuk perputhen.");
			System.exit(1);
		}
		
		
		if(k==0) {
			System.out.println("Gabim: Fjalekalimi duhet te permbaje se paku nje numer ose simbol.");
			System.exit(1);
		}
		
		String hash = generateStorngPasswordHash(password);
		
		File file = new File(U.getPath()+"//"+emri+".txt");
		PrintWriter pw = new PrintWriter(file);
		
		pw.println(hash);
		
		System.out.println("Eshte krijuar shfrytezuesi "+emri);
		
		pw.close();
		
	}
	
	//konstruktori qe thirr metoden KEY, per gjenerim te celesave
	public create_user() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KEY();
	}
	
	//Gjenerimi i pales se celesave publik dhe privat
	public void KEY() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		
		rsaPrivCrtKey = createCrtKey(rsaPubKeySpec, rsaPrivKeySpec);
	}
	
	//algoritme per gjetjen e koeficienteve Chinese Remainder Theorem
	//nga expinenti publik, exponenti privat si dhe moduli
	private static BigInteger findFactor(BigInteger e, BigInteger d, BigInteger n) {
		
	    BigInteger edMinus1 = e.multiply(d).subtract(BigInteger.ONE);
	    int s = edMinus1.getLowestSetBit();
	    BigInteger t = edMinus1.shiftRight(s);

	    for (int aInt = 2; true; aInt++) {
	    	
	        BigInteger aPow = BigInteger.valueOf(aInt).modPow(t, n);
	        
	        for (int i = 1; i <= s; i++) {
	        	
	            if (aPow.equals(BigInteger.ONE)) {
	                break;
	            }
	            
	            if (aPow.equals(n.subtract(BigInteger.ONE))) {
	                break;
	            }
	            
	            BigInteger aPowSquared = aPow.multiply(aPow).mod(n);
	            
	            if (aPowSquared.equals(BigInteger.ONE)) {
	                return aPow.subtract(BigInteger.ONE).gcd(n);
	            }
	            
	            aPow = aPowSquared;
	        }
	    }

	}
	
	
	//Krijimi i celesit privat, me koeficientet e Chinese Remainder Theorem
	private static RSAPrivateCrtKey createCrtKey(RSAPublicKeySpec rsaPubSpec, RSAPrivateKeySpec rsaPrivSpec) throws NoSuchAlgorithmException, InvalidKeySpecException {
	
	    BigInteger e = rsaPubSpec.getPublicExponent();
	    BigInteger d = rsaPrivSpec.getPrivateExponent();
	    BigInteger n = rsaPubSpec.getModulus();
	    BigInteger p = findFactor(e, d, n);
	    BigInteger q = n.divide(p);
	    
	    if (p.compareTo(q) > 0) {
	        BigInteger t = p;
	        p = q;
	        q = t;
	    }
	    
	    BigInteger exp1 = d.mod(p.subtract(BigInteger.ONE));
	    BigInteger exp2 = d.mod(q.subtract(BigInteger.ONE));
	    BigInteger coeff = q.modInverse(p);
	    RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(n, e, d, p, q, exp1, exp2, coeff);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    
	    return (RSAPrivateCrtKey) kf.generatePrivate(keySpec);
	}
	
	//Ruajtja e celesave publik e privat ne xml fajlla te ndare
	public static void MbusheFajllin(String emri) throws ParserConfigurationException, TransformerException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		//krijimi i userit
		user(emri);
		
		Base64.Encoder encoder = Base64.getEncoder();
		
		//per celesin publik koeficientat e chinese remainder theorem
		DocumentBuilderFactory docFactoryPub = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilderPub = docFactoryPub.newDocumentBuilder();

		Document docPub = docBuilderPub.newDocument();
		Element rootElementPub = docPub.createElement("RSAKeyValue");
		docPub.appendChild(rootElementPub);

		Element ModulusPub = docPub.createElement("Modulus");
		ModulusPub.appendChild(docPub.createTextNode(encoder.encodeToString(rsaPubKeySpec.getModulus().toByteArray())));
		rootElementPub.appendChild(ModulusPub);

		Element ExponentPub = docPub.createElement("Exponent");
		ExponentPub.appendChild(docPub.createTextNode(encoder.encodeToString(rsaPubKeySpec.getPublicExponent().toByteArray())));
		rootElementPub.appendChild(ExponentPub);
		
		TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
		Transformer transformerPub = transformerFactoryPub.newTransformer();
		//mi formatu ne xml
		transformerPub.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource sourcePub = new DOMSource(docPub);
		
		//per celesin privat koeficientat e chinese remainder theorem
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("RSAKeyValue");
		doc.appendChild(rootElement);

		Element Modulus = doc.createElement("Modulus");
		Modulus.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getModulus().toByteArray())));
		rootElement.appendChild(Modulus);

		Element Exponent = doc.createElement("Exponent");
		Exponent.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getPublicExponent().toByteArray())));
		rootElement.appendChild(Exponent);

		Element P = doc.createElement("P");
		P.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getPrimeP().toByteArray())));
		rootElement.appendChild(P);

		Element Q = doc.createElement("Q");
		Q.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getPrimeQ().toByteArray())));
		rootElement.appendChild(Q);
		
		Element DP = doc.createElement("DP");
		DP.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getPrimeExponentP().toByteArray())));
		rootElement.appendChild(DP);
		
		Element DQ = doc.createElement("DQ");
		DQ.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getPrimeExponentQ().toByteArray())));
		rootElement.appendChild(DQ);
		
		Element InverseQ = doc.createElement("InverseQ");
		InverseQ.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getCrtCoefficient().toByteArray())));
		rootElement.appendChild(InverseQ);
		
		Element D = doc.createElement("D");
		D.appendChild(doc.createTextNode(encoder.encodeToString(rsaPrivCrtKey.getPrivateExponent().toByteArray())));
		rootElement.appendChild(D);
		
		//me transformu .txt ne .xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		
		//krijimi i folderit keys
		File keys = new File("c://keys");
		keys.mkdir();
		
		//krijimi i fileve per celesa
		File filePub = new File(keys.getPath()+"//"+emri+".pub.xml");
		File file = new File(keys.getPath()+"//"+emri+".xml");
		
		if(file.exists() || filePub.exists()) {
			System.out.println("Gabim: Celesi "+emri+" ekziston paraprakisht.");
			System.exit(1);
		}
		
		StreamResult result = new StreamResult(file);
		System.out.println("Eshte krijuar celesi privat "+file.getPath());
		transformer.transform(source, result);
		
		//StreamResult result = new StreamResult(System.out);
		StreamResult resultPub = new StreamResult(filePub);
		System.out.println("Eshte krijuar celesi publik "+filePub.getPath());
		transformerPub.transform(sourcePub, resultPub);

	  }
	
}
	

	


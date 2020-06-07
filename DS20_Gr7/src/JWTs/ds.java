package JWTs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class ds {
	
public static void main(String[] args) {
		
		if(args.length<2) {
			System.out.println("Argumentet mungojne ose jane jo-valide");
			System.exit(1);
		}
		
		try {
			
			switch (args[0]) {
			
			case "beale": 
				switch (args[1]) {
				case "encrypt": beale.book(args[2]); beale.encrypt(args[2], args[3]); 
					break;
				case "decrypt": beale.decrypt(args[2], args[3]);
					break;
				default: System.out.println("Thirr encrypt ose decrypt");
					break;
				}
				break;
				
			case "tap-code": 
				switch (args[1]) {
				case "encode": tap_code.encode(args[2]);
					break;
				case "decode": tap_code.decode(args[2]);
					break;
				default: System.out.println("Thirr encode ose decode");
					break;
				}
				break;
				
			case "case": 
				switch (args[1]) {
				case "lower": _case.lower(args[2]);
					break;
				case "upper": _case.upper(args[2]);
					break;
				case "capitalize": _case.capitalize(args[2]);
					break;
				case "inverse": _case.inverse(args[2]);
					break;
				case "alternating": _case.alternating(args[2]);
					break;
				case "sentence": _case.sentence(args[2]);
					break;
				default: System.out.println("Thirr lower ose upper, capitalize, inverse, alternating, sentence");
					break;
				}
				break;
				
			case "create-user":
				create_user o = new create_user();
				create_user.MbusheFajllin(args[1]);
				break;
				
			case "delete-user": delete_user.delete(args[1]);
				break;
				
			case "login": login.token(args[1]);
				break;
				
			case "status": status.Check(args[1]);
				break;
				
			case "export-key":
				if(args.length==3) {
					export_key.CallPublicOrPrivate(args[1], args[2], "a");
				}
				else {
					export_key.CallPublicOrPrivate(args[1], args[2], args[3]);
				}
				break;
				
			case "import-key":
				import_key.ImportToFrom(args[1], args[2]);
				break;
				
			case "write-message":
				if(args.length==5) {
					write_message.Encrypt(args[1], args[2], args[3], args[4]);
				}
				else {
					write_message.Encrypt(args[1], args[2], "a", args[3]);
				}
				break;
				
			case "read-message":
				read_message.Decrypt(args[1]);
				break;
				
			default: System.out.println("Kryej veprime me beale, tap-code, case, "
					+ "create-user, delete-user, export-key, import-key, write-message, apo me read-message");
				break;
			}
		
	}	
		catch(SAXException ex) {
			ex.getMessage();
		}
		catch(NoSuchAlgorithmException ex) {
			ex.getMessage();
		}
		catch(InvalidKeySpecException ex) {
			ex.getMessage();
		}
		catch(ParserConfigurationException ex) {
			ex.getMessage();
		}
		catch(TransformerException ex) {
			ex.getMessage();
		}
		catch(FileNotFoundException ex) {
			ex.getMessage();
		}
		catch(IOException ex) {
			ex.getMessage();
		}
		 catch (Exception ex) {
			ex.getMessage();
		}

	}
	
}

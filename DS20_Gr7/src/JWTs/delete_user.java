package JWTs;

import java.io.File;

public class delete_user {
	
	public static void delete(String emri) {
		
		//dir e celesave
		File keys = new File("c://keys");
		keys.mkdir();
		
		//filet e pales se celesit pub/priv
		File filePub = new File(keys.getPath()+"//"+emri+".pub.xml");
		File file = new File(keys.getPath()+"//"+emri+".xml");
		
		if(file.exists() && filePub.exists()) {
			
			if(filePub.delete() && file.delete()) {
				System.out.println("Eshte larguar celesi privat "+file.getPath());
				System.out.println("Eshte larguar celesi publik "+filePub.getPath());
			}
			else {
				System.out.println("Fshirja nuk u realizua");
			}
		}
		
		else if(filePub.exists()) {
			
			if(filePub.delete()) {
				System.out.println("Eshte larguar celesi publik "+filePub.getPath());
			}
			else {
				System.out.println("Fshirja nuk u realizua");
			}
		
		}
		
		else if(file.exists()) {
			
			if(file.delete()) {
				System.out.println("Eshte larguar celesi privat "+file.getPath());
			}
			else {
				System.out.println("Fshirja nuk u realizua");
			}
		}
		else {
			System.out.println("Gabim: Celesi "+emri+" nuk ekziston.");
		}
		
	}
}

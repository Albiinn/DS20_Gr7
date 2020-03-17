package CryptoAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class beale {
	
	public static void encrypt(String path, String plaintext) throws Exception {
		Scanner input=null;
		String emri=null;
		int rez=1;
		char[] letter=plaintext.toCharArray();
		
			int[] cipher = new int[letter.length];
			
			for(int i=0; i<letter.length; i++) {
				input=new Scanner(book(path));
				
				while(input.hasNext()) {
				emri=input.next().toLowerCase();
				
				if(letter[i]==emri.charAt(0)) {
					cipher[i]=rez;
					input.nextLine();
					rez=1;
					System.out.print(cipher[i]+" ");
					break;
					
				}
				else {
					rez=rez+1;
				}
			}	
		}
			input.close();
	};
	
	
	public static void decrypt(String path, String ciphertext) throws Exception {
		String[] a = ciphertext.split(" ");
		int[] numbers=new int[a.length];
		
		for(int i=0; i<a.length; i++) {
			numbers[i]=Integer.parseInt(a[i]);
		}
		
		File file = new File(path);
		if(!file.exists()) {
			System.out.println("Fajlli nuk ekzsiton");
			System.exit(2);
		}
		
		char[] decipher = new char[numbers.length];
		int rez=1;
		Scanner input=null;
		
		for(int i=0; i<numbers.length; i++) {
			input = new Scanner(file);
			if(numbers[i]>a.length) {
				System.out.println("Te hyra jo valide");
				System.exit(3);
			}
			
			while(input.hasNext()) {
				String emri = input.next().toLowerCase();
				
				if(numbers[i]==rez) {
					decipher[i]=emri.charAt(0);
					System.out.print(decipher[i]);
					rez=1;
					input.nextLine();
					break;
				}
				else {
					rez=rez+1;
				}
			}
		}
		input.close();
	}

	
	
	public static File book(String path) throws FileNotFoundException {
		File file=new File(path);
		PrintWriter output = new PrintWriter(file);	
		output.println("Korona arriti pothuajse ne cdo vend. Bartes i virusit eshte lakuriqi, maceja etj.");
		output.println("Heshtja dhe qetesia kane kapluar te gjitha vendet");
		output.println("Virusi sulmon organet e frymemarrjes");
		output.println("Nese hundet ju rrjedhin, atehere jeni infektuar");
		output.println("Jane shfaqur kater personat e pare te infektuar, me inicialet ZY, YZ, XU, UX");
		output.close();
		return file;
	}
}

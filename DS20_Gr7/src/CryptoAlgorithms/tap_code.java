package CryptoAlgorithms;

class tap_code {
	
	public static void encode(String plaintext) {
		char[][] tabela=matrica();
		String s=plaintext.toLowerCase(); 
		char[] letter=s.toCharArray();
		
		for(int k=0; k<letter.length; k++) {
			
			for(int i=0; i<5; i++) {
				
				for(int j=0; j<5; j++) {
					
					if(letter[k]==tabela[i][j]) {
						
						for(int a=0; a<i+1; a++) {
							System.out.print(".");
						}
						
						System.out.print(" ");
						
						for(int b=0; b<j+1; b++) {
							System.out.print(".");
						}
						
						System.out.print("   ");
						break;
					}
				}
			}
			
		}
	};
	
	
	public static void decode(String ciphertext) {
		char[][] tabela=matrica();
		String[] rreshti=ciphertext.split("  ");
		for(int i=0; i<rreshti.length; i++) {
			String[] kolona=rreshti[i].split(" ");	
			if(kolona[i].length()>5) {
				System.out.println("Te hyra jo valide");
				System.exit(2);
			}
			System.out.print(tabela[kolona[0].length()-1][kolona[1].length()-1]);
		}
		
	};
	
	
	private static char[][] matrica() {
		char[][] tabela = new char[5][5];
		int ascci=97;
		
		for(int i=0; i<5; i++) {
			
			for(int j=0; j<5; j++) {
				
				if(ascci==99) {
					tabela[i][j]=(char)107;
				}
				
				else if(ascci==107) {
					j=j-1;
				}
				
				else {
				tabela[i][j]=(char)ascci;
				}
				ascci=ascci+1;
		 }
	  }
		return tabela;
	}
}
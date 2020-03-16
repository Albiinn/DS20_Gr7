package CryptoAlgorithms;
class _case {
	
	public static void lower(String s) {
		System.out.println(s.toLowerCase());
	}
	
	
	public static void upper(String s) {
		System.out.println(s.toUpperCase());
	}
	
	
	public static void capitalize(String s) {
		String[] d=s.split(" ");
		for(int i=0; i<d.length; i++) {
			char[] f=(d[i].toLowerCase()).toCharArray();	
			System.out.print(Character.toUpperCase(f[0]));
			for(int j=1; j<f.length; j++) {
				System.out.print(f[j]);
			}
			System.out.print(" ");
		}
	}
	
	
	public static void inverse(String s) {
		char[] d=s.toCharArray();
		char[] u=s.toUpperCase().toCharArray();
		char[] l=s.toLowerCase().toCharArray();
		for(int i=0; i<d.length; i++) {
			if(d[i]==u[i]) {
				System.out.print(l[i]);
			}
			else {
				System.out.print(u[i]);
			}
		}
	}
	
	
	public static void alternating(String s) {
		char[] a=s.toCharArray();
		for(int i=0; i<a.length; i++) {
			if(i%2==0) {
				System.out.print(Character.toLowerCase(a[i]));
			}
			else {
				System.out.print(Character.toUpperCase(a[i]));
			}
		}
	}
	
	
	public static void sentence(String s) {
		
		char[] a = s.toLowerCase().toCharArray();
		System.out.print(Character.toUpperCase(a[0]));
		
		for(int i=1; i<a.length; i++) {
			
			if(a[i]=='.' || a[i]=='!' || a[i]=='?') {
				System.out.print(a[i]);
				System.out.print(a[i+1]);
				System.out.print(Character.toUpperCase(a[i+2]));
				i=i+2;
			}
			else {
				System.out.print(a[i]);
			}
		}
		
		if(a[a.length-1]!='.' || a[a.length-1]!='!' || a[a.length-1]!='?')
		System.out.print(".");
		}
}
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Beale {

    public static void encrypt(String path, String plaintext) throws Exception {
        Scanner scanner = new Scanner(createAndWriteToFile(path));
        ;
        String name;
        int result = 1;
        char[] letter = plaintext.toCharArray();
        int[] cipher = new int[letter.length];

        for (int i = 0; i < letter.length; i++) {
            while (scanner.hasNext()) {
                name = scanner.next().toLowerCase();
                if (letter[i] == name.charAt(0)) {
                    cipher[i] = result;
                    scanner.nextLine();
                    result = 1;
                    System.out.print(cipher[i] + " ");
                    break;
                } else {
                    result = result + 1;
                }
            }
        }
        scanner.close();
        System.out.println(Arrays.toString(cipher));
    }

    public static void decrypt(String path, String ciphertext) throws Exception {
        //split string by spaces
        String[] a = ciphertext.split(" ");

        //convert string array to int array and check for consistency
        int count = countWord(path);
        int[] numbers = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            numbers[i] = Integer.parseInt(a[i]);
            if (numbers[i] > count) {
                System.out.println("No valid inputs!");
                System.exit(1);
            }
        }

        //prepare file to read
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("The file doesn't exist!");
            System.exit(1);
        }
        Scanner scanner = new Scanner(file);

        //decrypt the given string
        char[] decipher = new char[a.length];
        int result = 1;
        String name;
        for (int i = 0; i < numbers.length; i++) {
            while (scanner.hasNextLine()) {
                name = scanner.next().toLowerCase();
                if (numbers[i] == result) {
                    decipher[i] = name.charAt(0);
                    result = 1;
                    scanner.nextLine();
                    break;
                } else {
                    result = result + 1;
                }
            }
        }
        scanner.close();
        System.out.println(String.valueOf(decipher));
    }

    public static File createAndWriteToFile(String path) throws FileNotFoundException {
        File file = new File(path);
        PrintWriter output = new PrintWriter(file);
        output.println("Korona arriti pothuajse ne cdo vend. Bartes i virusit eshte lakuriqi, maceja etj.");
        output.println("Heshtja dhe qetesia kane kapluar te gjitha vendet.");
        output.println("Virusi sulmon organet e frymemarrjes!");
        output.println("Nese hundet ju rrjedhin, atehere jeni infektuar.");
        output.println("Jane shfaqur kater personat e pare te infektuar, me inicialet ZY, YZ, XU, UX.");
        output.close();
        return file;
    }

    public static int countWord(String path) throws Exception {
        int wordcount = 0;
        String line;
        BufferedReader bufferReader = new BufferedReader(new FileReader(path));

        while ((line = bufferReader.readLine()) != null) {
            String[] words = line.split(" ");
            wordcount = wordcount + words.length;
        }

        bufferReader.close();
        return wordcount;
    }
}

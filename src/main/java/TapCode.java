public class TapCode {

    public static void encode(String plaintext) {
        char[][] table = matrix();
        char[] letter = plaintext.toLowerCase().toCharArray();

        for (char c : letter) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (c == table[i][j]) {
                        for (int a = 0; a < i + 1; a++) {
                            System.out.print(".");
                        }
                        System.out.print(" ");
                        for (int b = 0; b < j + 1; b++) {
                            System.out.print(".");
                        }
                        System.out.print("   ");
                        break;
                    }
                }
            }
        }
    }

    public static void decode(String ciphertext) {
        char[][] table = matrix();
        String[] row = ciphertext.split("  ");
        char[] letters = new char[row.length];

        for (int i = 0; i < row.length; i++) {
            String[] column = row[i].split(" ");
            if (column[0].length() > 5 || column[1].length() > 5) {
                System.out.println("No valid input");
                System.exit(2);
            }
            letters[i] = table[column[0].length() - 1][column[1].length() - 1];
        }
        System.out.print(String.valueOf(letters));
    }

    private static char[][] matrix() {
        char[][] table = new char[5][5];
        int ascci = 97;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (ascci == 99) {
                    table[i][j] = (char) 107;
                } else if (ascci == 107) {
                    j = j - 1;
                } else {
                    table[i][j] = (char) ascci;
                }
                ascci = ascci + 1;
            }
        }
        return table;
    }
}

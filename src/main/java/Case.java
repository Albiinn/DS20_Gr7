public class Case {

    public static void lowerCase(String s) {
        System.out.println(s.toLowerCase());
    }

    public static void upperCase(String s) {
        System.out.println(s.toUpperCase());
    }

    public static void capitalize(String s) {
        System.out.println(s.toLowerCase().replace(s.charAt(0), Character.toUpperCase(s.charAt(0))));
    }

    public static void inverse(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'A' && chars[i] <= 'Z') {
                chars[i] = (char) (chars[i] + 32);
            } else if (chars[i] >= 'a' && chars[i] <= 'z') {
                chars[i] = (char) (chars[i] - 32);
            }
        }
        System.out.println(String.copyValueOf(chars));
    }

    public static void alternating(String s) {
        char[] a = s.toLowerCase().toCharArray();
        for (int i = 0; i < a.length; i++) {
            if (i % 2 == 1) {
                a[i] = Character.toUpperCase(a[i]);
            }
        }
        System.out.println(String.copyValueOf(a));
    }

    public static void formatSentence(String s) {
        String punctuation = ".!?";
        String punctuation1 = "-,:;";
        char[] letters = s.toLowerCase().toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < letters.length - 1; i++) {
            if (punctuation.contains(String.valueOf(letters[i]))) {
                stringBuilder.append(letters[i]);
                stringBuilder.append(' ');
                stringBuilder.append(Character.toUpperCase(letters[i + 1]));
                i++;
            } else if (punctuation1.contains(String.valueOf(letters[i]))) {
                stringBuilder.append(letters[i]);
                stringBuilder.append(' ');
                stringBuilder.append(letters[i + 1]);
                i++;
            } else {
                stringBuilder.append(letters[i]);
            }
        }
        System.out.println(stringBuilder);
    }
}

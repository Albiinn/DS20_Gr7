public final class DataSecurity {
    public static void run(String[] args) {
        if (args.length < 2) {
            System.out.println("Arguments are missing or are not valid!");
            System.exit(1);
        }
        try {
            switch (args[0]) {
                case "beale":
                    if (args.length > 3) {
                        switch (args[1]) {
                            case "encrypt":
                                Beale.createAndWriteToFile(args[2]);
                                Beale.encrypt(args[2], args[3]);
                                break;
                            case "decrypt":
                                Beale.decrypt(args[2], args[3]);
                                break;
                            default:
                                System.out.println("Call 'encrypt' or 'decrypt'");
                                break;
                        }
                    } else {
                        System.out.println("Arguments are missing");
                    }
                    break;

                case "tap-code":
                    if (args.length > 2) {
                        switch (args[1]) {
                            case "encode":
                                TapCode.encode(args[2]);
                                break;
                            case "decode":
                                TapCode.decode(args[2]);
                                break;
                            default:
                                System.out.println("Call 'encode' or 'decode'");
                                break;
                        }
                    } else {
                        System.out.println("Arguments are missing");
                    }
                    break;

                case "case":
                    if (args.length > 2) {
                        switch (args[1]) {
                            case "capitalize":
                                Case.capitalize(args[2]);
                                break;
                            case "inverse":
                                Case.inverse(args[2]);
                                break;
                            case "alternating":
                                Case.alternating(args[2]);
                                break;
                            case "sentence":
                                Case.formatSentence(args[2]);
                                break;
                            default:
                                System.out.println("Call 'lower', 'upper', 'capitalize', 'inverse', 'alternating', or 'sentence'.");
                                break;
                        }
                    } else {
                        System.out.println("Arguments are missing");
                    }
                    break;

                case "create-user":
                    User.createUser(args[1]);
                    break;

                case "delete-user":
                    User.deleteUser(args[1]);
                    break;

                case "login":
                    Login.getToken(args[1]);
                    break;

                case "status":
                    Status.verifySignature(args[1]);
                    break;

                case "export-key":
                    if (args.length > 2) {
                        if (args.length == 3) {
                            Exporter.exportKeys(args[1], args[2], "a");
                        } else {
                            Exporter.exportKeys(args[1], args[2], args[3]);
                        }
                    } else {
                        System.out.println("Arguments are missing");
                    }
                    break;

                case "import-key":
                    if (args.length > 2) {
                        Importer.importKeys(args[1], args[2]);
                    } else {
                        System.out.println("Arguments are missing");
                    }
                    break;

                case "write-message":
                    if (args.length > 2) {
                        if (args.length == 5) {
                            MessageWriter.encrypt(args[1], args[2], args[3], args[4]);
                        } else if (args.length == 4) {
                            if (args[3].contains(".txt")) {
                                MessageWriter.encrypt(args[1], args[2], args[3], "");
                            } else {
                                MessageWriter.encrypt(args[1], args[2], "", args[3]);
                            }
                        } else if (args.length == 3) {
                            MessageWriter.encrypt(args[1], args[2], "", "");
                        }
                    } else {
                        System.out.println("Arguments are missing");
                    }
                    break;

                case "read-message":
                    MessageReader.decrypt(args[1]);
                    break;

                default:
                    System.out.println("Call beale, tap-code, case, "
                            + "create-user, delete-user, export-key, import-key, write-message, or read-message");
                    break;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
        }
    }
}

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

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
import org.xml.sax.SAXException;

public class Importer {
    public static void importKeys(String name, String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        if (path.contains("http")) {
            getFromHttp(name, path);
        } else {
            if (!path.contains(".xml")) {
                System.out.println("Wrong! The given file was is not valid.");
                System.exit(1);
            }

            int k = privateOrPublic(path);
            if (k == 1) {
                checkPrivateKey(name, path);
                checkPublicKey(name, path);
            } else if (k == 0) {
                checkPublicKey(name, path);
            }
        }
    }

    public static void getFromHttp(String name, String path) throws IOException, ParserConfigurationException, TransformerException {
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int code = con.getResponseCode();

        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            ArrayList<String> a = new ArrayList<>();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                a.add(inputLine);
            }

            in.close();

            //get index where '>' and '<' starts
            int z = a.get(1).indexOf(">", 0);
            int x = a.get(1).indexOf("<", 1);
            String Z = a.get(1).substring(z + 1, x);

            z = a.get(2).indexOf(">", 0);
            x = a.get(2).indexOf("<", 1);
            String X = a.get(2).substring(z + 1, x);

            createXml(name, Z, X);
        }
    }

    public static int privateOrPublic(String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        File dir = new File("c://Users//hp//Desktop//exported_keys");
        dir.mkdir();

        File fromFile = new File(dir.getPath() + "//" + path);
        if (!fromFile.exists()) {
            System.out.println("Wrong! " + path + " doesn't exist!");
            System.exit(1);
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document fromDoc = dBuilder.parse(fromFile);
        DOMSource sourcePub = new DOMSource(fromDoc);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformerPub = transformerFactory.newTransformer();
        transformerPub.setOutputProperty(OutputKeys.INDENT, "yes");

        File txtFile = new File("albin.txt");
        StreamResult resultPub = new StreamResult(txtFile);
        transformerPub.transform(sourcePub, resultPub);

        int i = 0, k = 0;
        ArrayList<String> sa = new ArrayList<>();
        Scanner scanner = new Scanner(txtFile);
        while (scanner.hasNextLine()) {
            sa.add(scanner.nextLine());
            if (sa.get(i).contains("<D>")) {
                k = 1;
                break;
            }
            i++;
        }

        scanner.close();

        return k;
    }

    public static void checkPrivateKey(String name, String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        File dir = new File("c://Users//hp//Desktop//exported_keys");
        dir.mkdir();

        File fromFile = new File(dir.getPath() + "//" + path);
        if (!fromFile.exists()) {
            System.out.println("Wrong! " + fromFile.getPath() + " given file doesn't exist!");
            System.exit(1);
        }

        Document doc = Exporter.getContentFromPrivateFile(fromFile);
        File toFile = new File("c://keys//" + name + ".xml");

        if (toFile.exists()) {
            System.out.println("Wrong! " + name + " file already exist!");
            System.exit(1);
        }

        TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
        Transformer transformer = transformerFactoryPub.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(toFile);
        System.out.println("The private key was saved in " + toFile.getPath());
        transformer.transform(source, result);
    }

    public static void checkPublicKey(String name, String path) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        File dir = new File("c://Users//hp//Desktop//exported_keys");
        dir.mkdir();

        File fromFile = new File(dir.getPath() + "//" + path);
        if (!fromFile.exists()) {
            System.out.println("Wrong! " + fromFile.getPath() + " given file doesn't exist!");
            System.exit(1);
        }

        Document docPub = Exporter.getContentFromPublicFile(fromFile);
        File toPubFile = new File("c://keys//" + name + ".pub.xml");

        if (toPubFile.exists()) {
            System.out.println("Wrong! " + name + " file already exist!");
            System.exit(1);
        }

        TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
        Transformer transformer = transformerFactoryPub.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource sourcePub = new DOMSource(docPub);

        StreamResult resultPub = new StreamResult(toPubFile);
        System.out.println("The public key was saved in " + toPubFile.getPath());
        transformer.transform(sourcePub, resultPub);
    }

    public static void createXml(String name, String module, String exponent) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactoryHTTP = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderHTTP = docFactoryHTTP.newDocumentBuilder();
        Document docHTTP = docBuilderHTTP.newDocument();

        Element rootElementHTTP = docHTTP.createElement("RSAKeyValue");
        docHTTP.appendChild(rootElementHTTP);

        Element ModulusHTTP = docHTTP.createElement("Modulus");
        ModulusHTTP.appendChild(docHTTP.createTextNode(module));
        rootElementHTTP.appendChild(ModulusHTTP);

        Element ExponentHTTP = docHTTP.createElement("Exponent");
        ExponentHTTP.appendChild(docHTTP.createTextNode(exponent));
        rootElementHTTP.appendChild(ExponentHTTP);

        TransformerFactory transformerFactoryHTTP = TransformerFactory.newInstance();
        Transformer transformerHTTP = transformerFactoryHTTP.newTransformer();
        transformerHTTP.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource sourceHTTP = new DOMSource(docHTTP);

        File keys = new File("c://keys");
        keys.mkdir();

        File httpFile = new File(keys.getPath() + "//" + name + ".pub.xml");
        StreamResult resultPub = new StreamResult(httpFile);
        System.out.println(httpFile.getPath() + " public key was created");
        transformerHTTP.transform(sourceHTTP, resultPub);
    }
}

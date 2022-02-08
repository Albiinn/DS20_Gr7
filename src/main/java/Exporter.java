import java.io.File;
import java.io.IOException;

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

public class Exporter {
    public static void exportKeys(String key, String name, String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        if (key.equalsIgnoreCase("private")) {
            copyToPrivateFile(name, path);
        } else if (key.equalsIgnoreCase("public")) {
            copyToPublicFile(name, path);
        }
    }

    public static void copyToPrivateFile(String name, String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        File keys = new File("c://keys");
        keys.mkdir();

        File fromFile = new File(keys.getPath() + "//" + name + ".xml");
        if (!fromFile.exists()) {
            System.out.println("Wrong! " + name + " private key doesn't exist!");
            System.exit(1);
        }

        Document doc = getContentFromPrivateFile(fromFile);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result;

        File dir = new File("c://Users//hp//Desktop//exported_keys");
        dir.mkdir();

        File file = new File(dir.getPath() + "//" + path);
        if (path.equals("a")) {
            result = new StreamResult(System.out);
        } else {
            result = new StreamResult(file);
            System.out.println("The private key was saved in " + file.getPath());
        }
        transformer.transform(source, result);
    }

    public static Document getContentFromPrivateFile(File fromFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document fromDoc = dBuilder.parse(fromFile);
        Document toDoc = dBuilder.newDocument();

        String modulus = fromDoc.getElementsByTagName("Modulus").item(0).getTextContent();
        String exponent = fromDoc.getElementsByTagName("Exponent").item(0).getTextContent();
        String p = fromDoc.getElementsByTagName("P").item(0).getTextContent();
        String q = fromDoc.getElementsByTagName("Q").item(0).getTextContent();
        String dp = fromDoc.getElementsByTagName("DP").item(0).getTextContent();
        String dq = fromDoc.getElementsByTagName("DQ").item(0).getTextContent();
        String inverseQ = fromDoc.getElementsByTagName("InverseQ").item(0).getTextContent();
        String d = fromDoc.getElementsByTagName("D").item(0).getTextContent();

        Element rootElement = toDoc.createElement("RSAKeyValue");
        toDoc.appendChild(rootElement);

        Element Modulus = toDoc.createElement("Modulus");
        Modulus.appendChild(toDoc.createTextNode(modulus));
        rootElement.appendChild(Modulus);

        Element Exponent = toDoc.createElement("Exponent");
        Exponent.appendChild(toDoc.createTextNode(exponent));
        rootElement.appendChild(Exponent);

        Element P = toDoc.createElement("P");
        P.appendChild(toDoc.createTextNode(p));
        rootElement.appendChild(P);

        Element Q = toDoc.createElement("Q");
        Q.appendChild(toDoc.createTextNode(q));
        rootElement.appendChild(Q);

        Element DP = toDoc.createElement("DP");
        DP.appendChild(toDoc.createTextNode(dp));
        rootElement.appendChild(DP);

        Element DQ = toDoc.createElement("DQ");
        DQ.appendChild(toDoc.createTextNode(dq));
        rootElement.appendChild(DQ);

        Element InverseQ = toDoc.createElement("InverseQ");
        InverseQ.appendChild(toDoc.createTextNode(inverseQ));
        rootElement.appendChild(InverseQ);

        Element D = toDoc.createElement("D");
        D.appendChild(toDoc.createTextNode(d));
        rootElement.appendChild(D);

        return toDoc;
    }

    public static void copyToPublicFile(String name, String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        File keys = new File("c://keys");
        keys.mkdir();

        File fromFile = new File(keys.getPath() + "//" + name + ".pub.xml");
        if (!fromFile.exists()) {
            System.out.println("Wrong! " + name + " public key doesn't exist!");
            System.exit(1);
        }

        Document doc = getContentFromPublicFile(fromFile);
        TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
        Transformer transformer = transformerFactoryPub.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        File dir = new File("c://Users//hp//Desktop//exported_keys");
        dir.mkdir();
        File file = new File(dir.getPath() + "//" + path.replace(".xml", "") + ".pub.xml");

        StreamResult result;
        if (path.equals("a")) {
            result = new StreamResult(System.out);
        } else {
            result = new StreamResult(file);
            System.out.println("The public key was saved in " + file.getPath());
        }
        transformer.transform(source, result);
    }

    public static Document getContentFromPublicFile(File fromFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document fromDoc = dBuilder.parse(fromFile);
        Document toDoc = dBuilder.newDocument();

        String modulus = fromDoc.getElementsByTagName("Modulus").item(0).getTextContent();
        String exponent = fromDoc.getElementsByTagName("Exponent").item(0).getTextContent();

        Element rootElementPub = toDoc.createElement("RSAKeyValue");
        toDoc.appendChild(rootElementPub);

        Element ModulusPub = toDoc.createElement("Modulus");
        ModulusPub.appendChild(toDoc.createTextNode(modulus));
        rootElementPub.appendChild(ModulusPub);

        Element ExponentPub = toDoc.createElement("Exponent");
        ExponentPub.appendChild(toDoc.createTextNode(exponent));
        rootElementPub.appendChild(ExponentPub);

        return toDoc;
    }
}

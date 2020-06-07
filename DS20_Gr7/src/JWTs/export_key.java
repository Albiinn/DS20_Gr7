package JWTs;

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

public class export_key {

public static Document getContentFromPrivateFile(File fromfile, Document doc) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document fromdoc = dBuilder.parse(fromfile);
		
		String modulus = fromdoc.getElementsByTagName("Modulus").item(0).getTextContent();
		String exponent = fromdoc.getElementsByTagName("Exponent").item(0).getTextContent();
		String p = fromdoc.getElementsByTagName("P").item(0).getTextContent();
		String q = fromdoc.getElementsByTagName("Q").item(0).getTextContent();
		String dp = fromdoc.getElementsByTagName("DP").item(0).getTextContent();
		String dq = fromdoc.getElementsByTagName("DQ").item(0).getTextContent();
		String inverseQ = fromdoc.getElementsByTagName("InverseQ").item(0).getTextContent();
		String d = fromdoc.getElementsByTagName("D").item(0).getTextContent();
		
		Element rootElement = doc.createElement("RSAKeyValue");
		doc.appendChild(rootElement);

		Element Modulus = doc.createElement("Modulus");
		Modulus.appendChild(doc.createTextNode(modulus));
		rootElement.appendChild(Modulus);

		Element Exponent = doc.createElement("Exponent");
		Exponent.appendChild(doc.createTextNode(exponent));
		rootElement.appendChild(Exponent);

		Element P = doc.createElement("P");
		P.appendChild(doc.createTextNode(p));
		rootElement.appendChild(P);

		Element Q = doc.createElement("Q");
		Q.appendChild(doc.createTextNode(q));
		rootElement.appendChild(Q);
		
		Element DP = doc.createElement("DP");
		DP.appendChild(doc.createTextNode(dp));
		rootElement.appendChild(DP);
		
		Element DQ = doc.createElement("DQ");
		DQ.appendChild(doc.createTextNode(dq));
		rootElement.appendChild(DQ);
		
		Element InverseQ = doc.createElement("InverseQ");
		InverseQ.appendChild(doc.createTextNode(inverseQ));
		rootElement.appendChild(InverseQ);
		
		Element D = doc.createElement("D");
		D.appendChild(doc.createTextNode(d));
		rootElement.appendChild(D);
		
		return doc;
	}
	
	
	public static Document getContentFromPublicFile(File fromfile, Document doc) throws ParserConfigurationException, SAXException, IOException {
		
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document fromdoc = dBuilder.parse(fromfile);

			String modulus = fromdoc.getElementsByTagName("Modulus").item(0).getTextContent();
			String exponent = fromdoc.getElementsByTagName("Exponent").item(0).getTextContent();

			Element rootElementPub = doc.createElement("RSAKeyValue");
			doc.appendChild(rootElementPub);

			Element ModulusPub = doc.createElement("Modulus");
			ModulusPub.appendChild(doc.createTextNode(modulus));
			rootElementPub.appendChild(ModulusPub);

			Element ExponentPub = doc.createElement("Exponent");
			ExponentPub.appendChild(doc.createTextNode(exponent));
			rootElementPub.appendChild(ExponentPub);
			
			return doc;
	}
	
	
	public static void CallPublicOrPrivate(String celesi, String emri, String shtegu) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		if(celesi.toLowerCase().equals("private")) {
			copyToPrivateFile(emri, shtegu);
		}
		else if(celesi.toLowerCase().equals("public")) {
			copyToPublicFile(emri, shtegu);
		}
	}
	
	
	public static void copyToPublicFile(String emri, String shtegu) throws ParserConfigurationException, SAXException, IOException, TransformerException {
				
				File keys = new File("c://keys");
				keys.mkdir();
				
				File fromfile = new File(keys.getPath()+"//"+emri+".pub.xml");
				
				if (!fromfile.exists()) {
					System.out.println("Gabim: Celesi publik "+emri+" nuk ekziston");
					System.exit(1);
				}
		
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				doc = getContentFromPublicFile(fromfile, doc);
				
				TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
				Transformer transformer = transformerFactoryPub.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				
				StreamResult result;
				
				File dir = new File("c://Users//hp//Desktop//exported_keys"); 
				dir.mkdir();
				File file = new File(dir.getPath()+"//"+shtegu.replace(".xml", "")+".pub.xml");
				
				if(shtegu.equals("a")) {
					result = new StreamResult(System.out);
					transformer.transform(source, result);
				}
				else {
					result = new StreamResult(file);
					System.out.println("Celesi publik u ruajt ne fajllin "+file.getPath());
					transformer.transform(source, result);
				}
	}
	
	
	public static void copyToPrivateFile(String emri, String shtegu) throws ParserConfigurationException, SAXException, IOException, TransformerException {
				
				File keys = new File("c://keys");
				keys.mkdir();
				
				File fromfile = new File(keys.getPath()+"//"+emri+".xml");
				
				if (!fromfile.exists()) {
					System.out.println("Gabim: Celesi privat "+emri+" nuk ekziston");
					System.exit(1);
				}
				
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				doc = getContentFromPrivateFile(fromfile, doc);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				StreamResult result;
				
				File dir = new File("c://Users//hp//Desktop//exported_keys");
				dir.mkdir();
				File file = new File(dir.getPath()+"//"+shtegu);
				
				if(shtegu.equals("a")) { 
					result = new StreamResult(System.out);
					transformer.transform(source, result);
				}
				else {
					result = new StreamResult(file);
					System.out.println("Celesi privat u ruajt ne fajllin "+file.getPath());
					transformer.transform(source, result);
				}
	}
	
}

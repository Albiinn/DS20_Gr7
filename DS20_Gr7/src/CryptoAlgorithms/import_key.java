package CryptoAlgorithms;

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

public class import_key {
	

		public static int PrivOrPub(String shtegu) throws ParserConfigurationException, SAXException, IOException, TransformerException {
			
			File dir = new File("c://Users//hp//Desktop//exported_keys");
			dir.mkdir();
			File fromfile = new File(dir.getPath()+"//"+shtegu);
			
			//fute file-n ne nje document
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document fromdoc = dBuilder.parse(fromfile);
			DOMSource sourcePub = new DOMSource(fromdoc);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformerPub = transformerFactory.newTransformer();
			transformerPub.setOutputProperty(OutputKeys.INDENT, "yes");
			
			File file = new File("albin.txt");
						
			StreamResult resultPub = new StreamResult(file);
			transformerPub.transform(sourcePub, resultPub);
						
			int i=0, k=0;
			ArrayList<String> sa = new ArrayList<String>();
			Scanner input = new Scanner(file);
			
			while(input.hasNextLine()) {
				
				sa.add(input.nextLine());
				
				if(sa.get(i).contains("<D>")) {
					k=1;
					break;
				}
				
				i++;
			}
			
			return k;
		}
	
		public static void ImportToFrom(String emri, String shtegu) throws ParserConfigurationException, SAXException, IOException, TransformerException {
			
			//nese shtegu permbane http, thirre metoden per get request
			if(shtegu.contains("http")) {
				http(emri, shtegu);
			}
			
			else {
				//kthen 1 nese eshte celes privat, 0 per publik
				int k = PrivOrPub(shtegu);
				if(k==1) {
				priv(emri, shtegu); 
				pub(emri, shtegu);
			}
				else if(k==0) {
				pub(emri,shtegu);
				}
			}
		}
		
		public static void http(String emri, String shtegu) throws IOException, ParserConfigurationException, TransformerException {
			
			URL url = new URL(shtegu);
			String inputLine;
			HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
			con.setRequestMethod("GET");
			int code = con.getResponseCode();
			
			if(code==HttpURLConnection.HTTP_OK) {
				
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				
				ArrayList<String> a = new ArrayList<String>();
				int i=0;
				
				while ((inputLine = in.readLine()) != null) {
					a.add(inputLine);
					i++;
				} 
				
				in.close();
				
				//marrja e indexit ku fillon > dhe <
				int z = a.get(1).indexOf(">", 0);
				int x = a.get(1).indexOf("<", 1);
				String Z = a.get(1).substring(z+1, x);
				
				z = a.get(2).indexOf(">", 0);
				x = a.get(2).indexOf("<", 1);
				String X = a.get(2).substring(z+1, x);
				
				KrijimiXML(emri, Z, X);
			}
			
		}
			
		
		public static void KrijimiXML(String emri, String modul, String exponent) throws ParserConfigurationException, TransformerException {
		
			DocumentBuilderFactory docFactoryHTTP = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilderHTTP = docFactoryHTTP.newDocumentBuilder();
			Document docHTTP = docBuilderHTTP.newDocument();
			
			Element rootElementHTTP = docHTTP.createElement("RSAKeyValue");
			docHTTP.appendChild(rootElementHTTP);
			
			Element ModulusHTTP = docHTTP.createElement("Modulus");
			ModulusHTTP.appendChild(docHTTP.createTextNode(modul));
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
			File fileHTTP = new File(keys.getPath()+"//"+emri+".pub.xml");
			
			StreamResult resultPub = new StreamResult(fileHTTP);
			System.out.println("Eshte krijuar celesi publik "+fileHTTP.getPath());
			transformerHTTP.transform(sourceHTTP, resultPub);
			
		}

		
		public static void priv(String emri, String shtegu) throws ParserConfigurationException, SAXException, IOException, TransformerException {
			
			File dir = new File("c://Users//hp//Desktop//exported_keys");
			dir.mkdir();
			File fromfile = new File(dir.getPath()+"//"+shtegu);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			doc = export_key.getContentFromPrivateFile(fromfile, doc);
			File tofile = new File("c://keys//"+emri+".xml");
			
			//mbyll programin nese ekziston file
			if (tofile.exists()) {
				System.out.println("Gabim: Celesi "+emri+" ekziston paraprakisht");
				System.exit(1);
			}
			
			//ruaje filen tek shteg i caktuar, si xml file
			TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
			Transformer transformer = transformerFactoryPub.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result= new StreamResult(tofile);
			System.out.println("Celesi privat u ruajt ne fajllin "+tofile.getPath());
			transformer.transform(source, result);
	
		}	
		
		public static void pub(String emri, String shtegu) throws SAXException, IOException, ParserConfigurationException, TransformerException {
			
			File dir = new File("c://Users//hp//Desktop//exported_keys");
			dir.mkdir();
			File fromfile = new File(dir.getPath()+"//"+shtegu);
			
			//fute file-n ne nje document
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docPub = dBuilder.newDocument();
			docPub = export_key.getContentFromPublicFile(fromfile, docPub);
			File tofilePub = new File("c://keys//"+emri+".pub.xml");
			
			if (tofilePub.exists()) {
				System.out.println("Gabim: Celesi "+emri+" ekziston paraprakisht");
				System.exit(1);
			}
			
			TransformerFactory transformerFactoryPub = TransformerFactory.newInstance();
			Transformer transformer = transformerFactoryPub.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource sourcePub = new DOMSource(docPub);
			StreamResult resultPub= new StreamResult(tofilePub);
			System.out.println("Celesi publik u ruajt ne fajllin "+tofilePub.getPath());
			transformer.transform(sourcePub, resultPub);
		}

	}


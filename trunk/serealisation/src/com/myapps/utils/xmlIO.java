package com.myapps.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import com.myapps.Camera;

import android.util.Log;
import android.util.Xml;

/**
 * 
 * xmlIO offer two input/output function able to convert ArrayList<Camera> to
 * xml file and vice versa, like : <?xml version='1.0' encoding='UTF-8'
 * standalone='yes' ?> <camList> <camera> <id>home</id>
 * <adresse>http://192.168.1.20/</adresse> <channel>1</channel> </camera>
 * <camera> <id>home ext</id> <adresse>http://82.---.---.---/</adresse>
 * <channel>1</channel> </camera> </camList>
 * 
 */
public class xmlIO {
    public static void xmlCreateCamera(Camera c, String url, String name) {
	ArrayList<Camera> camList = new ArrayList<Camera>();
	camList.add(c);
	xmlWrite(camList, url, name);
	
    }

    public static String xmlCameraToString(Camera c) {
	String res = null;
	String fileName = "tmp";
	xmlCreateCamera(c, ".", fileName);

	InputStream ips;
	try {
	    ips = new FileInputStream(fileName);

	    InputStreamReader ipsr = new InputStreamReader(ips);
	    BufferedReader br = new BufferedReader(ipsr);
	    String ligne;
	    while ((ligne = br.readLine()) != null) {
		System.out.println(ligne);
		res += ligne + "\n";
	    }
	    br.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return null;
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
	return res;
    }

    /**
     * Write the ArrayList<Camera> into file named url
     * 
     * @param camList
     *            Your data
     * @param url
     *            File name
     */
    public static boolean xmlWrite(ArrayList<Camera> camList, String url,
	    String name) {
	FileOutputStream out = null;
	try {
	    File f = new File(url);
	    if (!f.exists()) {
		f.mkdir();
	    }
	    out = new FileOutputStream(url + name);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return false;
	}
	XmlSerializer serializer = Xml.newSerializer();
	try {
	    serializer.setOutput(out, "UTF-8");
	    serializer.startDocument(null, Boolean.valueOf(true));
	    serializer.startTag(null, "camList");
	    for (int i = 0; i < camList.size(); i++) {
		serializer.startTag(null, "camera");
		serializer.startTag(null, "id");
		serializer.text(camList.get(i).getId());
		serializer.endTag(null, "id");
		serializer.startTag(null, "adresse");
		serializer.text(camList.get(i).getURI());
		serializer.endTag(null, "adresse");
		serializer.startTag(null, "channel");
		serializer.text("" + camList.get(i).getChannel());
		serializer.endTag(null, "channel");
		serializer.endTag(null, "camera");
	    }
	    serializer.endTag(null, "camList");
	    serializer.endDocument();
	    serializer.flush();
	    out.close();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	    return false;
	} catch (IllegalStateException e) {
	    e.printStackTrace();
	    return false;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    /**
     * Read the xml file named url and return the ArrayList<Camera> completed
     * 
     * @param url
     *            The file url
     * @return The camera's list
     */
    public static ArrayList<Camera> xmlRead(String url) {
	Document doc = null;
	ArrayList<Camera> camList = new ArrayList<Camera>();
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    FileInputStream xml = new FileInputStream(url);
	    Log.e("Exception", "file open");
	    doc = db.parse(xml);
	    doc.getDocumentElement().normalize();
	    NodeList nodeList = doc.getElementsByTagName("camera");

	    for (int i = 0; i < nodeList.getLength(); i++) {
		Node node = nodeList.item(i);
		Element fstElmnt = (Element) node;

		NodeList idList = fstElmnt.getElementsByTagName("id");
		Element idElement = (Element) idList.item(0);
		idList = idElement.getChildNodes();
		String id = ((Node) idList.item(0)).getNodeValue();

		NodeList uriList = fstElmnt.getElementsByTagName("adresse");
		Element uriElement = (Element) uriList.item(0);
		uriList = uriElement.getChildNodes();
		String uri = ((Node) uriList.item(0)).getNodeValue();

		NodeList chanList = fstElmnt.getElementsByTagName("channel");
		Element chanElement = (Element) chanList.item(0);
		chanList = chanElement.getChildNodes();
		String chan = ((Node) chanList.item(0)).getNodeValue();

		Camera tmp = new Camera(id, uri, Integer.parseInt(chan));
		camList.add(tmp);
	    }
	    return camList;
	} catch (IOException e) {
	    Log.e("Exception", "error occurred while reading xml file");
	    return null;
	} catch (ParserConfigurationException e) {
	    Log.e("Exception", "error occurred while configure xml file");
	    e.printStackTrace();
	    return null;
	} catch (SAXException e) {
	    Log.e("Exception", "error occurred while parsing xml file");
	    e.printStackTrace();
	    return null;
	}
    }
}

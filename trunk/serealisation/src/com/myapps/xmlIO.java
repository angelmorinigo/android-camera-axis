package com.myapps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class xmlIO {
    public static void xmlWrite(ArrayList<Camera> camList, String url) {
	FileOutputStream fileos = null;
	try {
	    fileos = new FileOutputStream(url);
	} catch (FileNotFoundException e) {
	    Log.e("FileNotFoundException", "can't create FileOutputStream");
	}
	XmlSerializer serializer = Xml.newSerializer();
	try {
	    serializer.setOutput(fileos, "UTF-8");
	    serializer.startDocument(null, Boolean.valueOf(true));
	    serializer.setFeature(
		    "http://xmlpull.org/v1/doc/features.html#indent-output",
		    true);

	    serializer.startTag(null, "camList");
	    for (int i = 0; i < camList.size(); i++) {
		serializer.startTag(null, "camera");
		serializer.startTag(null, "id");
		serializer.text(camList.get(i).id);
		serializer.endTag(null, "id");
		serializer.startTag(null, "adresse");
		serializer.text(camList.get(i).uri);
		serializer.endTag(null, "adresse");
		serializer.startTag(null, "channel");
		serializer.text("" + camList.get(i).channel);
		serializer.endTag(null, "channel");
		serializer.endTag(null, "camera");
	    }

	    serializer.endTag(null, "camList");
	    serializer.endDocument();
	    serializer.flush();
	    fileos.close();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (IllegalStateException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

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
	} catch (IOException e) {
	    Log.e("Exception", "error occurred while reading xml file");
	} catch (ParserConfigurationException e) {
	    Log.e("Exception", "error occurred while configure xml file");
	    e.printStackTrace();
	} catch (SAXException e) {
	    Log.e("Exception", "error occurred while parsing xml file");
	    e.printStackTrace();
	}

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
    }
}

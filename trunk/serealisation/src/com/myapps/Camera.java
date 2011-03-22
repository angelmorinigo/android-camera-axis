package com.myapps;

import java.net.URI;
import java.net.URISyntaxException;

import android.net.Uri;

/**
 * Camera class describe information's camera
 */
public class Camera implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	protected String id, login, pass;
	public String ip;
	protected String protocol;
	URI uri;
	public int port;
	protected int channel, uniqueID = -1, groupeID = -1;

	/**
	 * Public constructor for a camera with information
	 * 
	 * @param id
	 *            The camera name
	 * @param login
	 *            Your login for private acces
	 * @param pass
	 *            Your pass for private acces
	 * @param ip
	 *            Ip's Camera
	 * @param port
	 *            Port's Camera
	 * @param protocol
	 *            Only "http" at the moment
	 * @param channel
	 *            The channel's camera (no channel = 1)
	 */
	public Camera(String id, String login, String pass, String ip, int port,
			String protocol, int channel) {
		this.id = id;
		this.login = login;
		this.pass = pass;
		this.ip = ip;
		this.port = port;
		this.protocol = protocol;
		this.channel = channel;
		try {
		    this.uri = new URI("http", null, ip , port, null, null, null);
		} catch (URISyntaxException e) {
		    uri = null;
		    e.printStackTrace();
		}
	}

	/**
	 * Public constructor for a camera with a URI form QrCode
	 * 
	 * @param id
	 *            The name of th camera
	 * @param login
	 *            Your login for private acces
	 * @param pass
	 *            Your pass for private acces
	 * @param uri
	 *            The address's camera
	 * @param channel
	 *            The channel's camera (no channel = 1)
	 */
	public Camera(String id, String login, String pass, String uri, int channel) {
		this.id = id;
		this.login = login;
		this.pass = pass;
		try {
		    this.uri = new URI(uri);
		} catch (URISyntaxException e) {
		    e.printStackTrace();
		}
		this.channel = channel;
	}

	/**
	 * Public constructor for a camera with a URI without username/password
	 * 
	 * @param id
	 *            The name of th camera
	 * @param uri
	 *            The address's camera
	 * @param channel
	 *            The channel's camera (no channel = 1)
	 */
	public Camera(String id, String uri, int channel) {
		this.id = id;
		try {
		    this.uri = new URI(uri);
		} catch (URISyntaxException e) {
		    e.printStackTrace();
		}
		this.channel = channel;
		this.login = "";
		this.pass = "";
	}

	/**
	 * Get the address's camera
	 * 
	 * @return address's camera
	 */
	public URI getAddress() {
		return uri;
	}
	
	/**
	 * Get the address's camera
	 * 
	 * @return address's camera
	 */
	public String getURI() {
		return uri.toString()+"/";
	}

	/**
	 * Get the camera ID
	 * 
	 * @return camera ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the camera channel
	 * 
	 * @return camera channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * toString with camera name
	 * 
	 * @return camera.id
	 */
	@Override
	public String toString() {
		return id;
	}

	public void setUniqueID(int id) {
		uniqueID = id;
	}

	public void setGroup(int group) {
		groupeID = group;
	}

	public int getMotionDetectionID(int startID) {
		return groupeID + (uniqueID * 10) + startID;
	}

	protected Object clone() throws CloneNotSupportedException {
		Camera clone;
		clone = (Camera) super.clone();
		return clone;
	}
}
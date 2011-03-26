package com.myapps;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Describes information of a camera
 */
public class Camera implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	protected String id, login, pass;
	public String ip;
	protected String protocol;
	URI uri;
	public int port;
	protected int channel, uniqueID = -1;
	public int groupID = -1;

	/**
	 * Public constructor for a camera with information
	 * @param id The name of the camera
	 * @param login The login for private access
	 * @param pass The pass for private access
	 * @param ip The IP of the camera
	 * @param port The port of the camera
	 * @param protocol Only "http" at the moment
	 * @param channel The channel of the camera (no channel = 1)
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
	 * @param id The name of the camera
	 * @param login The login for private access
	 * @param pass The pass for private access
	 * @param uri The address of the camera
	 * @param channel The channel of the camera (no channel = 1)
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
	 * Public constructor for a camera with a URI but without username/password
	 * @param id The name of the camera
	 * @param uri The address of the camera
	 * @param channel The channel of the camera (no channel = 1)
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
	 * Get the address of the camera
	 * @return The address of the camera
	 */
	public URI getAddress() {
		return uri;
	}
	
	/**
	 * Get the address of the camera
	 * @return The string representation of the address of the camera
	 */
	public String getURI() {
		return uri.toString()+"/";
	}

	/**
	 * Get the camera ID
	 * @return camera ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the channel of the camera
	 * @return The channel of the camera
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * toString with camera name
	 * @return camera.id
	 */
	@Override
	public String toString() {
		return id;
	}

	/**
	 * Set the camera channel
	 * @return The channel of the camera
	 */
	public void setUniqueID(int id) {
		uniqueID = id;
	}

	/**
	 * Set the groupID used for Motion Detection
	 * @param group for Motion Detection
	 */
	public void setGroup(int group) {
		groupID = group;
	}

	/**
	 * Get Motion Detection ID
	 * @param startID base ID to get a unique Motion Detection ID
	 * @return unique Motion Detection ID
	 */
	public int getMotionDetectionID(int startID) {
		return groupID + (uniqueID * 10) + startID;
	}

	/**
	 * Clone the Camera object
	 * @return A clone of Camera object
	 * @throws CloneNotSupportedException
	 */
	protected Object clone() throws CloneNotSupportedException {
		Camera clone;
		clone = (Camera) super.clone();
		return clone;
	}
	
}
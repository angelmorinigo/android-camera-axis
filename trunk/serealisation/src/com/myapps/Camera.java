package com.myapps;

public class Camera implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    protected String id, login, pass, ip, protocol, uri;
    protected int port, channel;

    public Camera(String id, String login, String pass, String ip, int port,
	    String protocol, int channel) {
	this.id = id;
	this.login = login;
	this.pass = pass;
	this.ip = ip;
	this.port = port;
	this.protocol = protocol;
	this.channel = channel;
	this.uri = makeURL(ip, port, protocol);
    }

    public Camera(String id, String login, String pass, String uri, int channel) {
	this.id = id;
	this.login = login;
	this.pass = pass;
	this.uri = uri;
    }

    private String makeURL(String ip, int port, String protocol) {
	return "" + protocol + "://" + ip + ":" + port + "/";
    }

    public String getURI(){
	return uri;
    }
    
    public String toString() {
	return id;
    }
}
package com.myapps;

public class Camera implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	protected String id, login, pass, ip, protocol;
	protected int port, channel;

	private String UrlSuffixeAxProtocol = "/mpeg4/1/media.amp";
	private String UrlSuffixeHTTPProtocol = "/axis-cgi/mjpg/video.cgi";

	protected Camera(String id, String login, String pass, String ip,
			int port, String protocol, int channel) {
		this.id = id;
		this.login = login;
		this.pass = pass;
		this.ip = ip;
		this.port = port;
		this.protocol= protocol;
		this.channel=channel;
	}
	
	
	public String getUrl(){
		String uri;
		if (protocol.equalsIgnoreCase("http"))
			uri = protocol + "://" + ip + ":" + port + UrlSuffixeHTTPProtocol;
		else
			uri = protocol + "://" + ip + ":" + port + UrlSuffixeAxProtocol;
		return uri;
	}
	
	public String toString(){
		return id;
	}
}
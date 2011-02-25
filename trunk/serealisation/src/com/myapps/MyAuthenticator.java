package com.myapps;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * 
 * NEVER USE DELETE AT THE END
 *
 */
public class MyAuthenticator extends Authenticator {
	String user;
	String pass;
	public MyAuthenticator(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, pass.toCharArray());
	}
}

package com.myapps;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class CameraControl {
	private static final long serialVersionUID = 1L;
	protected Camera cam;
	
	private boolean ptzEnabled;
	private int minPan;
	private int minTilt;
	private int minZoom;
	private int maxPan;
	private int maxTilt;
	private int maxZoom;	
	
	public CameraControl(Camera cam) {
		this.cam = cam;
	}
	
	public boolean isPTZEnabled() {
        return ptzEnabled;
    }
	
	public boolean isPTZAvailable() {
		/*String result = sendCommand("axis-cgi/com/ptz.cgi?info=1&camera=" + String.valueOf(cam.channel));
		if (result.equals("true")) {
			ptzEnabled = true;
		}*/
		return false;
	}
	
	private String createURL() {
		return "http://" + cam.ip + ":" + String.valueOf(cam.port) + "/";
	}
	
	private String sendCommand(String command) {
        URL url = null;
        URLConnection connection = null;
        DataInputStream result = null;
        
        try {
            url = new URL(createURL() + command);
            connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();
            
            result = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
        } catch (IOException e) {
            Log.i("Applog", e.getMessage());
        }
        
        finally {
            connection = null;
            url = null;
        }
        
        return result.toString();
    }
}

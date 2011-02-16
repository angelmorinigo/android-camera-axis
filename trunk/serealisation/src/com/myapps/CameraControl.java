package com.myapps;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images;
import android.util.Log;

public class CameraControl {
	private static final long serialVersionUID = 1L;
	protected Camera cam;
	
	private boolean ptzEnabled;
	private boolean motionEnabled;
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
		String result = sendCommand("axis-cgi/com/ptz.cgi?info=1&camera=" + String.valueOf(cam.channel));
		if (result.equals("true")) {
			ptzEnabled = true;
		}
		return false;
	}
	
	public void getConfig() {
		
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
	
	public void zoom(int zoomVal) {
		sendCommand("axis-cgi/com/ptz.cgi?zoom" + String.valueOf(zoomVal) + "&camera=" + String.valueOf(cam.channel));
	}
	
	public void move(float panVal, float tiltVal) {
		sendCommand("axis-cgi/com/ptz.cgi?info=1&camera=" + String.valueOf(cam.channel));
	}
	
	public Bitmap takeSnapshot() {
		Bitmap bmp = null;
		String url = "axis-cgi/com/ptz.cgi?info=1&camera=" + String.valueOf(cam.channel);
		try {
            bmp = BitmapFactory.decodeStream((InputStream)new URL(this.createURL() + url).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return bmp;
	}
	
	public void saveImage() {
		
	}
}

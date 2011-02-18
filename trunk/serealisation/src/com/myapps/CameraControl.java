package com.myapps;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
		DataInputStream result;
		try {
			HttpURLConnection con = sendCommand("axis-cgi/com/ptz.cgi?info=1&camera="
					+ String.valueOf(cam.channel));
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void getConfig() {

	}

	private String createURL() {
		return "http://" + cam.ip /* + ":" + String.valueOf(cam.port) */+ "/";
	}

	private HttpURLConnection sendCommand(String command) {
		URL url = null;
		HttpURLConnection connection = null;
		DataInputStream result = null;

		try {
			url = new URL(createURL() + command);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization",
					base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
			connection.connect();
			return connection;

			/*
			 * c'est preferable de recuperer directement la connection :) result
			 * = new DataInputStream(new BufferedInputStream(
			 * connection.getInputStream()));
			 */
		} catch (IOException e) {
			Log.i("Applog", e.getMessage());
		}

		finally {
			connection = null;
			url = null;
		}

		return connection;
	}

	public void zoom(int zoomVal) {
		sendCommand("axis-cgi/com/ptz.cgi?zoom" + String.valueOf(zoomVal)
				+ "&camera=" + String.valueOf(cam.channel));
	}

	public void move(float panVal, float tiltVal) {
		sendCommand("axis-cgi/com/ptz.cgi?info=1&camera="
				+ String.valueOf(cam.channel));
	}

	public Bitmap takeSnapshot(String resolution) throws IOException {
		
		URLConnection con = sendCommand("axis-cgi/jpg/image.cgi" + "?resolution=" + resolution);
		Log.i("AppLog", "connected");
		InputStream stream = con.getInputStream();
		Bitmap bmp = BitmapFactory.decodeStream(stream);
		stream.close();
		return bmp;
	}

	public void saveImage() {

	}
}

package com.myapps;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CameraControl {
	private static final long serialVersionUID = 1L;
	
	public static final int PAN = 0;
	public static final int TILT = 1;
	public static final int ZOOM = 2;
	public static final int FOCUS = 3;
	public static final int IRIS = 4;
	public static final int AUTOFOCUS = 5;
	public static final int AUTOIRIS = 6;
	public static final int IR_FILTER = 7;
	public static final int AUTO_IR = 8;
	public static final int BACKLIGHT = 9;
	public static final int MOTION = 10;
	public static final int AUDIO = 11;
	
	public static final int NB_FUNC = 12;
	public static final int NB_BASIC_FUNC = 5;
		
	public static final int NOT_SUPPORTED = -1;
	public static final int DISABLED = 0;
	public static final int ENABLED = 1;
	
	public static final char ABSOLUTE = 1;
	public static final char RELATIVE = 2;
	public static final int DIGITAL = 4;
	public static final int AUTO = 8;
	public static final int CONTINUOUS = 16;
	
	private static final int TOP = 0;
	private static final int RIGHT = 1;
	private static final int BOTTOM = 2;
	private static final int LEFT = 3;
	private static final int SENSITIVITY = 4;
	private static final int HISTORY = 5;
	private static final int OBJECT_SIZE = 6;
	
	private Camera cam;

	private int[] currentConfig = new int[NB_FUNC];
	private int[] functionProperties = new int[NB_BASIC_FUNC];
	private float[] motionParams = new float[7];

	public CameraControl(Camera cam) {
		this.cam = cam;
		this.initConfig();
		this.loadConfig(-1);
	}
	
	public void initConfig() {
		for (int i = 0; i < CameraControl.NB_FUNC; i++) {
			this.currentConfig[i] = -1;
		}
	}
	
	public void loadConfig(int function) {
		HttpURLConnection con;
		InputStream result;
		String param = "";
		int sum;
		
		try {
			con = sendCommand("axis-cgi/com/ptz.cgi?info=1&camera="
					+ String.valueOf(cam.channel));
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				result = new BufferedInputStream(con.getInputStream());
				for (int i = 0; i < CameraControl.NB_FUNC - 2; i++) {
					sum = 0;
					switch (function) {
					case CameraControl.PAN:
						param = "Pan";
						break;
					case CameraControl.TILT:
						param = "Tilt";
						break;
					case CameraControl.ZOOM:
						param = "Zoom";
						break;
					case CameraControl.FOCUS:
						param = "Focus";
						break;
					case CameraControl.IRIS:
						param = "Iris";
						break;
					case CameraControl.AUTOFOCUS:
						param = "AutoFocus";
						break;
					case CameraControl.AUTOIRIS:
						param = "AutoIris";
						break;
					case CameraControl.IR_FILTER:
						param = "IrCutFilter";
						break;
					case CameraControl.AUTO_IR:
						param = "AutoIrCutFilter";
						break;
					case CameraControl.BACKLIGHT:
						param = "Backlight";
						break;	
					}
					if (function >= 0 && function <= CameraControl.NB_BASIC_FUNC) {
						sum = (result.toString().contains("Absolute" + param + "=true") ? CameraControl.ABSOLUTE : 0)
							+ (result.toString().contains("Relative" + param + "=true") ? CameraControl.RELATIVE : 0)
							+ (result.toString().contains("Continuous" + param + "=true") ? CameraControl.CONTINUOUS : 0);
						
						if (function == CameraControl.ZOOM)
							sum += result.toString().contains("DigitalZoom=true") ? CameraControl.DIGITAL : 0;
						else if (function == CameraControl.FOCUS || function == CameraControl.IRIS)
							sum += result.toString().contains("Auto" + param + "=true") ? CameraControl.AUTO : 0;
						
						this.functionProperties[function] = sum;
						if (sum > 0) {
							this.currentConfig[function] = result.toString().contains(param + "Enabled=true")
									? CameraControl.ENABLED : CameraControl.DISABLED;
						}
					} else if (function == CameraControl.AUTOFOCUS || function == CameraControl.AUTOIRIS) {
						this.currentConfig[function] = result.toString().contains(param + "=true")
								? CameraControl.ENABLED : CameraControl.DISABLED;
					} else if (function == CameraControl.IR_FILTER || function == CameraControl.BACKLIGHT) {
						this.currentConfig[function] = result.toString().contains(param + "Enabled=true")
								? CameraControl.DISABLED : CameraControl.NOT_SUPPORTED;
						if (function == CameraControl.IR_FILTER)
							this.currentConfig[function] = (result.toString().contains(param + "=on")
									|| result.toString().contains(param + "=auto"))
									? CameraControl.ENABLED : CameraControl.DISABLED;
						else
							this.currentConfig[function] = result.toString().contains(param + "=true")
									? CameraControl.ENABLED : CameraControl.DISABLED;
					} else if (function == CameraControl.AUTO_IR) {
						this.currentConfig[function] = result.toString().contains(param + "=true")
								? CameraControl.DISABLED : CameraControl.NOT_SUPPORTED;
						this.currentConfig[function] = result.toString().contains("IrCutFilter" + "=auto")
								? CameraControl.ENABLED : CameraControl.DISABLED;
					}
				}
			}
			
			con = sendCommand("axis-cgi/admin/param.cgi?action=list&group=Properties.Motion,Properties.Audio");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				result = new BufferedInputStream(con.getInputStream());
				this.currentConfig[CameraControl.MOTION] = result.toString().contains("Properties.Motion=yes")
						? CameraControl.DISABLED : CameraControl.NOT_SUPPORTED;
				this.currentConfig[CameraControl.AUDIO] = result.toString().contains("Properties.Audio=yes")
				? CameraControl.DISABLED : CameraControl.NOT_SUPPORTED;
			}
			
			result = null;
			con = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isEnabled(int function) {
		if (function < 0 || function >= CameraControl.NB_FUNC)
			return false;
		return currentConfig[function] == CameraControl.ENABLED;
	}
	
	public boolean isSupported(int function) {
		if (function < 0 || function >= CameraControl.NB_FUNC)
			return false;
		return currentConfig[function] != CameraControl.NOT_SUPPORTED;
	}
	
	public int enableFunction(int function) {
		if (function < 0 || function >= CameraControl.NB_BASIC_FUNC)
			return 0;
		if (!isSupported(function))
			return -1;
		this.currentConfig[function] = CameraControl.ENABLED;
		return 1;
	}
	
	public int disableFunction(int function) {
		if (function < 0 || function >= CameraControl.NB_BASIC_FUNC)
			return 0;
		if (!isSupported(function))
			return -1;
		this.currentConfig[function] = CameraControl.DISABLED;
		return 1;
	}

	private String createURL() {
		return "http://" + cam.ip /* + ":" + String.valueOf(cam.port) */+ "/";
	}

	private HttpURLConnection sendCommand(String command) {
		URL url = null;
		HttpURLConnection con = null;

		try {
			url = new URL(createURL() + command);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Authorization",
					base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
			con.connect();
			return con;

			/*
			 * c'est preferable de recuperer directement la connection :) result
			 * = new DataInputStream(new BufferedInputStream(
			 * connection.getInputStream()));
			 */
		} catch (IOException e) {
			Log.i("Applog", e.getMessage());
		}

		finally {
			con = null;
			url = null;
		}

		return con;
	}

	
	public int changeValFunc(int function, float value1, float value2) {
		if (function < 0 || function >= CameraControl.NB_BASIC_FUNC)
			return 0;
		if (!isSupported(function))
			return -1;
		
		String query = "";
		switch (function) {
		case CameraControl.PAN:
		case CameraControl.TILT:
			query = "rpan=" + value1 + "&rtilt=" + value2;
			break;
		case CameraControl.ZOOM:
			query = "rzoom=" + value1;
			break;
		case CameraControl.FOCUS:
			query = "rfocus=" + value1;
			break;
		case CameraControl.IRIS:
			query = "riris=" + value1;
			break;	
		}		
		try {
			HttpURLConnection con = sendCommand("axis-cgi/com/ptz.cgi?" + query
					+ "&camera=" + String.valueOf(cam.channel));
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream result = new BufferedInputStream(con.getInputStream());
				return (result.toString().contains("OK") ? 1 : 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * Active / désactive l'autofocus ou l'autoiris
	 */
	public int switchAutoFunc(int function) {
		if (function != CameraControl.AUTOFOCUS && function != CameraControl.AUTOIRIS)
			return 0;
		if (!isSupported(function))
			return -1;
		
		String param = (function == CameraControl.AUTOFOCUS) ? "autofocus" : "autoiris";
		String val = isEnabled(function) ? "off" : "on";
		try {
			HttpURLConnection con = sendCommand("axis-cgi/com/ptz.cgi?" + param + "=" + val
					+ "&camera=" + String.valueOf(cam.channel));
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream result = new BufferedInputStream(con.getInputStream());
				return (result.toString().contains("OK") ? 1 : 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
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

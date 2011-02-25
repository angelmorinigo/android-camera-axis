package com.myapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 
 * CameraControl class implements control's camera like PTZ, Snapshot, iris,
 * focus, etc.
 * 
 */
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

    private static final int NB_FUNC = 12;
    private static final int NB_BASIC_FUNC = 5;

    public static final int NOT_SUPPORTED = -1;
    public static final int DISABLED = 0;
    public static final int ENABLED = 1;

    public static final char ABSOLUTE = 1;
    public static final char RELATIVE = 2;
    public static final int DIGITAL = 4;
    public static final int AUTO = 8;
    public static final int CONTINUOUS = 16;

    // paramètres de config du Motion Detection
    private static final int TOP = 0;
    private static final int RIGHT = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT = 3;
    private static final int SENSITIVITY = 4;
    private static final int HISTORY = 5;
    private static final int OBJECT_SIZE = 6;

    private Camera cam;
    private int timeout = 2000;
    private int[] currentConfig = new int[NB_FUNC];
    private int[] functionProperties = new int[NB_BASIC_FUNC];
    private float[] motionParams = new float[7];

    public CameraControl(Camera cam) {
	this.cam = cam;
	this.initConfig();
	this.loadConfig(-1);
    }

    private void initConfig() {
	for (int i = 0; i < CameraControl.NB_FUNC; i++) {
	    this.currentConfig[i] = -1;
	}
    }

    private void loadConfig(int function) {
	HttpURLConnection con = null;
	InputStream result = null;
	String line, property = null, value = null;

	try {
	    con = sendCommand("axis-cgi/com/ptz.cgi?info=1&camera=1");
	    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
		result = con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(
			result));
		while ((line = in.readLine()) != null) {
		    if (line.indexOf("=") > -1) {
			property = line.substring(0, line.indexOf("=")).trim();
			value = line.substring(line.indexOf("=") + 1);
			System.out.println(property + "=" + value);
			if (property.contains("pan")) {
			    if (property.contentEquals("pan"))
				this.currentConfig[CameraControl.PAN] += CameraControl.ABSOLUTE;
			    else if (property.contentEquals("rpan"))
				this.currentConfig[CameraControl.PAN] += CameraControl.RELATIVE;
			    else if (property
				    .contentEquals("continuouspantiltmove")) {
				this.currentConfig[CameraControl.PAN] += CameraControl.CONTINUOUS;
				this.currentConfig[CameraControl.TILT] += CameraControl.CONTINUOUS;
			    }
			}
		    } else if (property.contains("tilt")) {
			if (property.contentEquals("tilt"))
			    this.currentConfig[CameraControl.TILT] += CameraControl.ABSOLUTE;
			else if (property.contentEquals("rtilt"))
			    this.currentConfig[CameraControl.TILT] += CameraControl.RELATIVE;
		    } else if (property.contains("zoom")) {
			if (property.contentEquals("zoom"))
			    this.currentConfig[CameraControl.ZOOM] += CameraControl.ABSOLUTE;
			else if (property.contentEquals("rzoom"))
			    this.currentConfig[CameraControl.ZOOM] += CameraControl.RELATIVE;
			else if (property.contentEquals("continuouszoommove"))
			    this.currentConfig[CameraControl.ZOOM] += CameraControl.CONTINUOUS;
			else if (property.contentEquals("digitalzoom"))
			    this.currentConfig[CameraControl.ZOOM] += CameraControl.DIGITAL;
		    } else if (property.contains("focus")) {
			if (property.contentEquals("focus"))
			    this.currentConfig[CameraControl.FOCUS] += CameraControl.ABSOLUTE;
			else if (property.contentEquals("rfocus"))
			    this.currentConfig[CameraControl.FOCUS] += CameraControl.RELATIVE;
			else if (property.contentEquals("continuousfocusmove"))
			    this.currentConfig[CameraControl.FOCUS] += CameraControl.CONTINUOUS;
			else if (property.contentEquals("autofocus")) {
			    this.currentConfig[CameraControl.FOCUS] += CameraControl.AUTO;
			    this.currentConfig[CameraControl.AUTOFOCUS] = CameraControl.DISABLED;
			}
		    } else if (property.contains("iris")) {
			if (property.contentEquals("iris"))
			    this.currentConfig[CameraControl.IRIS] += CameraControl.ABSOLUTE;
			else if (property.contentEquals("riris"))
			    this.currentConfig[CameraControl.IRIS] += CameraControl.RELATIVE;
			else if (property.contentEquals("continuousirismove"))
			    this.currentConfig[CameraControl.IRIS] += CameraControl.CONTINUOUS;
			else if (property.contentEquals("autoiris")) {
			    this.currentConfig[CameraControl.IRIS] += CameraControl.AUTO;
			    this.currentConfig[CameraControl.AUTOFOCUS] = CameraControl.DISABLED;
			}
		    } else if (property.contentEquals("ircutfilter")) {
			this.currentConfig[CameraControl.IR_FILTER] = CameraControl.DISABLED;
			if (value.contains("auto"))
			    this.currentConfig[CameraControl.AUTO_IR] = CameraControl.DISABLED;
		    } else if (property.contentEquals("backlight")) {
			this.currentConfig[CameraControl.BACKLIGHT] = CameraControl.DISABLED;
		    }
		}

		for (int i = 0; i < CameraControl.NB_BASIC_FUNC; i++)
		    if (this.currentConfig[i] > 0)
			this.currentConfig[i] = CameraControl.ENABLED;

		con = sendCommand("axis-cgi/admin/param.cgi?action=list&group=Properties.Motion,Properties.Audio");
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    result = con.getInputStream();
		    in = new BufferedReader(new InputStreamReader(result));
		    while ((line = in.readLine()) != null) {
			System.out.println(line);
			if (line.contains("Properties.Motion.Motion=yes"))
			    this.currentConfig[CameraControl.MOTION] = CameraControl.DISABLED;
			if (line.contains("Properties.Audio.Audio=yes"))
			    this.currentConfig[CameraControl.AUDIO] = CameraControl.DISABLED;
		    }
		}
	    }
	} catch (Exception e) {
	    con = null;
	    result = null;
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

    /**
     * Get address's camera
     * 
     * @return address's camera
     */
    private String createURL() {
	return cam.getURI();
    }

    /**
     * Open a HttpURLConnection to (camera.getUri+command) with authorization
     * Exemple : camera.getUri = http://192.168.1.2/ command =
     * axis-cgi/com/ptz.cgi?info=1&camera=1
     * 
     * @param command
     * @return The HttpURLConnection
     */
    public HttpURLConnection sendCommand(String command) throws IOException {
	URL url = null;
	HttpURLConnection con = null;
	Log.i("AppLog", command);
	url = new URL(createURL() + command);
	con = (HttpURLConnection) url.openConnection();
	con.setConnectTimeout(timeout);
	con.setDoOutput(true);
	con.setRequestProperty("Authorization",
		base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
	con.connect();
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
	    return (con.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) ? 1
		    : 0;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return 0;
    }

    /*
     * Active / désactive l'autofocus ou l'autoiris
     */
    public int switchAutoFunc(int function, String value) {
	if (function != CameraControl.AUTOFOCUS
		&& function != CameraControl.AUTOIRIS)
	    return 0;
	if (!isSupported(function))
	    return -1;
	if (!value.equals("on") && !value.equals("off"))
	    return 0;

	String param = (function == CameraControl.AUTOFOCUS) ? "autofocus"
		: "autoiris";
	try {
	    HttpURLConnection con = sendCommand("axis-cgi/com/ptz.cgi?" + param
		    + "=" + value + "&camera=" + String.valueOf(cam.channel));
	    if (con.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
		if (value.equals("on"))
		    this.enableFunction(function);
		else
		    this.disableFunction(function);
		return 1;
	    } else {
		return 0;
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return 0;
    }

    /**
     * Get SnapShop from Axis Camera
     * 
     * @param resolution
     *            You can choose the snapshot resolution from : "1280x1024",
     *            "1280x960", "1280x720", "768x576", "4CIF", "704x576",
     *            "704x480", "VGA", "640x480", "640x360", "2CIFEXP", "2CIF",
     *            "704x288", "704x240", "480x360", "CIF", "384x288", "352x288",
     *            "352x240", "320x240", "240x180", "QCIF", "192x144", "176x144",
     *            "176x120", "160x120"
     * @return The Bitmap created by the camera
     * @throws IOException
     *             If camera can't take snapshot or if the camera is unreachable
     */
    public Bitmap takeSnapshot(String resolution) throws IOException {
	URLConnection con = sendCommand("axis-cgi/jpg/image.cgi"
		+ "?resolution=" + resolution);
	Log.i("AppLog", "connected");
	InputStream stream = con.getInputStream();
	Bitmap bmp = BitmapFactory.decodeStream(stream);
	stream.close();
	return bmp;
    }

}

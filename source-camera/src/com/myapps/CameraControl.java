package com.myapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.myapps.utils.CouldNotCreateGroupException;
import com.myapps.utils.base64Encoder;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 *
 * Implements remote camera control like PTZ
 * (Pan/Tilt/Zoom), Snapshot, iris, focus, etc...
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
    public static final int MOTION_D = 10;
    public static final int AUDIO = 11;
    public static final int BRIGHTNESS = 12;

    private static final int NB_FUNC = 13;
    private static final int NB_BASIC_FUNC = 5;

    public static final int NOT_SUPPORTED = -1;
    public static final int DISABLED = 0;
    public static final int ENABLED = 1;

    public static final char ABSOLUTE = 1;
    public static final char RELATIVE = 2;
    public static final int DIGITAL = 4;
    public static final int AUTO = 8;
    public static final int CONTINUOUS = 16;

    public Camera cam;
    private int[] currentConfig = new int[NB_FUNC];
    private int[] functionProperties = new int[NB_BASIC_FUNC];
    private String[] resolutions, rotations, formats;
    private Activity activity;

    /**
     * Constructor
     * @param cam The camera to control
     * @param activity The current activity 
     */
    public CameraControl(Camera cam, Activity activity) {
		this.cam = cam;
		Application a;
		this.activity = activity;
		this.initConfig();
		this.loadConfig();
    }

    /**
     *  Initialize all the camera's functions to the NOT_SUPPORTED state
     */
    private void initConfig() {
	for (int i = 0; i < NB_FUNC; i++) {
	    this.currentConfig[i] = NOT_SUPPORTED;
	}
    }

	/** 
	 * Request camera's possibilities from server and mark off them
	 */
	private void loadConfig() {
		HttpURLConnection con;
		InputStream result;
		BufferedReader in;
		String line, property, value;

		try {
			con = sendCommand("axis-cgi/com/ptz.cgi?info=1&camera=" + String.valueOf(cam.channel));
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				result = con.getInputStream();
				in = new BufferedReader(new InputStreamReader(
						result));
				while ((line = in.readLine()) != null) {
					if (line.indexOf("=") > -1) {
						property = line.substring(0, line.indexOf("=")).trim();
						value = line.substring(line.indexOf("=") + 1);
						if (property.contains("pan")) {
							if (property.contentEquals("pan"))
								functionProperties[PAN] += ABSOLUTE;
							else if (property.contentEquals("rpan"))
								functionProperties[PAN] += RELATIVE;
							else if (property
									.contentEquals("continuouspantiltmove")) {
								functionProperties[PAN] += CONTINUOUS;
								functionProperties[TILT] += CONTINUOUS;
							}
						} else if (property.contains("tilt")) {
							if (property.contentEquals("tilt"))
								functionProperties[TILT] += ABSOLUTE;
							else if (property.contentEquals("rtilt"))
								functionProperties[TILT] += RELATIVE;
						} else if (property.contains("zoom")) {
							if (property.contentEquals("zoom"))
								functionProperties[ZOOM] += ABSOLUTE;
							else if (property.contentEquals("rzoom"))
								functionProperties[ZOOM] += RELATIVE;
							else if (property
									.contentEquals("continuouszoommove"))
								functionProperties[ZOOM] += CONTINUOUS;
							else if (property.contentEquals("digitalzoom"))
								functionProperties[ZOOM] += DIGITAL;
						} else if (property.contains("focus")) {
							if (property.contentEquals("focus"))
								functionProperties[FOCUS] += ABSOLUTE;
							else if (property.contentEquals("rfocus"))
								functionProperties[FOCUS] += RELATIVE;
							else if (property
									.contentEquals("continuousfocusmove"))
								functionProperties[FOCUS] += CONTINUOUS;
							else if (property.contentEquals("autofocus")) {
								functionProperties[FOCUS] += AUTO;
								currentConfig[AUTOFOCUS] = DISABLED;
							}
						} else if (property.contains("iris")) {
							if (property.contentEquals("iris"))
								functionProperties[IRIS] += ABSOLUTE;
							else if (property.contentEquals("riris"))
								functionProperties[IRIS] += RELATIVE;
							else if (property
									.contentEquals("continuousirismove"))
								functionProperties[IRIS] += CONTINUOUS;
							else if (property.contentEquals("autoiris")) {
								functionProperties[IRIS] += AUTO;
								currentConfig[AUTOIRIS] = DISABLED;
							}
						} else if (property.contentEquals("ircutfilter")) {
							currentConfig[IR_FILTER] = DISABLED;
							if (value.contains("auto"))
								currentConfig[AUTO_IR] = DISABLED;
						} else if (property.contentEquals("backlight")) {
							currentConfig[BACKLIGHT] = DISABLED;
						}
					}
				}
				con.disconnect();
			}
			for (int i = 0; i < NB_BASIC_FUNC; i++)
				if (functionProperties[i] > 0)
					currentConfig[i] = ENABLED;
			
			con = sendCommand("axis-cgi/admin/param.cgi?action=list&group=" +
					"Properties.Motion.Motion,Properties.Audio.Audio," +
					"Properties.Image");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				result = con.getInputStream();
				in = new BufferedReader(new InputStreamReader(
						result));
				while ((line = in.readLine()) != null) {
					if (line.contains("Properties.Motion.Motion=yes"))
						currentConfig[MOTION_D] = ENABLED;
					else if (line.contains("Properties.Audio.Audio=yes"))
						currentConfig[AUDIO] = ENABLED;
					else if (line.contains("Properties.Image.Rotation")) {
						value = line.substring(line.indexOf("=") + 1);
						rotations = value.split(",");
					} else if (line.contains("Properties.Image.Resolution")) {
						value = line.substring(line.indexOf("=") + 1);
						resolutions = value.split(",");
						Log.i(activity.getString(R.string.logTag), value);
					} else if (line.contains("Properties.Image.Format")) {
						value = line.substring(line.indexOf("=") + 1);
						formats = value.split(",");
					}
				}
				for (int i = 0; i < NB_FUNC; i++)
					Log.i(activity.getString(R.string.logTag), "func" + i + ": "
							+ currentConfig[i]);
			}
			con.disconnect();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

    /**
     * Get the different resolutions used for snapshot
     * @return The array of resolutions
     */
    public String[] getResolutions() {
	return resolutions;
    }

    /**
     * Get the different rotation angles of image
     * @return The array of rotation angles
     */
    public String[] getRotations() {
	return rotations;
    }

    /**
     * Get the different streaming file formats
     * @return The array of file formats
     */
    public String[] getFormats() {
	return formats;
    }

    /**
     * Check if a functionality is enabled
     * @param function The functionality
     * @return true if the functionality is enabled, false otherwise
     */
    public boolean isEnabled(int function) {
	if (function < 0 || function >= NB_FUNC)
	    return false;
	return currentConfig[function] == ENABLED;
    }

    /**
     * Check if a functionality is supported by the camera
     * @param function The functionality
     * @return true if the functionality is supported, false otherwise
     */
    public boolean isSupported(int function) {
	if (function < 0 || function >= NB_FUNC)
	    return false;
	return currentConfig[function] != NOT_SUPPORTED;
    }

    /**
     * Enable a functionality on the camera
     * @param function The functionality
     * @return true if the functionality has been enabled, false otherwise
     */
    public boolean enableFunction(int function) {
	if (function < 0 || function >= NB_BASIC_FUNC || !isSupported(function))
	    return false;
	this.currentConfig[function] = ENABLED;
	return true;
    }

    /**
     * Disable a functionality on the camera
     * @param function The functionality
     * @return true if the functionality has been disabled, false otherwise
     */
    public boolean disableFunction(int function) {
	if (function < 0 || function >= NB_BASIC_FUNC || !isSupported(function))
		return false;
	this.currentConfig[function] = DISABLED;
	return true;
    }

    /**
     * Get the address of the camera
     * @return The address of the camera
     */
    public String createURL() {
	return cam.getURI();
    }

    /**
     * Open a HttpURLConnection to (camera.getUri+command) with authorization
     * Example : camera.getUri = http://192.168.1.2/
     * 			 command = axis-cgi/com/ptz.cgi?info=1&camera=1
     * @param command The URL part used for the connection
     * @return The HttpURLConnection object
     * @throws IOException
     */
    public HttpURLConnection sendCommand(String command) throws IOException {
	URL url = null;
	HttpURLConnection con = null;
	Log.i(activity.getString(R.string.logTag), command);
	url = new URL(createURL() + command);
	con = (HttpURLConnection) url.openConnection();
	int timeout = Integer.parseInt(Home.preferences.getString(
		activity.getString(R.string.TimeOut),
		activity.getString(R.string.defaultTimeOut)));
	con.setConnectTimeout(timeout);
	con.setDoOutput(true);
	con.setRequestProperty("Authorization",
		base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
	con.connect();
	return con;
    }

    /**
     * Change the value of PTZ/Focus/Iris/Brightness used by the camera (perform an action)
     * @param function The functionality to perform
     * @param value1 The value to apply
     * @param value2 The second value used only in case of Pan/Tilt
     * @return true if request succeeded, false otherwise
     */
    public boolean changeValFunc(int function, float value1, float value2) {
	// if (function < 0 || function >= NB_BASIC_FUNC)
	// return 0;
	// if (!isSupported(function))
	// return -1;

	String query = "";
	switch (function) {
	case PAN:
	case TILT:
	    query = "rpan=" + value1 + "&rtilt=" + value2;
	    break;
	case ZOOM:
	    query = "rzoom=" + value1;
	    break;
	case FOCUS:
	    query = "rfocus=" + value1;
	    break;
	case IRIS:
	    query = "riris=" + value1;
	    break;
	case BRIGHTNESS:
	    query = "rbrightness=" + value1;
	    break;
	}
	try {
	    HttpURLConnection con = sendCommand("axis-cgi/com/ptz.cgi?" + query
		    + "&camera=" + String.valueOf(cam.channel));
	    Log.i(activity.getString(R.string.logTag),
		    ("axis-cgi/com/ptz.cgi?" + query + "&camera=" + String
			    .valueOf(cam.channel)));
	    boolean res = con.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT;
	    con.disconnect();
	    return res;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;
    }

    /**
     * Switch on/off the "auto" mode of certain functionalities on the camera
     * @param function The functionality to perform
     * @param The value to apply among {on, off, auto}
     * @return true if request succeeded, false otherwise
     */
    public boolean switchAutoFunc(int function, String value) {
	if (function != AUTOFOCUS && function != AUTOIRIS
		&& function != AUTO_IR && function != BACKLIGHT)
	    return false;
	/*
	 * if (!isSupported(function)) return -1;
	 */
	if (!value.equals("on") && !value.equals("off")
		&& !value.equals("auto"))
	    return false;
	String param = "";
	switch (function) {
	case AUTOFOCUS:
	    param = "autofocus";
	    break;
	case AUTOIRIS:
	    param = "autoiris";
	    break;
	case AUTO_IR:
	    param = "ircutfilter";
	    break;
	case BACKLIGHT:
	    param = "backlight";
	    break;
	}
	try {
	    HttpURLConnection con = sendCommand("axis-cgi/com/ptz.cgi?" + param
		    + "=" + value + "&camera=" + String.valueOf(cam.channel));
	    if (con.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
		if (value.equals("on"))
		    this.enableFunction(function);
		else
		    this.disableFunction(function);
		con.disconnect();
		return true;
	    } else {
		con.disconnect();
		return false;
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;
    }

    /**
     * Get SnapShot from the camera
     * @param resolution
     *            You can choose the snapshot resolution from : "1280x1024",
     *            "1280x960", "1280x720", "768x576", "4CIF", "704x576",
     *            "704x480", "VGA", "640x480", "640x360", "2CIFEXP", "2CIF",
     *            "704x288", "704x240", "480x360", "CIF", "384x288", "352x288",
     *            "352x240", "320x240", "240x180", "QCIF", "192x144", "176x144",
     *            "176x120", "160x120" (dependent on the camera)
     * @return The Bitmap created by the camera
     * @throws IOException
     *             If camera can't take snapshot or if the camera is unreachable
     */
    public Bitmap takeSnapshot(String resolution) throws IOException {
	HttpURLConnection con = sendCommand("axis-cgi/jpg/image.cgi"
		+ "?resolution=" + resolution + "&camera=" + String.valueOf(cam.channel));
	Log.i(activity.getString(R.string.logTag), "Snapshot");
	InputStream stream = con.getInputStream();
	Bitmap bmp = BitmapFactory.decodeStream(stream);
	stream.close();
	con.disconnect();
	return bmp;
    }

    /**
     * Add a new Motion Detection window on the camera
     * @return The groupID given to the window by the camera
     * @throws IOException
     * @throws CouldNotCreateGroupException The creation of a MD window is impossible
     */
    public int addMotionD() throws IOException, CouldNotCreateGroupException {
	HttpURLConnection con = sendCommand("axis-cgi/operator/param.cgi?action=add&group=Motion&template=motion");
	if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
	    BufferedReader in = new BufferedReader(new InputStreamReader(
		    con.getInputStream()));
	    String line;
	    while ((line = in.readLine()) != null) {
		if (line.contains("HTTP/1.0 200 OK\r\n"))
		    continue;
		if (line.contains("# Request failed: Couldn't create group"))
		    throw new CouldNotCreateGroupException();
		if (line.contains("M")) {
		    cam.groupID = Integer.parseInt(line.substring(1, 2));
		    Log.i(activity.getString(R.string.logTag), "MotionDetection groupe = " + cam.groupID);
		    return cam.groupID;
		}
	    }
	}
	throw new CouldNotCreateGroupException();
    }

    /**
     * Remove an existent Motion Detection window on the camera
     * @return -1 if the removal succeeded, the current groupID otherwise 
     * @throws IOException
     */
    public int removeMotionD() throws IOException {
	HttpURLConnection con = sendCommand("axis-cgi/operator/param.cgi?action=remove&group=Motion.M"
		+ cam.groupID);
	Log.i(activity.getString(R.string.logTag), con.getResponseCode()
		+ "MotionDetection free groupe = " + cam.groupID);
	if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
	    cam.groupID = -1;
	    return -1;
	}
	return cam.groupID;
    }

    /**
     * Update parameters of an existent Motion Detection window on the camera
     * @param param
     * @param value
     * @return
     * @throws IOException
     */
    public boolean updateMotionDParam(String param, String value)
	    throws IOException {
	HttpURLConnection con = sendCommand("axis-cgi/operator/param.cgi?action=update&Motion.M"
		+ cam.groupID + "." + param + "=" + value);
	if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
	    return true;
	}
	return false;
    }

    /* NEVER USE FOR THE MOMENT */
    /*public void getMotionDValues(int sensitivity, int limit, long delay)
	    throws IOException {
	if (!isEnabled(MOTION_D)) {
	    HttpURLConnection con = sendCommand("axis-cgi/"
		    + "operator/param.cgi?action=add&group=Motion&template="
		    + "motion&Motion.M.Sensitivity=" + sensitivity
		    + "Motion.M.ObjectSize=" + limit);
	    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
		try {
		    InputStream result = con.getInputStream();
		    BufferedReader in = new BufferedReader(
			    new InputStreamReader(result));
		    String line = in.readLine();
		    int end = line.indexOf("OK") - 1;
		    String id = line.substring(1, end);
		} catch (IOException e) {
		    Log.i(activity.getString(R.string.logTag), "MotionDetection IOException");
		    e.printStackTrace();
		}
		enableFunction(MOTION_D);
		Log.i(activity.getString(R.string.logTag), "Detection activee");
	    }
	}
    }*/
}

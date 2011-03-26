package com.myapps.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import com.myapps.Camera;
import com.myapps.CameraControl;
import com.myapps.MultiVideo;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

/**
 * Simulate MJPEG video by downloading successive JPEG photos
 */
public class PlayerThread implements Runnable {

    private String TAG = "AppLog";

    private HttpURLConnection con;
    private int delay, index;
    private CameraControl camC;

    /**
     * Constructor
     * @param cam The camera to use
     * @param index The index of video frame on multi-view
     * @param delay The delay in milliseconds to limit FPS
     * @throws IOException
     */
    public PlayerThread(Camera cam, Activity activity, int index, int delay)
	    throws IOException {
	this.delay = delay;
	this.index = index;
	this.camC = new CameraControl(cam, activity);
    }

    /**
     * Thread code to run
     */
    public void run() {
	Log.i(TAG, "PlayerThread starts");
	Bitmap bmp = null;
	String command;
	command = "axis-cgi/jpg/image.cgi?resolution=160x120";
	while (!Thread.currentThread().isInterrupted()) {
	    Message m = new Message();
	    m.what = MultiVideo.GUIUPDATEIDENTIFIER;
	    m.arg1 = index;
	    try {

		con = camC.sendCommand(command);
		Log.i(TAG, ("Cam " + index + " connected"));
		/* Get image result */
		InputStream stream = con.getInputStream();
		bmp = BitmapFactory.decodeStream(stream);
		stream.close();
		con.disconnect();
		/* Set the new image to display */
		MultiVideo.newBMP[index] = bmp;
		/* Send message to UI to refresh View */
		MultiVideo.myViewUpdateHandler.sendMessage(m);
		Log.i(TAG, "Message sent from : " + index);
		try {
		    /*
		     * Sleep to give hand to UI thread and limit FPS and gain
		     * bandwidth
		     */
		    Thread.sleep(delay);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	    } catch (MalformedURLException e) {
		Log.i(TAG, "MalformedURLException");
		e.printStackTrace();
		break;
	    } catch (IOException e) {
		Log.i(TAG, "PlayerThread IOException");
		m.what = MultiVideo.URLERRORIDENTIFIER;
		MultiVideo.myViewUpdateHandler.sendMessage(m);
		e.printStackTrace();
		break;
	    }
	}
    }
}
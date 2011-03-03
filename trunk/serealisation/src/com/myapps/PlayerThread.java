package com.myapps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * PlayerThread simulate mjpeg video by downloading jpeg
 */
public class PlayerThread implements Runnable {

    private String logTag = "AppLog";

    private URLConnection con;
    private Camera cam;
    private int delay, index;
    private Activity activity;
    private CameraControl camC;

    /**
     * Create a PlayerThread
     * 
     * @param cam
     *            camera to print
     * @param index
     *            index of video frame (0,1,2,3)
     * @param delay
     *            delay in milliseconds to limit fps
     * @throws IOException
     */
    public PlayerThread(Camera cam, Activity activity, int index, int delay)
	    throws IOException {
	this.cam = cam;
	this.delay = delay;
	this.index = index;
	this.camC = new CameraControl(cam, activity);
    }

    /**
     * Thread code to run
     */
    public void run() {
	Log.i(logTag, "go");
	Bitmap bmp = null;
	String command;
	while (!Thread.currentThread().isInterrupted()) {
	    Message m = new Message();
	    m.what = MultiVideo.GUIUPDATEIDENTIFIER;
	    m.arg1 = index;
	    try {
		/* Open HTTP connection */
		command = "axis-cgi/jpg/image.cgi?resolution=160x120";
		con = camC.sendCommand(command);
		Log.i(logTag, ("" + index + " connected"));
		/* Get image result */
		InputStream stream = con.getInputStream();
		bmp = BitmapFactory.decodeStream(stream);
		stream.close();
		/* Set the new image to print */
		MultiVideo.newBMP[index] = bmp;
		/* Send message to UI to refresh View */
		MultiVideo.myViewUpdateHandler.sendMessage(m);
		Log.i(logTag, "message send from : " + index);
		try {
		    /*
		     * Sleep to give hand to UI thread and limit fps to gain
		     * bandwidth
		     */
		    Thread.sleep(delay);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	    } catch (MalformedURLException e) {
		Log.i(logTag, "MalformedURLException");
		e.printStackTrace();
		break;
	    } catch (IOException e) {
		Log.i(logTag, "PlayerThread IOException");
		m.what = MultiVideo.URLERRORIDENTIFIER;
		MultiVideo.myViewUpdateHandler.sendMessage(m);
		e.printStackTrace();
		break;
	    }
	}
    }
}
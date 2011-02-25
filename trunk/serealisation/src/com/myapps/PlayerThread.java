package com.myapps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

/**
 * PlayerThread simulate mjpeg video by downloading jpeg
 */
public class PlayerThread implements Runnable {

    private String logTag = "AppLog";

    private URLConnection con;
    private Camera cam;
    long t0, t1;
    private int delay, index;

    /**
     * Create a PlayerThread
     * 
     * @param cam
     *            camera to print
     * @param index
     *            index of video frame (0,1,2,3)
     * @param delay
     *            delay in milliseconds to limit fps
     */
    public PlayerThread(Camera cam, int index, int delay) {
	this.cam = cam;
	this.delay = delay;
	this.index = index;
    }


    /**
     * Thread code to run
     */
    public void run() {
	Log.i(logTag, "go");
	while (!Thread.currentThread().isInterrupted()) {
	    Message m = new Message();
	    m.what = Video.GUIUPDATEIDENTIFIER;
	    m.arg1 = index;
	    Bitmap bmp = null;
	    URL url;
	    try {
		/* Open HTTP connection */
		url = new URL(cam.getURI()
			+ "axis-cgi/jpg/image.cgi?resolution=160x120");
		Log.i(logTag, url.toString());
		con = url.openConnection();
		con.setRequestProperty("Authorization", base64Encoder
			.userNamePasswordBase64(cam.login, cam.pass));
		con.connect();
		Log.i(logTag, "connected");
		/* Get image result */
		t0 = System.currentTimeMillis();
		InputStream stream = con.getInputStream();
		t1 = System.currentTimeMillis();
		Log.i(logTag, "img dl temps : " + (t1 - t0));
		bmp = BitmapFactory.decodeStream(stream);
		stream.close();
		/* Set the new image to print */ 
		MultiVideo.newBMP[index] = bmp;
		/* Send message to UI to refresh View */
		MultiVideo.myViewUpdateHandler.sendMessage(m);
		Log.i(logTag, "message send");
		try {
		    /* Sleep to give hand to UI thread and limit fps to gain bandwidth */
		    Thread.sleep(delay);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	    } catch (MalformedURLException e) {
		Log.i(logTag, "MalformedURLException");
		e.printStackTrace();
	    } catch (IOException e) {
		Log.i(logTag, "IOException");
		e.printStackTrace();
	    }
	}
    }
}
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
    public PlayerThread(Camera cam, int index, int delay) throws IOException {
	this.cam = cam;
	this.delay = delay;
	this.index = index;
	this.camC = new CameraControl(cam);
    }


    /**
     * Thread code to run
     */
    public void run() {
	Log.i(logTag, "go");
	    Bitmap bmp = null;
	    String command;
	    Message m = new Message();
	while (!Thread.currentThread().isInterrupted()) {
	    m.what = Video.GUIUPDATEIDENTIFIER;
	    m.arg1 = index;
	    try {
		/* Open HTTP connection */
		command = "axis-cgi/jpg/image.cgi?resolution=160x120";
		con = camC.sendCommand(command);
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
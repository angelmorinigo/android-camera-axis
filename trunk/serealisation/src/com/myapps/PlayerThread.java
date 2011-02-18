package com.myapps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

public class PlayerThread extends Thread {

	private String logTag = "AppLog";

	private Bitmap bmp;

	public PlayerThread() {
	}

	synchronized public Bitmap getBMP() {
		return bmp;
	}

	synchronized protected Bitmap CaptureVideo() {
		Bitmap bmp = null;

		try {
			Log.i(logTag, "connected");
			URL url = new URL("http://192.168.1.20/axis-cgi/jpg/image.cgi");
			InputStream stream = url.openStream();
			bmp = BitmapFactory.decodeStream(stream);
			stream.close();
		} catch (MalformedURLException e) {
			Log.i(logTag, "MalformedURLException");
		} catch (IOException e) {
			Log.i(logTag, "IOException");
		}
		Log.i(logTag, "setBMP");
		return bmp;
	}

	public void run() {
		Log.i(logTag, "go");
		for (int i = 0; i < 10; i++) {
			bmp = CaptureVideo();
			try {
				Log.i(logTag, "yield");
				yield();
				sleep(100);
			} catch (InterruptedException e) {
				Log.i(logTag, "InterruptedException");
				e.printStackTrace();
			}
		}
	}
}
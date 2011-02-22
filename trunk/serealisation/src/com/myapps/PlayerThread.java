package com.myapps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;

public class PlayerThread implements Runnable {

	private String logTag = "AppLog";

	private Bitmap bmp;
	private ImageView img;
	private URLConnection con;
	private Camera cam;
	long t0, t1;
	private int delay, index;
	
	public PlayerThread(Camera cam, int index ,int delay) {
		this.cam = cam;
		this.delay = delay;
		this.index = index;
	}

	@Override
	public void run() {
		Log.i(logTag, "go");
		while (!Thread.currentThread().isInterrupted()) {
			Message m = new Message();
			m.what = Video.GUIUPDATEIDENTIFIER;
			m.arg1 = index;
			Bitmap bmp = null;
			URL url;
			try {
				url = new URL("http://" + cam.ip + ":" + cam.port + "/axis-cgi/jpg/image.cgi?resolution=160x120" );
				Log.i(logTag, url.toString());				
				con = url.openConnection();
				con.setRequestProperty("Authorization",
						base64Encoder.userNamePasswordBase64(cam.login,cam.pass));
				con.connect();
				Log.i(logTag, "connected");

				t0 = System.currentTimeMillis();
				InputStream stream = con.getInputStream();
				t1 = System.currentTimeMillis();
				Log.i(logTag, "img dl temps : " + (t1-t0));
				bmp = BitmapFactory.decodeStream(stream);
				stream.close();
				// TROUVER UN MOYEN POUR RECUPERER LES IMAGES /HANDLER PLUS FACILEMENT
				MultiVideo.newBMP[index] = bmp;
				MultiVideo.myViewUpdateHandler.sendMessage(m);
				Log.i(logTag, "message send");
				try {
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
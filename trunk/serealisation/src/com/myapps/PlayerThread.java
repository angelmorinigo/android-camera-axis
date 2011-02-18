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
import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

public class PlayerThread implements Runnable{

	private String logTag = "AppLog";

	private Bitmap bmp;
	private ImageView img;
private URLConnection con;


	public PlayerThread() {
	}

	@Override
	public void run() {
		Log.i(logTag, "go");
		while (!Thread.currentThread().isInterrupted()) {
			Message m = new Message();
			m.what = Video.GUIUPDATEIDENTIFIER;
			Bitmap bmp = null;
			URL url;
			try {
				url = new URL(
						"http://192.168.1.20/axis-cgi/jpg/image.cgi");

				con = url.openConnection();
				con.setRequestProperty("Authorization",
						base64Encoder
								.userNamePasswordBase64(
										"root", "root"));
				con.connect();
				Log.i(logTag, "connected");
				InputStream stream = con.getInputStream();
				bmp = BitmapFactory.decodeStream(stream);
				stream.close();
				Log.i(logTag, "img dl");
				Video.newBMP = bmp;
				Log.i(logTag, "image set");
				Video.myViewUpdateHandler.sendMessage(m);
				Log.i(logTag, "message send");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
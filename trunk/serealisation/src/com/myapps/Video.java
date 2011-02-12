package com.myapps;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Video extends Activity implements SurfaceHolder.Callback {
	private MediaPlayer mediaPlayer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		mediaPlayer=new MediaPlayer();

		SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView1);
		SurfaceHolder holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(400, 300);
		Log.i("AppLog", "Surface View Initialisé");
		
		Bundle extras = getIntent().getExtras();
		String uri = extras.getString("uri");
		Log.i("AppLog", "Demande lecture "+uri);
		try {
			Log.i("AppLog", "demarrage video");
			mediaPlayer.setDisplay(holder);
			mediaPlayer.setDataSource(uri);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {			e.printStackTrace();
		} catch (IllegalStateException e) {			e.printStackTrace();
		} catch (IOException e) {
			Log.i("AppLog", "No video found");
			e.printStackTrace();

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.start();
		Log.i("AppLog", "video en cours de lecture");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mediaPlayer.release();
	}
}

package com.myapps;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Video extends Activity implements SurfaceHolder.Callback {
	private MediaPlayer mediaPlayer;
	private String uri;
	private String logTag = "AppLog";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		setRequestedOrientation(0);
		mediaPlayer = new MediaPlayer();
		
		
		SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView1);
		SurfaceHolder holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(400,300);
		Log.i(logTag, "Surface View Initialise");

		Bundle extras = getIntent().getExtras();
		uri = extras.getString("uri");
		Log.i(logTag, "Demande lecture " + uri);
		mediaPlayer.setDisplay(holder);

		
		Button buttonPlay = (Button) findViewById(R.id.Play);
		buttonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					Log.i(logTag, "video en pause");
				} else {
					mediaPlayer.start();
					Log.i(logTag, "video en cours de lecture");
				}
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			Log.i(logTag, "demarrage video");
			mediaPlayer.setDataSource(uri);
			mediaPlayer.setScreenOnWhilePlaying(true);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "No video found", Toast.LENGTH_LONG).show();
			Log.i(logTag, "No video found");
			e.printStackTrace();
			this.finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mediaPlayer.release();
	}
}

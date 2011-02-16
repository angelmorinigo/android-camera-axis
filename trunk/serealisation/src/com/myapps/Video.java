package com.myapps;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Video extends Activity implements SurfaceHolder.Callback {
	private MediaPlayer mediaPlayer;
	private String uri;
	private String camTag = "camera";
	private Camera cam;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		setRequestedOrientation(0);
		mediaPlayer = new MediaPlayer();

		/* Initialisation de la surface video */
		SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView1);
		SurfaceHolder holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// holder.setFixedSize(400, 300);
		mediaPlayer.setDisplay(holder);
		Log.i(getString(R.string.logTag), "Surface View Initialise");

		/* Récupération des arguments */
		Bundle extras = getIntent().getExtras();
		cam = (Camera) extras.getSerializable(camTag);

		uri = cam.getUrl();
		Log.i(getString(R.string.logTag), "Demande lecture " + uri);

		/* Button Listener */
		/* test */
		//uri = "/sdcard/Smart_Life.MP4";
		Button buttonPlay = (Button) findViewById(R.id.Play);
		buttonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					Log.i(getString(R.string.logTag), "video en pause");
				} else {
					mediaPlayer.start();
					Log.i(getString(R.string.logTag), "video en cours de lecture");
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
			Log.i(getString(R.string.logTag), "demarrage video");
			mediaPlayer.setDataSource(uri);
			mediaPlayer.setScreenOnWhilePlaying(true);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "No video found", Toast.LENGTH_LONG).show();
			Log.i(getString(R.string.logTag), "No video found");
			e.printStackTrace();
			this.finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mediaPlayer.release();
	}
}

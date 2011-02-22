package com.myapps;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import de.mjpegsample.MjpegView.MjpegInputStream;
import de.mjpegsample.MjpegView.MjpegView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Video extends Activity {
	private String url;
	private Camera cam;
	private CameraControl camC;
	public Activity activity;
	private static ImageView img;
	private MjpegView mv;
	private boolean pause;
	protected static final int GUIUPDATEIDENTIFIER = 0x101;
	static final String[] SIZE = new String[] { "1280x1024", "1280x960",
			"1280x720", "768x576", "4CIF", "704x576", "704x480", "VGA",
			"640x480", "640x360", "2CIFEXP", "2CIF", "704x288", "704x240",
			"480x360", "CIF", "384x288", "352x288", "352x240", "320x240",
			"240x180", "QCIF", "192x144", "176x144", "176x120", "160x120" };
	static Bitmap newBMP;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		setRequestedOrientation(0);

		/*
		 * Récupération des arguments
		 */

		Bundle extras = getIntent().getExtras();
		cam = (Camera) extras.getSerializable(getString(R.string.camTag));
		camC = new CameraControl(cam);

		/*
		 * Buttons Listener
		 */

		Button buttonSnap = (Button) findViewById(R.id.Snap);
		buttonSnap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("SnapShot Format");
				builder.setSingleChoiceItems(SIZE, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								try {
									Bitmap bmp = camC.takeSnapshot(SIZE[item]);
									FileOutputStream fichier = new FileOutputStream(
											"/sdcard/test.jpeg");
									img.setImageBitmap(bmp);
									bmp.compress(Bitmap.CompressFormat.JPEG,
											80, fichier);
									fichier.flush();
									fichier.close();
									Log.i(getString(R.string.logTag),
											"Snap Save !!");
								} catch (IOException e) {
									e.printStackTrace();
								}
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});


		/*
		 * Contrôle du PTZ par déplacement sur l'écran
		 */
		/*
		 * img.setOnTouchListener(new OnTouchListener() { float startX, startY;
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { if
		 * (event.getAction() == MotionEvent.ACTION_DOWN) { startX =
		 * event.getX(); startY = event.getY(); return true; } return false; }
		 * });
		 */

		/*
		 * Affichage video
		 */
		url = "http://" + cam.ip + ":" + cam.port + "/axis-cgi/mjpg/video.cgi?resolution=320x240";
		mv = (MjpegView) findViewById(R.id.surfaceView1);
		start_connection(mv, url, cam);

	}

	private void start_connection(MjpegView mv, String url, Camera cam) {
		try {
			URL addr = new URL(url);
			Log.i("AppLog", addr.toString());
			HttpURLConnection con = (HttpURLConnection) addr.openConnection();
			con.setRequestProperty("Authorization",
					base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
			con.connect();
			InputStream stream;
			stream = con.getInputStream();
			mv.setSource(new MjpegInputStream(stream));
			mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
			mv.showFps(true);
			pause = false;
			Log.i("AppLog", "onCreate");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onResume() {
		super.onResume();
		if (pause){
			mv.resumePlayback();
			pause = false;
		}
		
	}

	public void onPause() {
		pause = true;
		super.onPause();
	}

	public void onDestroy() {
		super.onDestroy();
		mv.stopPlayback();
	}
}

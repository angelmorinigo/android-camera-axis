package com.myapps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import de.mjpegsample.MjpegView.MjpegInputStream;
import de.mjpegsample.MjpegView.MjpegView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 
 * Implements the main Video Viewer Interface
 * 
 */
public class Video extends Activity {
    private String url;
    private Camera cam;
    private CameraControl camC;
    private Activity activity;
    private MjpegView mv;
    private boolean pause;
    protected static final int GUIUPDATEIDENTIFIER = 0x101;
    static final String[] SIZE = new String[] { "1280x1024", "1280x960",
	    "1280x720", "768x576", "4CIF", "704x576", "704x480", "VGA",
	    "640x480", "640x360", "2CIFEXP", "2CIF", "704x288", "704x240",
	    "480x360", "CIF", "384x288", "352x288", "352x240", "320x240",
	    "240x180", "QCIF", "192x144", "176x144", "176x120", "160x120" };
    protected static Bitmap newBMP;

    private String fileNameURL = "/sdcard/com.myapps.camera/";
    private NotificationManager notificationManager;

    /**
     * Send command to camera and execute it
     * 
     * @param direction
     *            Direction to move
     */
    public void movePanTilt(final String direction) {
	String command;
	/* Define command */
	if (direction == "horizontalstart")
	    command = "axis-cgi/com/ptz.cgi?camera=1" + "&pan=-180" + "&tilt=0";
	else if (direction == "horizontalend")
	    command = "axis-cgi/com/ptz.cgi?camera=1" + "&pan=180" + "&tilt=0";
	else if (direction == "verticalstart")
	    command = "axis-cgi/com/ptz.cgi?camera=1" + "&pan=0" + "&tilt=180";
	else if (direction == "verticalend")
	    command = "axis-cgi/com/ptz.cgi?camera=1" + "&pan=0" + "&tilt=-180";

	else {
	    command = "axis-cgi/com/ptz.cgi?camera=1";
	    command = command + "&move=";
	    command = command + direction;
	}
	/* Send command */
	try {
	    HttpURLConnection con = camC.sendCommand(command);
	    Log.i(getString(R.string.logTag), ("" + con.getResponseCode()));
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Called when Activity start or resume
     */
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.video);
	setRequestedOrientation(0);
	activity = this;

	/* Recover arguments */

	Bundle extras = getIntent().getExtras();
	cam = (Camera) extras.getSerializable(getString(R.string.camTag));
	camC = new CameraControl(cam);

	/* Check network info */
	ConnectivityManager mConnectivity = (ConnectivityManager) activity
		.getApplicationContext().getSystemService(
			Context.CONNECTIVITY_SERVICE);
	NetworkInfo info = mConnectivity.getActiveNetworkInfo();
	int netType = info.getType();
	int netSubtype = info.getSubtype();
	if (netType == ConnectivityManager.TYPE_WIFI) {
	    Log.i("AppLog", "Wifi detecte");
	    url = "axis-cgi/mjpg/video.cgi?resolution=320x240";
	} else {
	    Log.i("AppLog", "Reseau detecte");
	    url = "axis-cgi/mjpg/video.cgi?resolution=160x120";
	}

	/* Buttons Listener */
	Button buttonSnap = (Button) findViewById(R.id.Snap);
	buttonSnap.setOnClickListener(new OnClickListener() {
	    @Override
	    /**
	     * Show resolution dialog, get Snapshot and record it
	     */
	    public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("SnapShot Format");
		builder.setSingleChoiceItems(SIZE, -1,
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
				try {

				    File f = new File(fileNameURL);
				    if (!f.exists()) {
					f.mkdir();
				    }
				    String fileName = fileNameURL
					    + System.currentTimeMillis()
					    + ".jpeg";
				    Log.i(getString(R.string.logTag), fileName);
				    Bitmap bmp = camC.takeSnapshot(SIZE[item]);
				    Log.i(getString(R.string.logTag),
					    "Snap ok !!");
				    FileOutputStream fichier = new FileOutputStream(
					    fileName);
				    bmp.compress(Bitmap.CompressFormat.JPEG,
					    80, fichier);
				    fichier.flush();
				    fichier.close();
				    statusBarNotification(activity, bmp,
					    ("Snap save : " + fileName),
					    fileName);
				} catch (IOException e) {
				    Log.i(getString(R.string.logTag),
					    "Snap I/O exception !!");
				    e.printStackTrace();
				}
				dialog.dismiss();
			    }
			});
		AlertDialog alert = builder.create();
		alert.show();
	    }
	});

	/* Motion listners */
	Button buttonright = (Button) findViewById(R.id.arrow_right);
	buttonright.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		movePanTilt("right");
	    }
	});

	Button buttonleft = (Button) findViewById(R.id.arrow_left);
	buttonleft.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		movePanTilt("left");
	    }
	});

	Button buttonup = (Button) findViewById(R.id.arrow_up);
	buttonup.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		movePanTilt("up");
	    }
	});

	Button buttondown = (Button) findViewById(R.id.arrow_down);
	buttondown.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		movePanTilt("down");
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

	mv = (MjpegView) findViewById(R.id.surfaceView1);
	start_connection(mv, url);

    }

    /**
     * 
     * Create a StatusBar Notification for Snapshop
     * 
     * @param activity
     *            The current activity
     * @param bmp
     *            The Snapshot recorded
     * @param text
     *            A message (like url)
     * @param path
     *            The snapshot url to start gallery activity on touch
     *            notification
     */
    private void statusBarNotification(Activity activity, Bitmap bmp,
	    String text, String path) {
	notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	Notification notification = new Notification(R.drawable.camera,
		"Camera-Axis", System.currentTimeMillis());
	notification.contentView = new RemoteViews(activity.getPackageName(),
		R.layout.notification);
	/* Action lors d'un clic sur la notification */
	Intent intentNotification = new Intent();
	intentNotification.setAction(android.content.Intent.ACTION_VIEW);
	intentNotification.setDataAndType(Uri.fromFile(new File(path)),
		"image/png");
	PendingIntent pendingIntent = PendingIntent.getActivity(
		activity.getApplicationContext(), 0, intentNotification, 0);

	notification.defaults |= Notification.DEFAULT_VIBRATE;
	notification.contentIntent = pendingIntent;
	notification.contentView.setImageViewBitmap(R.id.Nimage, bmp);
	notification.contentView.setTextViewText(R.id.Ntext, text);
	notificationManager.notify(1, notification);
    }

    /**
     * Create and start the Mjpeg video
     * 
     * @param mv
     * @param url
     * @param cam
     */
    private void start_connection(MjpegView mv, String url) {
	try {
	    HttpURLConnection con = camC.sendCommand(url);
	    InputStream stream = con.getInputStream();
	    mv.setSource(new MjpegInputStream(stream));
	    mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
	    mv.showFps(true);
	    pause = false;

	} catch (IOException e) {
	    Log.i(getString(R.string.logTag), "StartConnect IOException");
	    Toast.makeText(activity.getApplicationContext(),
		    "Cam�ra introuvable", Toast.LENGTH_LONG).show();
	    e.printStackTrace();
	    finish();
	}
    }

    /**
     * Resume video when activity resume.
     */
    public void onResume() {
	super.onResume();
	if (pause) {
	    mv.resumePlayback();
	    pause = false;
	}

    }

    /**
     * Stop video when activity sleep
     */
    public void onPause() {
	pause = true;
	super.onPause();
    }

    /**
     * Stop Video before destroy
     */
    public void onDestroy() {
	super.onDestroy();
	mv.stopPlayback();
    }
}

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
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
    private boolean advanceCtrl = false;

    /* TODO : deplacer dans le value/stringd.xml */
    static final String[] SIZE = new String[] { "1280x1024", "1280x960",
	    "1280x720", "768x576", "4CIF", "704x576", "704x480", "VGA",
	    "640x480", "640x360", "2CIFEXP", "2CIF", "704x288", "704x240",
	    "480x360", "CIF", "384x288", "352x288", "352x240", "320x240",
	    "240x180", "QCIF", "192x144", "176x144", "176x120", "160x120" };
    protected static Bitmap newBMP;

    private String fileNameURL = "/sdcard/com.myapps.camera/";
    private NotificationManager notificationManager;

    /**
     * Called when Activity start or resume
     */
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	if (!advanceCtrl)
	    setContentView(R.layout.video);

	setRequestedOrientation(0);
	activity = this;

	/* Recover arguments */

	Bundle extras = getIntent().getExtras();
	cam = (Camera) extras.getSerializable(getString(R.string.camTag));
	camC = new CameraControl(cam, this);

	/* Check network info */
	ConnectivityManager mConnectivity = (ConnectivityManager) activity
		.getApplicationContext().getSystemService(
			Context.CONNECTIVITY_SERVICE);
	NetworkInfo info = mConnectivity.getActiveNetworkInfo();
	int netType = info.getType();
	// int netSubtype = info.getSubtype();
	if (netType == ConnectivityManager.TYPE_WIFI) {
	    Log.i("AppLog", "Wifi detecte");
	    url = "axis-cgi/mjpg/video.cgi?resolution=320x240";
	} else {
	    Log.i("AppLog", "Reseau detecte");
	    url = "axis-cgi/mjpg/video.cgi?resolution=160x120";
	}

	mv = (MjpegView) findViewById(R.id.surfaceView1);
	start_connection(mv, url);

	mv.setOnTouchListener(new TouchListener(camC));
    }

    private class MyOnClickListenerControl implements OnClickListener {
	float value0, value1;
	int function;

	public MyOnClickListenerControl(int function, float value0, float value1) {
	    this.function = function;
	    this.value0 = value0;
	    this.value1 = value1;
	}

	@Override
	public void onClick(View v) {
	    camC.changeValFunc(function, value0, value1);
	}

    }

    /**
     * Assign custom menu to activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_video, menu);
	return true;
    }

    /**
     * Implements Menu Items Listener
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_control:
	    if (!advanceCtrl) {
		advanceCtrl = true;
		setContentView(R.layout.adv_video);
		/* Buttons Listener */

		Button buttonSnap = (Button) findViewById(R.id.Snap);
		buttonSnap.setOnClickListener(new OnClickListener() {
		    @Override
		    /**
		     * Show resolution dialog, get Snapshot and record it
		     */
		    public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
				activity);
			builder.setTitle("SnapShot Format");
			/* TODO getResolutions() MARCHE PAS */
			// final String[] resolutions = camC.getResolutions();
			builder.setSingleChoiceItems(SIZE, -1,
				new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,
					    int item) {
					try {

					    File f = new File(fileNameURL);
					    if (!f.exists()) {
						f.mkdir();
					    }
					    String fileName = fileNameURL
						    + System.currentTimeMillis()
						    + ".jpeg";
					    Log.i(getString(R.string.logTag),
						    fileName);
					    Bitmap bmp = camC
						    .takeSnapshot(SIZE[item]);
					    Log.i(getString(R.string.logTag),
						    "Snap ok !!");
					    FileOutputStream fichier = new FileOutputStream(
						    fileName);
					    bmp.compress(
						    Bitmap.CompressFormat.JPEG,
						    80, fichier);
					    fichier.flush();
					    fichier.close();
					    statusBarNotification(
						    activity,
						    bmp,
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

		Button buttonIrisP = (Button) findViewById(R.id.IrisP);
		buttonIrisP.setOnClickListener(new MyOnClickListenerControl(
			CameraControl.IRIS, 250, 0));

		Button buttonIrisM = (Button) findViewById(R.id.IrisM);
		buttonIrisM.setOnClickListener(new MyOnClickListenerControl(
			CameraControl.IRIS, -250, 0));

		Button buttonFocusP = (Button) findViewById(R.id.FocusP);
		buttonFocusP.setOnClickListener(new MyOnClickListenerControl(
			CameraControl.FOCUS, 2500, 0));

		Button buttonFocusM = (Button) findViewById(R.id.FocusM);
		buttonFocusM.setOnClickListener(new MyOnClickListenerControl(
			CameraControl.FOCUS, -2500, 0));

		Button buttonBrightnessP = (Button) findViewById(R.id.BrightnessP);
		buttonBrightnessP
			.setOnClickListener(new MyOnClickListenerControl(
				CameraControl.BRIGHTNESS, 2500, 0));

		Button buttonBrightnessM = (Button) findViewById(R.id.BrightnessM);
		buttonBrightnessM
			.setOnClickListener(new MyOnClickListenerControl(
				CameraControl.BRIGHTNESS, -2500, 0));
		Button buttonIROn = (Button) findViewById(R.id.IROn);
		buttonIROn.setOnClickListener(new OnClickListener() {	    
		    @Override
		    public void onClick(View v) {
			    camC.switchAutoFunc(CameraControl.AUTO_IR, "on");
		    }
		});
		
		Button buttonIROff = (Button) findViewById(R.id.IROff);
		buttonIROff.setOnClickListener(new OnClickListener() {	    
		    @Override
		    public void onClick(View v) {
			    camC.switchAutoFunc(CameraControl.AUTO_IR, "off");
		    }
		});
		Button backlightOn = (Button) findViewById(R.id.BacklightOn); 
		backlightOn.setOnClickListener(new OnClickListener() {	    
		    @Override
		    public void onClick(View v) {
			    camC.switchAutoFunc(CameraControl.BACKLIGHT, "on");
		    }
		});
		Button backlightOff = (Button) findViewById(R.id.BacklightOff); 
		backlightOff.setOnClickListener(new OnClickListener() {	    
		    @Override
		    public void onClick(View v) {
			    camC.switchAutoFunc(CameraControl.BACKLIGHT, "off");
		    }
		});
	    } else {
		advanceCtrl = false;
		setContentView(R.layout.video);
	    }
	    mv = (MjpegView) findViewById(R.id.surfaceView1);
	    start_connection(mv, url);
	    mv.setOnTouchListener(new TouchListener(camC));
	    return true;
	case R.id.menu_auto_focus:
	    camC.switchAutoFunc(CameraControl.AUTOFOCUS, "on");
	    return true;
	case R.id.menu_auto_ir:
	    camC.switchAutoFunc(CameraControl.AUTO_IR, "auto");
	    return true;
	case R.id.menu_auto_iris:
	    camC.switchAutoFunc(CameraControl.AUTOIRIS, "on");
	    return true;
	}
	return false;
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
		    "Camera introuvable", Toast.LENGTH_LONG).show();
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

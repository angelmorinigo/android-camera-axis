package com.myapps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.myapps.utils.notificationLauncher;

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
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
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
    private boolean MDWindowSelector = false;
    private Thread t;
    private int id;

    /* TODO : deplacer dans le value/stringd.xml */
    static final String[] SIZE = new String[] { "1280x1024", "1280x960",
	    "1280x720", "768x576", "4CIF", "704x576", "704x480", "VGA",
	    "640x480", "640x360", "2CIFEXP", "2CIF", "704x288", "704x240",
	    "480x360", "CIF", "384x288", "352x288", "352x240", "320x240",
	    "240x180", "QCIF", "192x144", "176x144", "176x120", "160x120" };
    protected static Bitmap newBMP;

    private String fileNameURL = "/sdcard/com.myapps.camera/";
    private PowerManager.WakeLock wl;

    /**
     * Called when Activity start or resume
     */
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	if (!advanceCtrl)
	    setContentView(R.layout.video);

	setRequestedOrientation(0);
	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tags");
	activity = this;
	id = 0;

	/* Recover arguments */

	Bundle extras = getIntent().getExtras();
	cam = (Camera) extras.getSerializable(getString(R.string.camTag));
	camC = new CameraControl(cam, this);

	TextView tv = (TextView) findViewById(R.id.idTV);
	tv.setText("ID : " + cam.uniqueID + "-" + cam.id);
	/* Check network info */
	ConnectivityManager mConnectivity = (ConnectivityManager) activity
		.getApplicationContext().getSystemService(
			Context.CONNECTIVITY_SERVICE);
	NetworkInfo info = mConnectivity.getActiveNetworkInfo();
	int netType = info.getType();
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
					    notificationLauncher
						    .statusBarNotificationImage(
							    activity,
							    bmp,
							    ("Snap save : " + fileName),
							    fileName, id,
							    "" + cam.uniqueID);
					    id++;
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
	case R.id.menu_active_md:
	    int indice;
	    if ((indice = MotionDetectionService.isAlreadyRunning(cam)) != -1) {
		Log.i(getString(R.string.logTag), "Remove cam " + indice);
		MotionDetectionService.stopRunningDetection(cam,
			this.getApplication(), indice);
	    } else {

		if (!MDWindowSelector) {
		    advanceCtrl = true;
		    setContentView(R.layout.mds_video);
		    Button ok = (Button) findViewById(R.id.okRectView);
		    ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    try {
				// A REMPLACER PAR LES PRIMITIVES AJOUTER UN
				// DIALOG AVEC UNE
				// BARRE POUR LA SENSIBILITE
				camC.sendCommand("axis-cgi/operator/param.cgi?action=remove&group=Motion.M1,group=Motion.M2,group=Motion.M3,group=Motion.M4,group=Motion.M5");
				camC.sendCommand("axis-cgi/operator/param.cgi?action=add&group=Motion&template=motion");
				Intent intent = new Intent(v.getContext(),
					MotionDetectionService.class);
				Bundle objetbunble = new Bundle();
				objetbunble.putSerializable(
					getString(R.string.camTag), cam);
				intent.putExtras(objetbunble);
				int lim = Integer.parseInt(Home.preferences
					.getString(
						getString(R.string.SeuilDM),
						getString(R.string.defaultSeuilDM)));
				intent.putExtra("limit", lim);
				long delay = Long.parseLong(Home.preferences
					.getString(
						getString(R.string.NotifTO),
						getString(R.string.defaultNotifTO)));
				intent.putExtra("delay", delay);
				Log.i(getString(R.string.logTag),
					"Start service");
				startService(intent);
			    } catch (IOException e) {
				e.printStackTrace();
			    }
			}
		    });
		} else {
		    MDWindowSelector = false;
		    advanceCtrl = false;
		    setContentView(R.layout.video);
		}
		mv = (MjpegView) findViewById(R.id.surfaceView1);
		start_connection(mv, url);
		mv.setOnTouchListener(new TouchListener(camC));
		return true;
	    }
	}
	return false;
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
     * Resume video and acquire wakelock when activity resume.
     */
    public void onResume() {
	super.onResume();
	wl.acquire();
	if (pause) {
	    mv.resumePlayback();
	    pause = false;
	}

    }

    /**
     * Stop video and release wakelock when activity sleep
     */
    public void onPause() {
	pause = true;
	wl.release();
	super.onPause();
    }

    /**
     * Stop Video before destroy
     */
    public void onDestroy() {
	mv.stopPlayback();
	super.onDestroy();

    }
}

package com.myapps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.myapps.utils.base64Encoder;
import com.myapps.utils.notificationLauncher;

public class MotionDetectionService extends Service {
    private Camera cam;
    private int limit;
    private long delay;
    private Thread t;
    private int ID_NOTIFICATION = 10;
    private static Vibrator vibreur;

    public final static int MVTMSG = 5;
    public static Handler myViewUpdateHandler = new Handler() {
	public void handleMessage(Message msg) {
	    if (msg.what == MVTMSG) {
		Log.i("AppLog", "Mouvement !");
		vibreur.vibrate(1000);
	    }

	    super.handleMessage(msg);
	}
    };

    @Override
    public void onCreate() {
	super.onCreate();
	Log.i("AppLog", "onCreate");
	vibreur = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public HttpURLConnection sendCommand(String command) throws IOException {
	URL url = null;
	HttpURLConnection con = null;
	url = new URL(cam.getURI() + command);
	con = (HttpURLConnection) url.openConnection();
	con.setDoOutput(true);
	con.setRequestProperty("Authorization",
		base64Encoder.userNamePasswordBase64(cam.login, cam.pass));
	con.connect();
	return con;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.i("AppLog", "onStart");
	/* Recover arguments */
	Bundle extras = intent.getExtras();
	cam = (Camera) extras.getSerializable(getString(R.string.camTag));
	limit = extras.getInt("limit");
	delay = extras.getLong("delay");

	Intent notificationIntent = new Intent(getApplicationContext(),
		Video.class);
	Bundle objetbunble = new Bundle();
	objetbunble.putSerializable(getString(R.string.camTag), cam);
	notificationIntent.putExtras(objetbunble);
	notificationIntent.putExtra("isCalledFromService", true);

	notificationLauncher.statusBarNotificationRunning(
		this.getApplication(), notificationIntent, ID_NOTIFICATION);

	t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		HttpURLConnection con;
		Log.i("AppLog", "thread run");
		try {
		    con = sendCommand("axis-cgi/motion/motiondata.cgi?Sensitivity=65&History=50&Size=25");
		    InputStreamReader isr = new InputStreamReader(con
			    .getInputStream());
		    BufferedReader br = new BufferedReader(isr);
		    String s;
		    int lvlc, lvlb, lvlf;
		    long last = System.currentTimeMillis();
		    while (!Thread.currentThread().isInterrupted()) {
			s = br.readLine();
			if (s.contains("level=") == true) {
			    lvlc = s.indexOf("level=");
			    s = s.substring(lvlc);
			    lvlb = s.indexOf("=");
			    lvlf = s.indexOf(";");
			    s = s.substring(lvlb + 1, lvlf);
			    if (Integer.parseInt(s) > limit) {
				Message m = new Message();
				m.what = MotionDetectionService.MVTMSG;
				if (last + delay < System.currentTimeMillis()) {
				    MotionDetectionService.myViewUpdateHandler
					    .sendMessage(m);
				    last = System.currentTimeMillis();
				}
			    }
			}
		    }
		} catch (IOException e) {
		    Log.i("AppLog", "IOE");
		    e.printStackTrace();
		}
	    }
	});
	t.start();
	return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
	Log.d(this.getClass().getName(), "onDestroy");
	notificationLauncher.removeStatusBarNotificationRunning(
		this.getApplication(), ID_NOTIFICATION);
	t.interrupt();
	super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }
}
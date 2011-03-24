package com.myapps.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.myapps.*;
import com.myapps.utils.CouldNotCreateGroupException;
import com.myapps.utils.notificationLauncher;
import com.myapps.utils.snapShotManager;
import com.myapps.utils.xmlIO;

public class CameraTest extends
	android.test.ActivityInstrumentationTestCase2<Home> {
    Activity mActivity;
    Camera src, tmp;
    public static ArrayList<Camera> camList;

    private String exportPath = "/sdcard/com.myapps.camera/";
    private String exportName = "export.xml";

    public CameraTest() {
	super("com.myapps.Home", Home.class);

    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	mActivity = this.getActivity();
	src = new Camera("test", "root", "root", "192.168.1.20", 80, "http", 1);
    }

    public void testimport() {
	/* Ajout 3 cameras */
	camList = new ArrayList<Camera>();
	int position = camList.size();
	src.setUniqueID(position);
	camList.add(position, src);
	assertEquals(camList.size(), 1);

	position = camList.size();
	src.setUniqueID(position);
	camList.add(position, src);
	assertEquals(camList.size(), 2);

	position = camList.size();
	src.setUniqueID(position);
	camList.add(position, src);
	assertEquals(camList.size(), 3);

	/* Export */
	boolean res = xmlIO.xmlWrite(camList, exportPath, exportName);
	assertEquals(res, true);

	/* Purge la liste */
	camList.clear();
	assertEquals(camList.size(), 0);

	/* Import */
	camList = xmlIO.xmlRead(exportPath + exportName);
	assertEquals(camList.size(), 3);

    }

    public void testNotification() {
	/* ajout notif */
	int index = 5;
	Resources res = mActivity.getResources();
	Drawable drawable = res
		.getDrawable(com.myapps.R.drawable.hello_android);
	Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
	notificationLauncher.statusBarNotificationImage(mActivity, bmp, "test",
		"blabla", index, "tag");
	NotificationManager notificationManager;
	notificationManager = (NotificationManager) mActivity
		.getSystemService(Context.NOTIFICATION_SERVICE);

	/* suppr notif */
	notificationManager.cancel("tag", index);
    }

    public void testRunningNotification() {
	int index = 10;
	Intent notificationIntent = new Intent(
		mActivity.getApplicationContext(), Video.class);

	Bundle objetbunble = new Bundle();
	objetbunble.putSerializable(
		mActivity.getString(com.myapps.R.string.camTag), src);
	notificationIntent.putExtras(objetbunble);
	notificationIntent.setAction(Intent.ACTION_MAIN);
	/*
	 * Set data because filterEquals() compare intents without extras
	 * !!!!!!!!!
	 */
	notificationIntent.setDataAndType(Uri.parse("value"), "Camera");
	PendingIntent contentIntent = PendingIntent.getActivity(
		mActivity.getApplicationContext(), 0, notificationIntent, 0);
	notificationLauncher
		.statusBarNotificationRunning(mActivity.getApplication(),
			contentIntent, index, "ca tourne !");

	notificationLauncher.removeStatusBarNotificationRunning(
		mActivity.getApplication(), index);
    }

    public void testSendCommand() {
	CameraControl c = new CameraControl(src, mActivity);
	HttpURLConnection res = null;
	try {
	    res = c.sendCommand("axis-cgi/com/ptz.cgi?info=1&camera=1");

	} catch (IOException e) {
	    e.printStackTrace();
	}
	assertNotNull(res);
    }

    public void testSnap() {
	CameraControl camC = new CameraControl(src, mActivity);
	String[] resolutions = camC.getResolutions();
	String fileNameURL = "/sdcard/com.myapps.camera/";
	String fileName = "Snap-" + System.currentTimeMillis() + ".jpeg";
	Bitmap bmp;
	try {
	    bmp = camC.takeSnapshot(resolutions[0]);
	    assertNotNull(bmp);
	    snapShotManager.saveSnap(bmp, fileNameURL, fileName);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void testAddRemoveMotionDetection() {
	CameraControl c = new CameraControl(src, mActivity);
	assertNotNull(c);
	int groupe = -1;
	try {
	    groupe = c.addMotionD();
	    assertNotSame(groupe, -1);

	    groupe = c.removeMotionD();
	    assertEquals(groupe, -1);
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (CouldNotCreateGroupException e) {
	    e.printStackTrace();
	}
    }

    public void testMD() {
	try {
	    CameraControl c = new CameraControl(src, mActivity);
	    int group = c.addMotionD();
	    c.cam.setGroup(group);
	    
	    Intent intent = new Intent(mActivity, MotionDetectionService.class);
	    Bundle objetbunble = new Bundle();
	    objetbunble.putSerializable(mActivity.getString(R.string.camTag),
		    c.cam);
	    intent.putExtras(objetbunble);
	    int lim = 20;
	    long delay = 1000;
	    intent.putExtra("limit", lim);
	    intent.putExtra("delay", delay);
	    mActivity.startService(intent);
	    while(MotionDetectionService.detected == false){
		Thread.sleep(1000);
		/* passer devant la cam pour continuer */
	    }
	    
	    int indice;
	    if ((indice = MotionDetectionService.isAlreadyRunning(c.cam)) != -1) {
		MotionDetectionService.stopRunningDetection(c.cam,
			mActivity.getApplication(), indice);

		c.removeMotionD();
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	} catch (CouldNotCreateGroupException e) {
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

    }

}

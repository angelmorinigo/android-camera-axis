package com.myapps.utils;

import java.io.File;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.myapps.Camera;
import com.myapps.R;
import com.myapps.Video;

public class notificationLauncher {
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
    public static void statusBarNotificationImage(Activity activity, Bitmap bmp,
	    String text, String path, int id, String tag) {
	NotificationManager notificationManager;
	notificationManager = (NotificationManager) activity
		.getSystemService(Context.NOTIFICATION_SERVICE);
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
	notificationManager.notify(tag, id, notification);
    }

    public static void statusBarNotificationRunning(Application application, PendingIntent contentIntent,
	    int id, String text) {
	/* Notification Running */
	NotificationManager mNotificationManager;
	mNotificationManager = (NotificationManager) application
		.getApplicationContext().getSystemService(
			Context.NOTIFICATION_SERVICE);
	Notification notification = new Notification(R.drawable.camera,
		"Camera-Axis Motion Detection", System.currentTimeMillis());
	notification.flags |= Notification.FLAG_ONGOING_EVENT;

	//launcher.setAction(Intent.ACTION_MAIN);
	//launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


	notification.setLatestEventInfo(application.getApplicationContext(),
		"Camera-Axis",  text,
		contentIntent);
	mNotificationManager.notify(id, notification);

    }

    public static void removeStatusBarNotificationRunning(
	    Application application, int id) {
	NotificationManager mNotificationManager;

	mNotificationManager = (NotificationManager) application
		.getApplicationContext().getSystemService(
			Context.NOTIFICATION_SERVICE);
	mNotificationManager.cancel(id);
    }
}

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
import android.widget.RemoteViews;

import com.myapps.R;

/**
 * 
 * Manages the different notifications launched in the StatusBar
 *
 */
public class notificationLauncher {
    /**
     * Create a StatusBar notification for Snapshot
     * @param activity The current activity
     * @param bmp The Snapshot recorded
     * @param text A message (like URL)
     * @param path The snapshot URL used to start gallery activity by touching
     *            notification
     * @param id A unique id for the notification
     * @param tag A tag for the notification
     */
    public static boolean statusBarNotificationImage(Activity activity, Bitmap bmp,
	    String text, String path, int id, String tag) {
	NotificationManager notificationManager;
	notificationManager = (NotificationManager) activity
		.getSystemService(Context.NOTIFICATION_SERVICE);
	Notification notification = new Notification(R.drawable.camera,
		"Camera-Axis", System.currentTimeMillis());
	notification.contentView = new RemoteViews(activity.getPackageName(),
		R.layout.notification);
	/* Action performed on click on notification */
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
	return true;
    }

    /**
     * Create a StatusBar notification for Motion Detection instance
     * @param application The current application
     * @param contentIntent The intent to launch on click
     * @param id A unique id for the notification
     * @param tag A text for the notification
     */
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

	notification.setLatestEventInfo(application.getApplicationContext(),
		"Camera-Axis", text, contentIntent);
	mNotificationManager.notify(id, notification);

    }

    /**
     * Remove an existing notification in the StatusBar
     * @param application The current application
     * @param id The notification ID to remove
     */
    public static void removeStatusBarNotificationRunning(
	    Application application, int id) {
	NotificationManager mNotificationManager;

	mNotificationManager = (NotificationManager) application
		.getApplicationContext().getSystemService(
			Context.NOTIFICATION_SERVICE);
	mNotificationManager.cancel(id);
    }
}

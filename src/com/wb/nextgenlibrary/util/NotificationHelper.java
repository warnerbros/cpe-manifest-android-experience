package com.wb.nextgenlibrary.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {

	private Context context;
	private int id;
	private int tagId = 1;
	private NotificationCompat.Builder builder;
	private NotificationManager notificationManager;

	public NotificationHelper(Context context, int id) {
		this.context = context;
		this.id = id;
		builder = new NotificationCompat.Builder(context);
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	/**
	 * Set parameters for a simple Notification
	 * @param icon
	 * @param title
	 * @param text
	 */
	public void setSimpleNotification(int icon, CharSequence title, CharSequence text) {
		builder.setSmallIcon(icon);
		builder.setContentTitle(title);
		builder.setContentText(text);
	}
	
	/**
	 * notification is automatically canceled when the user clicks it in the panel
	 * @param autoCancel
	 */
	public void setAutoCancel(boolean autoCancel) {
		builder.setAutoCancel(autoCancel);
	}

	/**
	 * 
	 * @param resultIntent
	 */
    public void setNotificationClick(Intent resultIntent) {
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        builder.setContentIntent(resultPendingIntent);
    }
    
    public void displayNewNotification() {
    	notificationManager.notify(Integer.toString(++tagId), id, builder.build());
    }
}

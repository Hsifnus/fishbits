package com.melonfishy.sleepycat.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.melonfishy.sleepycat.MainActivity;
import com.melonfishy.sleepycat.R;

public class NotificationUtils {

    private static final int EVENT_ALARM_NOTIFICATION_ID = 500;

    private static final int EVENT_ALARM_PENDING_INTENT_ID = 600;
    private static final int ACTION_STOP_ALARM_PENDING_INTENT_ID = 6001;
    private static final int ACTION_SEE_DETAILS_PENDING_INTENT_ID = 6002;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(intent);
    }

    public static void alarmUser(Context context, Bundle bundle) {

        String notification_title = "";
        String notification_body = "";

        String date, eventTime, eventTitle, eventDetails;
        try {
            date = bundle.getString(ScheduleUtils.SCHEDULE_DATE_PATH);
            eventTime = bundle.getString(ScheduleUtils.SCHEDULE_START_TIME);
            eventTitle = bundle.getString(ScheduleUtils.SCHEDULE_TITLE);
            if (eventTime != null) {
                eventTime = new ClockTime(eventTime).toString();
            }
            eventDetails = bundle.getString(ScheduleUtils.SCHEDULE_DETAILS);
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new IllegalStateException("Invalid job params!");
        }

        notification_title = "An event is approaching!";
        notification_body = eventTime.replaceAll(" ", "") + " ~ " + date + ": \n" + eventTitle;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_sleepycat_white)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        notification_body))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(seeDetailsAction(context, bundle))
                .addAction(ignoreAlarmAction(context, bundle))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(EVENT_ALARM_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_sleepycat);
        return largeIcon;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                EVENT_ALARM_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationCompat.Action ignoreAlarmAction(Context context, Bundle bundle) {
        Intent ignoreAlarmIntent = new Intent(context, EventAlarmIntentService.class);
        ignoreAlarmIntent.putExtra(ScheduleUtils.SCHEDULE_BUNDLE, bundle);
        ignoreAlarmIntent.setAction(EventAlarmTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreAlarmPendingIntent = PendingIntent.getService(
                context,
                ACTION_STOP_ALARM_PENDING_INTENT_ID,
                ignoreAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_delete_24px,
                "Dismiss",
                ignoreAlarmPendingIntent);
    }

    private static NotificationCompat.Action seeDetailsAction(Context context, Bundle bundle) {
        Intent seeDetailsIntent = new Intent(context, EventAlarmIntentService.class);
        seeDetailsIntent.putExtra(ScheduleUtils.SCHEDULE_BUNDLE, bundle);
        seeDetailsIntent.setAction(EventAlarmTasks.ACTION_SEE_DETAILS);
        PendingIntent seeDetailsPendingIntent = PendingIntent.getService(
                context,
                ACTION_SEE_DETAILS_PENDING_INTENT_ID,
                seeDetailsIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
       return new NotificationCompat.Action(R.drawable.ic_proceed_24px,
                "See Details",
                seeDetailsPendingIntent);
    }
}

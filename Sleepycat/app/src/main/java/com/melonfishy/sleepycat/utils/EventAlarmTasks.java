package com.melonfishy.sleepycat.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.melonfishy.sleepycat.MainActivity;

public class EventAlarmTasks {

    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_ALARM_REMINDER = "charging-reminder";
    public static final String ACTION_SEE_DETAILS = "see-details";

    public static void executeTask(Context context, String action, Bundle bundle) {
        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_ALARM_REMINDER.equals(action)) {
            issueAlarmReminder(context, bundle);
        } else if (ACTION_SEE_DETAILS.equals(action)) {
            // TODO Navigate user to the appropriate EventDetailActivity page
            ContentResolver resolver = context.getContentResolver();
            NotificationUtils.clearAllNotifications(context);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(ScheduleUtils.SCHEDULE_BUNDLE, bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private static void issueAlarmReminder(Context context, Bundle bundle) {
        NotificationUtils.alarmUser(context, bundle);
    }

}

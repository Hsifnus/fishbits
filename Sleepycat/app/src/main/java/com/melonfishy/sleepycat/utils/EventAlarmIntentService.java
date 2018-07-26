package com.melonfishy.sleepycat.utils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class EventAlarmIntentService extends IntentService {

    public EventAlarmIntentService () {super("EventAlarmIntentService");}

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getBundleExtra(ScheduleUtils.SCHEDULE_BUNDLE);
        EventAlarmTasks.executeTask(this, action, bundle);
    }
}

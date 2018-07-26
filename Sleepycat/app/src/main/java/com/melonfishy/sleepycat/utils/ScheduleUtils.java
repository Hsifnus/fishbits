package com.melonfishy.sleepycat.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.melonfishy.sleepycat.EventDetailActivity;
import com.melonfishy.sleepycat.R;
import com.melonfishy.sleepycat.data.ScheduleDbContract;

import java.util.Date;

public class ScheduleUtils {

    public static final String SCHEDULE_UPPER_HEADER = "scheduleUpperHeader";
    public static final String SCHEDULE_LOWER_HEADER = "scheduleLowerHeader";
    public static final String SCHEDULE_DATE_PATH = "scheduleDatePath";

    public static final String SCHEDULE_START_TIME = "scheduleStartTime";
    public static final String SCHEDULE_END_TIME = "scheduleEndTime";
    public static final String SCHEDULE_ALARM_OFFSET = "scheduleAlarmOffset";
    public static final String SCHEDULE_TITLE = "scheduleTitle";
    public static final String SCHEDULE_DETAILS = "scheduleDetails";
    public static final String SCHEDULE_ID = "scheduleID";

    public static final String SCHEDULE_BUNDLE = "scheduleBundle";

    public static final int REQUEST_ADD_EVENT = 200;
    public static final int REQUEST_EDIT_EVENT = 201;

    public static final int RESULT_EVENT_ADDED = 301;
    public static final int RESULT_NO_CHANGE = 300;
    public static final int RESULT_EVENT_UPDATED = 302;
    public static final int RESULT_EVENT_DELETED = 303;

    private static SparseBooleanArray eventStates = new SparseBooleanArray();
    private static final long ALARM_FLEXTIME_MINUTES = 0;
    private static final int ALARM_DELAY_OFFSET_SECONDS = 20;
    private static final int ALARM_FLEXTIME_SECONDS = 40;
    private static Integer currentID;

    public static String formatMonthAndDay(int month, int day) {
        String m = "";
        switch(month) {
            case 1:
                m = "January ";
                break;
            case 2:
                m = "February ";
                break;
            case 3:
                m = "March ";
                break;
            case 4:
                m = "April ";
                break;
            case 5:
                m = "May ";
                break;
            case 6:
                m = "June ";
                break;
            case 7:
                m = "July ";
                break;
            case 8:
                m = "August ";
                break;
            case 9:
                m = "September ";
                break;
            case 10:
                m = "October ";
                break;
            case 11:
                m = "November ";
                break;
            case 12:
                m = "December ";
                break;
            default:
                throw new IllegalArgumentException(month + " is not a valid month number (1 - 12)");
        }
        return m + day;
    }

    public static String formatDatePath(int year, int month, int day) {
        String monthString = (month >= 10) ? String.valueOf(month)
                : "0" + String.valueOf(month);
        String dayString = (day >= 10) ? String.valueOf(day)
                : "0" + String.valueOf(day);
        return monthString + "_" + dayString + "_" + String.valueOf(year);
    }

    public static String clockTimeToString(int h, int min) {
        return new ClockTime(h, min).queryString();
    }

    public static String displayQueryString(String qString) {
        return new ClockTime(qString).toString();
    }

    public static void insertFakeDataIntoDatabase(Context context) {
        ContentValues cv = new ContentValues();
        context.getContentResolver().delete(ScheduleDbContract.ScheduleEntry.CONTENT_URI, null, null);
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, clockTimeToString(0, 15));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DATE, "07_04_2017");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, "Have fun!");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, clockTimeToString(19, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, clockTimeToString(23, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, "4th of July!");
        Uri targetUri = ScheduleDbContract.ScheduleEntry.CONTENT_URI;
        context.getContentResolver().insert(targetUri, cv);

        cv = new ContentValues();
        context.getContentResolver().delete(ScheduleDbContract.ScheduleEntry.CONTENT_URI, null, null);
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, "48_00");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DATE, "07_06_2017");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, "Does it say July 4th?");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, clockTimeToString(14, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, "none");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, "Here's a test!");
        context.getContentResolver().insert(targetUri, cv);

        cv = new ContentValues();
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, clockTimeToString(0, 15));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DATE, "07_04_2017");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, "Set up party supplies.");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, clockTimeToString(18, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, clockTimeToString(19, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, "Party Setup");
        context.getContentResolver().insert(targetUri, cv);

        cv = new ContentValues();
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, clockTimeToString(0, 15));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DATE, "07_04_2017");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, "Discuss lunch options with friend " +
                "beforehand.");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, clockTimeToString(13, 13));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, "none");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, "Lunch with Friend at Brea Mall");
        context.getContentResolver().insert(targetUri, cv);

        cv = new ContentValues();
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, clockTimeToString(0, 15));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DATE, "07_04_2017");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, "Remember to vacuum garage carpets!");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, clockTimeToString(10, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, clockTimeToString(11, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, "Cleaning Chores");
        context.getContentResolver().insert(targetUri, cv);

        cv = new ContentValues();
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, clockTimeToString(0, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DATE, "07_04_2017");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, "Brush teeth, eat breakfast, whatnot");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, clockTimeToString(9, 0));
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, "none");
        cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, "Rise and Shine!");
        context.getContentResolver().insert(targetUri, cv);
    }

    public static boolean isClockTime(String time) {
        return !(time == null || !(time.matches("[0-9]{2}:[0-9]{2}")
                || time.matches("[0-9]{2}_[0-9]{2}")));
    }

    public static long timeToSeconds(String time) {
        if (!isClockTime(time)) {
            throw new IllegalArgumentException("Given time string is invalid: " + time);
        } else {
            int hours = Integer.valueOf(time.substring(0, 2));
            int minutes = Integer.valueOf(time.substring(3, 5));
            return hours * 3600 + minutes * 60;
        }
    }

    public static String getAlarmDate(String date, String currentTime, String alarmTime) {
        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
        try {
            long alarmResult = format.parse(date).getTime() + timeToSeconds(currentTime) * 1000
                    - timeToSeconds(alarmTime) * 1000;
            return format.format(new Date(alarmResult));
        } catch (ParseException e) {
            e.printStackTrace();
            return "NaN";
        }
    }

    private static boolean isInitialized(int id) {
        return eventStates.get(id);
    }

    synchronized public static void scheduleEvent(@NonNull final Context context, int id,
                                                  String date, String eventTime, String alarmTime,
                                                String eventTitle, String eventDetails) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        String eventTag = id + "";

        long alarmMillis;
        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
        try {
            alarmMillis = format.parse(date).getTime() + timeToSeconds(eventTime) * 1000
                    - timeToSeconds(alarmTime) * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to parse time.");
        }

        long currentMillis = System.currentTimeMillis();
        long interval = 9 * (alarmMillis - currentMillis) / 10000;

        if (interval >= 0) {
            Bundle bundle = new Bundle();
            bundle.putString(SCHEDULE_DATE_PATH, date);
            bundle.putString(SCHEDULE_START_TIME, eventTime);
            bundle.putString(SCHEDULE_TITLE, eventTitle);
            bundle.putString(SCHEDULE_DETAILS, eventDetails);

            Job eventScheduleJob = dispatcher.newJobBuilder()
                    .setExtras(bundle)
                    .setService(EventScheduleFirebaseJobService.class)
                    .setTag(eventTag)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(false)
                    .setReplaceCurrent(true)
                    .setTrigger(Trigger
                            .executionWindow(Math.max(
                                    (int) interval - ALARM_DELAY_OFFSET_SECONDS, 0),
                                    Math.max((int) interval
                                            + ALARM_FLEXTIME_SECONDS
                                            - ALARM_DELAY_OFFSET_SECONDS, 0)))
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build();

            dispatcher.mustSchedule(eventScheduleJob);
            eventStates.append(id, true);
            Log.d(ScheduleUtils.class.getSimpleName(), "Event (ID: " + id + ") " +
                    "scheduled to sound alarm in "
            + interval + " to " + (interval + ALARM_FLEXTIME_SECONDS) + " seconds.");
        }
    }

    synchronized public static void cancelEvent(@NonNull final Context context, int id) {
        if (!eventStates.get(id)) {
            return;
        }

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        String event_tag = id + "";
        Log.d(EventDetailActivity.class.getSimpleName(), "Event (ID: " + id + ") deleted.");

        dispatcher.cancel(event_tag);
        eventStates.append(id, false);
    }
    
    public static void setCurrentID(int count) {
        currentID = count;
    }
    
    public static Integer getCurrentID() {
        return currentID;
    }

    public static void initializeLogo(AppCompatActivity act) {
        act.getSupportActionBar().setDisplayShowHomeEnabled(true);
        act.getSupportActionBar().setIcon(R.drawable.sleepycat_toolbar);
        act.setTitle("");
    }
}

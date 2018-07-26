package com.melonfishy.sleepycat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "schedule.db";
    private static final int DATABASE_VERSION = 1;

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + ScheduleDbContract.ScheduleEntry.TABLE_NAME + " (" +
                ScheduleDbContract.ScheduleEntry._ID                + " INTEGER PRIMARY KEY, " +
                ScheduleDbContract.ScheduleEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME    + " TEXT NOT NULL," +
                ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME    + " TEXT NOT NULL," +
                ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET    + " TEXT NOT NULL," +
                ScheduleDbContract.ScheduleEntry.COLUMN_TITLE    + " TEXT NOT NULL," +
                ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS    + " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDbContract.ScheduleEntry.TABLE_NAME);
    }

}

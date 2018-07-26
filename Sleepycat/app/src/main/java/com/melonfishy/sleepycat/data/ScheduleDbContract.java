package com.melonfishy.sleepycat.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ScheduleDbContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.melonfishy.sleepycat";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_SCHEDULE = "schedule";

    public static final class ScheduleEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE)
                .build();
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_START_TIME = "startTime";
        public static final String COLUMN_END_TIME = "endTime";
        public static final String COLUMN_ALARM_OFFSET = "alarmOffset";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DETAILS = "details";
    }
}

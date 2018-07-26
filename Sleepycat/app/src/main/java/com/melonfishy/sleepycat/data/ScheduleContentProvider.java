package com.melonfishy.sleepycat.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.melonfishy.sleepycat.utils.ScheduleUtils;

public class ScheduleContentProvider extends ContentProvider {

    private static final int EVENTS = 100;
    private static final int EVENTS_WITH_DATE = 101;
    private static final int EVENTS_WITH_DATE_AND_ID = 102;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ScheduleDbContract.AUTHORITY, ScheduleDbContract.PATH_SCHEDULE, EVENTS);
        matcher.addURI(ScheduleDbContract.AUTHORITY, ScheduleDbContract.PATH_SCHEDULE + "/*",
                EVENTS_WITH_DATE);
        matcher.addURI(ScheduleDbContract.AUTHORITY, ScheduleDbContract.PATH_SCHEDULE + "/*/#",
                EVENTS_WITH_DATE_AND_ID);
        return matcher;
    }

    private ScheduleDbHelper mScheduleHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mScheduleHelper = new ScheduleDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mScheduleHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EVENTS:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(ScheduleDbContract.ScheduleEntry.TABLE_NAME, null, values);
                ScheduleUtils.setCurrentID((int) id);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(ScheduleDbContract.ScheduleEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown or invalid uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mScheduleHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor resultCursor;
        String date, selectClause;
        String[] selectArgs;

        switch(match) {
            case EVENTS:
                resultCursor = db.query(ScheduleDbContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case EVENTS_WITH_DATE:
                date = uri.getPathSegments().get(1);
                selectClause = ScheduleDbContract.ScheduleEntry.COLUMN_DATE + "=?";
                selectArgs = new String[]{date};
                resultCursor = db.query(ScheduleDbContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selectClause,
                        selectArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case EVENTS_WITH_DATE_AND_ID:
                date = uri.getPathSegments().get(1);
                String id = uri.getPathSegments().get(2);
                selectClause = ScheduleDbContract.ScheduleEntry.COLUMN_DATE + "=? AND " +
                        ScheduleDbContract.ScheduleEntry._ID + "=?";
                selectArgs = new String[]{date, id};
                resultCursor = db.query(ScheduleDbContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selectClause,
                        selectArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return resultCursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mScheduleHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        String date, id, selectClause;
        String[] selectArgs;

        switch(match) {
            case EVENTS_WITH_DATE_AND_ID:
                date = uri.getPathSegments().get(1);
                id = uri.getPathSegments().get(2);
                selectClause = ScheduleDbContract.ScheduleEntry.COLUMN_DATE + "=? AND " +
                        ScheduleDbContract.ScheduleEntry._ID + "=?";
                selectArgs = new String[]{date, id};
                rowsUpdated = db.update(ScheduleDbContract.ScheduleEntry.TABLE_NAME, values,
                        selectClause, selectArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown or invalid URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mScheduleHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        String id, date, selectClause;
        String[] selectArgs;

        switch (match) {
            case EVENTS:
                rowsDeleted = db.delete(ScheduleDbContract.ScheduleEntry.TABLE_NAME, null, null);
                break;
            case EVENTS_WITH_DATE:
                date = uri.getPathSegments().get(1);
                selectClause = ScheduleDbContract.ScheduleEntry.COLUMN_DATE + "=?";
                selectArgs = new String[]{date};
                rowsDeleted = db.delete(ScheduleDbContract.ScheduleEntry.TABLE_NAME,
                        selectClause, selectArgs);
                break;
            case EVENTS_WITH_DATE_AND_ID:
                date = uri.getPathSegments().get(1);
                id = uri.getPathSegments().get(2);
                selectClause = ScheduleDbContract.ScheduleEntry.COLUMN_DATE + "=? AND " +
                        ScheduleDbContract.ScheduleEntry._ID + "=?";
                selectArgs = new String[]{date, id};
                rowsDeleted = db.delete(ScheduleDbContract.ScheduleEntry.TABLE_NAME,
                        selectClause, selectArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown or invalid URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Operation not implemented.");
    }
}

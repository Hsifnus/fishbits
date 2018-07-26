package com.melonfishy.sleepycat;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.melonfishy.sleepycat.data.ScheduleDbContract;
import com.melonfishy.sleepycat.utils.ScheduleUtils;

import org.w3c.dom.Text;

public class ScheduleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ScheduleAdapter.ScheduleAdapterOnClickHandler {

    private TextView mUpperHeader, mLowerHeader;
    private RecyclerView mScheduleList;
    private ScheduleAdapter mAdapter;
    private ImageButton mAddEventButton, mBackButton;
    private static final int SCHEDULE_LOADER_ID = 0;
    private static final String TAG = ScheduleActivity.class.getSimpleName();
    private String datePath;

    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_WEATHER_DATE = 1;
    public static final int INDEX_WEATHER_START_TIME = 2;
    public static final int INDEX_WEATHER_END_TIME = 3;
    public static final int INDEX_WEATHER_ALARM_OFFSET = 4;
    public static final int INDEX_WEATHER_TITLE = 5;
    public static final int INDEX_WEATHER_DETAILS = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ScheduleUtils.initializeLogo(this);

        mScheduleList = (RecyclerView) findViewById(R.id.rv_schedule_list);
        mScheduleList.setLayoutManager(new LinearLayoutManager(this));
        mUpperHeader = (TextView) findViewById(R.id.tv_upper_header);
        mLowerHeader = (TextView) findViewById(R.id.tv_lower_header);
        mAddEventButton = (ImageButton) findViewById(R.id.ib_add_event);
        mAdapter = new ScheduleAdapter(this, this);
        mBackButton = (ImageButton) findViewById(R.id.ib_cancel_event);

        final Intent intent = getIntent();
        if (intent != null) {
            mUpperHeader.setText(intent.getStringExtra(ScheduleUtils.SCHEDULE_UPPER_HEADER));
            mLowerHeader.setText(intent.getStringExtra(ScheduleUtils.SCHEDULE_LOWER_HEADER));
            datePath = intent.getStringExtra(ScheduleUtils.SCHEDULE_DATE_PATH);
        }

        mScheduleList.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(SCHEDULE_LOADER_ID, null, this);

        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(ScheduleActivity.this, AddEventActivity.class);
                newIntent.putExtra(
                        ScheduleUtils.SCHEDULE_UPPER_HEADER,
                        intent.getStringExtra(ScheduleUtils.SCHEDULE_UPPER_HEADER));
                newIntent.putExtra(
                        ScheduleUtils.SCHEDULE_LOWER_HEADER,
                        intent.getStringExtra(ScheduleUtils.SCHEDULE_LOWER_HEADER));
                newIntent.putExtra(
                        ScheduleUtils.SCHEDULE_DATE_PATH,
                        intent.getStringExtra(ScheduleUtils.SCHEDULE_DATE_PATH));
                startActivityForResult(newIntent, ScheduleUtils.REQUEST_ADD_EVENT);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(SCHEDULE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                try {
                    Uri dateUri = ScheduleDbContract.ScheduleEntry.CONTENT_URI.buildUpon()
                            .appendPath(datePath).build();
                    return getContentResolver().query(dateUri,
                            null,
                            null,
                            null,
                            ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ScheduleUtils.REQUEST_ADD_EVENT) {
            if (resultCode == ScheduleUtils.RESULT_EVENT_ADDED) {
                getSupportLoaderManager().restartLoader(SCHEDULE_LOADER_ID, null,
                        ScheduleActivity.this);
            }
        } else if (requestCode == ScheduleUtils.REQUEST_EDIT_EVENT) {
            if (resultCode == ScheduleUtils.RESULT_EVENT_UPDATED
                    || resultCode == ScheduleUtils.RESULT_EVENT_DELETED) {
                getSupportLoaderManager().restartLoader(SCHEDULE_LOADER_ID, null,
                        ScheduleActivity.this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(String date, String startTime, String endTime, String alarmOffset,
                        String title, String detail, int id) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra(ScheduleUtils.SCHEDULE_UPPER_HEADER, mUpperHeader.getText());
        intent.putExtra(ScheduleUtils.SCHEDULE_LOWER_HEADER, mLowerHeader.getText());
        intent.putExtra(ScheduleUtils.SCHEDULE_DATE_PATH, date);
        intent.putExtra(ScheduleUtils.SCHEDULE_START_TIME, startTime);
        intent.putExtra(ScheduleUtils.SCHEDULE_END_TIME, endTime);
        intent.putExtra(ScheduleUtils.SCHEDULE_ALARM_OFFSET, alarmOffset);
        intent.putExtra(ScheduleUtils.SCHEDULE_TITLE, title);
        intent.putExtra(ScheduleUtils.SCHEDULE_DETAILS, detail);
        intent.putExtra(ScheduleUtils.SCHEDULE_ID, id);
        startActivityForResult(intent, ScheduleUtils.REQUEST_EDIT_EVENT);
    }
}

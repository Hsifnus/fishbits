package com.melonfishy.sleepycat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.melonfishy.sleepycat.utils.ClockTime;
import com.melonfishy.sleepycat.utils.ScheduleUtils;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifTextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private boolean alarmFlag = false;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private ImageButton mCalendarButton, mThemeButton, mSettingsButton;
    private GifTextView mForegroundGif;
    private TextView mAlarmTitle, mAlarmTime, mAlarmDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ScheduleUtils.initializeLogo(this);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        show();
        mCalendarButton = (ImageButton) findViewById(R.id.calendar_button);
        mThemeButton = (ImageButton) findViewById(R.id.theme_button);
        mSettingsButton = (ImageButton) findViewById(R.id.settings_button);
        mForegroundGif = (GifTextView) findViewById(R.id.gft_foreground);

        mAlarmTitle = (TextView) findViewById(R.id.tv_main_alarm_title);
        mAlarmTime = (TextView) findViewById(R.id.tv_main_alarm_time);
        mAlarmDetails = (TextView) findViewById(R.id.tv_main_alarm_details);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ScheduleUtils.SCHEDULE_BUNDLE)) {
            setAlarmState(true);
        } else {
            setAlarmState(false);
        }

        // ScheduleUtils.insertFakeDataIntoDatabase(this);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmFlag) {
                    setAlarmState(false);
                }
            }
        });

        mCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
                setAlarmState(false);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void setAlarmState(boolean state) {
        alarmFlag = state;
        if (!state) {
            mForegroundGif.setBackgroundResource(R.drawable.sleepycat_fg_draft);
            mAlarmTitle.setVisibility(View.GONE);
            mAlarmTime.setVisibility(View.GONE);
            mAlarmDetails.setVisibility(View.GONE);
        } else {
            mForegroundGif.setBackgroundResource(R.drawable.sleepycat_fg_alert_anim);
            mAlarmTitle.setVisibility(View.VISIBLE);
            mAlarmTime.setVisibility(View.VISIBLE);
            mAlarmDetails.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            if (intent == null || !intent.hasExtra(ScheduleUtils.SCHEDULE_BUNDLE)) {
                throw new IllegalArgumentException("Cannot invoke setAlarmState without " +
                        "sending an Intent to MainActivity.");
            }
            Bundle bundle = intent.getBundleExtra(ScheduleUtils.SCHEDULE_BUNDLE);
            String title = bundle.getString(ScheduleUtils.SCHEDULE_TITLE);
            String time = bundle.getString(ScheduleUtils.SCHEDULE_START_TIME);
            String date = bundle.getString(ScheduleUtils.SCHEDULE_DATE_PATH);
            String details = bundle.getString(ScheduleUtils.SCHEDULE_DETAILS);
            mAlarmTitle.setText(title);
            time = new ClockTime(time).toString();
            mAlarmTime.setText(time.replaceAll(" ", "") + " ~ " + date);
            mAlarmDetails.setText(details);
        }
    }
}

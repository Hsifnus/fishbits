package com.melonfishy.sleepycat;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.melonfishy.sleepycat.data.ScheduleDbContract;
import com.melonfishy.sleepycat.utils.ClockTime;
import com.melonfishy.sleepycat.utils.ScheduleUtils;

public class EditEventActivity extends AppCompatActivity {

    private TextView mUpperHeader, mLowerHeader;
    private ImageButton mBackButton, mConfirmButton;
    private EditText mTimeInputHour, mTimeInputMinute, mTimeInputMeridian,
            mTitleInput, mDescriptionInput, mTimeInputHourEnd, mTimeInputMinuteEnd,
            mTimeInputMeridianEnd, mTimeInputHourAlarm, mTimeInputMinuteAlarm;
    private String mDatePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        final Intent intent = getIntent();
        ScheduleUtils.initializeLogo(this);

        mUpperHeader = (TextView) findViewById(R.id.tv_upper_header_edit);
        mLowerHeader = (TextView) findViewById(R.id.tv_lower_header_edit);
        mUpperHeader.setText(intent.getStringExtra(ScheduleUtils.SCHEDULE_UPPER_HEADER));
        mLowerHeader.setText(intent.getStringExtra(ScheduleUtils.SCHEDULE_LOWER_HEADER));

        mBackButton = (ImageButton) findViewById(R.id.ib_cancel_event);
        mConfirmButton = (ImageButton) findViewById(R.id.ib_confirm_event);

        mTimeInputHour = (EditText) findViewById(R.id.et_time_text_edit_hour);
        mTimeInputMinute = (EditText) findViewById(R.id.et_time_text_edit_minute);
        mTimeInputMeridian = (EditText) findViewById(R.id.et_time_text_edit_meridian);

        mTimeInputHourEnd = (EditText) findViewById(R.id.et_time_text_edit_hour_end);
        mTimeInputMinuteEnd = (EditText) findViewById(R.id.et_time_text_edit_minute_end);
        mTimeInputMeridianEnd = (EditText) findViewById(R.id.et_time_text_edit_meridian_end);

        mTimeInputHourAlarm = (EditText) findViewById(R.id.et_time_text_edit_hour_alarm);
        mTimeInputMinuteAlarm = (EditText) findViewById(R.id.et_time_text_edit_minute_alarm);

        mTitleInput = (EditText) findViewById(R.id.et_title_text_edit);
        mDescriptionInput = (EditText) findViewById(R.id.et_description_text_edit);

        String startTime = fetchString(ScheduleUtils.SCHEDULE_START_TIME);
        mTimeInputMeridian.setText(rSubstring(startTime, 0, 2));
        mTimeInputMinute.setText(rSubstring(startTime, 3, 5));
        mTimeInputHour.setText(rSubstring(startTime, 6, startTime.length()).replaceAll("\\s",""));
        
        String endTime = fetchString(ScheduleUtils.SCHEDULE_END_TIME);
        mTimeInputMeridianEnd.setText(rSubstring(endTime, 0, 2));
        mTimeInputMinuteEnd.setText(rSubstring(endTime, 3, 5));
        mTimeInputHourEnd.setText(rSubstring(endTime, 6, endTime.length()).replaceAll("\\s",""));
        
        mTitleInput.setText(fetchString(ScheduleUtils.SCHEDULE_TITLE));
        mDescriptionInput.setText(fetchString(ScheduleUtils.SCHEDULE_DETAILS));

        String alarmOffset = fetchString(ScheduleUtils.SCHEDULE_ALARM_OFFSET);
        mTimeInputMinuteAlarm.setText(rSubstring(alarmOffset, 0, 2));
        mTimeInputHourAlarm.setText(rSubstring(alarmOffset,
                3, alarmOffset.length()).replaceAll("\\s",""));

        mDatePath = fetchString(ScheduleUtils.SCHEDULE_DATE_PATH);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventActivity.this.finalize(ScheduleUtils.RESULT_NO_CHANGE);
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeHourNum = 0;
                int timeMinuteNum = 0;
                try {
                    timeHourNum = Integer.valueOf(mTimeInputHour.getText().toString());
                    timeMinuteNum = Integer.valueOf(mTimeInputMinute.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(EditEventActivity.this,
                            R.string.add_event_time_blank_error,Toast.LENGTH_SHORT).show();
                }
                String timeMeridianText = mTimeInputMeridian.getText().toString();
                if (!timeMeridianText.equals("AM") && !timeMeridianText.equals("PM")) {
                    Toast.makeText(EditEventActivity.this,
                            R.string.add_event_time_error,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (timeHourNum > 12 || timeHourNum <= 0 || timeMinuteNum >= 60 || timeMinuteNum < 0) {
                    Toast.makeText(EditEventActivity.this,
                            R.string.add_event_time_error,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (timeMeridianText.equals("PM") && timeHourNum != 12) {
                    timeHourNum += 12;
                } else if (timeMeridianText.equals("AM") && timeHourNum == 12) {
                    timeHourNum = 0;
                }

                boolean noEndTime = false;
                int timeHourNumEnd = 0;
                int timeMinuteNumEnd = 0;
                try {
                    timeHourNumEnd = Integer.valueOf(mTimeInputHourEnd.getText().toString());
                    timeMinuteNumEnd = Integer.valueOf(mTimeInputMinuteEnd.getText().toString());
                } catch (NumberFormatException e) {
                    timeHourNumEnd = timeHourNum;
                    timeMinuteNumEnd = timeMinuteNum;
                    noEndTime = true;
                }
                String timeMeridianTextEnd = mTimeInputMeridianEnd.getText().toString();
                if (!noEndTime) {
                    if (!timeMeridianTextEnd.equals("AM") && !timeMeridianTextEnd.equals("PM")) {
                        Toast.makeText(EditEventActivity.this,
                                R.string.add_event_time_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (timeHourNumEnd > 12 || timeHourNumEnd <= 0 || timeMinuteNumEnd >= 60 || timeMinuteNumEnd < 0) {
                        Toast.makeText(EditEventActivity.this,
                                R.string.add_event_time_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (timeMeridianTextEnd.equals("PM") && timeHourNumEnd != 12) {
                        timeHourNumEnd += 12;
                    } else if (timeMeridianTextEnd.equals("AM") && timeHourNumEnd == 12) {
                        timeHourNumEnd = 0;
                    }
                    if (timeHourNumEnd < timeHourNum) {
                        Toast.makeText(EditEventActivity.this,
                                R.string.time_comparison_error, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (timeHourNum == timeHourNumEnd && timeMinuteNum > timeMinuteNumEnd) {
                        Toast.makeText(EditEventActivity.this,
                                R.string.time_comparison_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                int timeHourNumAlarm;
                int timeMinuteNumAlarm;
                try {
                    timeHourNumAlarm = Integer.valueOf(mTimeInputHourAlarm.getText().toString());
                    timeMinuteNumAlarm = Integer.valueOf(mTimeInputMinuteAlarm
                            .getText().toString());
                } catch (NumberFormatException e) {
                    timeHourNumAlarm = 0;
                    timeMinuteNumAlarm = 0;
                    e.printStackTrace();
                }

                if (timeMinuteNumAlarm >= 60) {
                    Toast.makeText(EditEventActivity.this,
                            R.string.invalid_alarm_time_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                String timeHourNumAlarmText = (timeHourNumAlarm >= 10)
                        ? String.valueOf(timeHourNumAlarm)
                        : "0" + timeHourNumAlarm;
                String timeMinuteNumAlarmText = (timeMinuteNumAlarm >= 10)
                        ? String.valueOf(timeMinuteNumAlarm)
                        : "0" + timeMinuteNumAlarm;

                ClockTime time = new ClockTime(timeHourNum, timeMinuteNum);
                ClockTime timeEnd = new ClockTime(timeHourNumEnd, timeMinuteNumEnd);
                String timeText = time.queryString();
                String titleText = mTitleInput.getText().toString();
                String descriptionText = mDescriptionInput.getText().toString();
                String timeEndText = timeEnd.queryString();
                String alarmText = timeHourNumAlarmText + "_" + timeMinuteNumAlarmText;

                if (alarmText.length() > 5) {
                    Toast.makeText(EditEventActivity.this,
                            R.string.invalid_alarm_time_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (timeHourNum == timeHourNumEnd && timeMinuteNum == timeMinuteNumEnd
                        && (noEndTime || timeMeridianText.equals(timeMeridianTextEnd))) {
                    timeEndText = "none";
                }

                if (!titleText.equals("")) {
                    int id = getIntent()
                                    .getIntExtra(ScheduleUtils.SCHEDULE_ID, 0);

                    ContentValues cv = new ContentValues();
                    cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_ALARM_OFFSET, alarmText);
                    cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_DETAILS, descriptionText);
                    cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME, timeText);
                    cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME, timeEndText);
                    cv.put(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE, titleText);

                    Uri contentUri = ScheduleDbContract.ScheduleEntry.CONTENT_URI
                            .buildUpon().appendPath(mDatePath)
                            .appendPath(String.valueOf(id))
                            .build();

                    int rowsUpdated = getContentResolver().update(contentUri, cv,
                            null, null);

                    Toast.makeText(EditEventActivity.this,
                            "Event updated!"
                            ,Toast.LENGTH_SHORT).show();

                    ScheduleUtils.scheduleEvent(EditEventActivity.this, id,
                            mUpperHeader.getText().toString() + ", "
                                    + mLowerHeader.getText().toString(),
                            time.queryString(), alarmText, titleText, descriptionText);
                    EditEventActivity.this.finalize(ScheduleUtils.RESULT_EVENT_UPDATED);
                } else {
                    Toast.makeText(EditEventActivity.this,
                            R.string.add_event_title_error,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String fetchString(String key) {
        return getIntent().getStringExtra(key);
    }

    public void finalize(int resultCode) {
        Intent data = new Intent();
        setResult(resultCode, data);
        finish();
    }

    private String rSubstring(String str, int start, int end) {
        int len = str.length();
        return str.substring(len - end, len - start);
    }
}

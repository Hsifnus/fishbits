package com.melonfishy.sleepycat;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.melonfishy.sleepycat.data.ScheduleDbContract;
import com.melonfishy.sleepycat.utils.ClockTime;
import com.melonfishy.sleepycat.utils.ScheduleUtils;

import org.w3c.dom.Text;

import static android.view.View.GONE;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mUpperHeader, mLowerHeader, mStartTime, mEndTime, mAlarmTime, mTitle, mDetails
            , mTimeDivider, mAlarmDate;
    private ImageButton mEditButton, mBackButton, mDeleteButton;
    private String mDatePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ScheduleUtils.initializeLogo(this);

        mEditButton = (ImageButton) findViewById(R.id.ib_edit_event);
        mBackButton = (ImageButton) findViewById(R.id.ib_cancel_event);
        mDeleteButton = (ImageButton) findViewById(R.id.ib_delete_event);

        mUpperHeader = (TextView) findViewById(R.id.tv_upper_header_detail);
        mLowerHeader = (TextView) findViewById(R.id.tv_lower_header_detail);
        mStartTime = (TextView) findViewById(R.id.tv_detail_start_time);
        mTimeDivider = (TextView) findViewById(R.id.tv_detail_time_divider);
        mEndTime = (TextView) findViewById(R.id.tv_detail_end_time);
        mAlarmTime = (TextView) findViewById(R.id.tv_detail_alarm_time);
        mAlarmDate = (TextView) findViewById(R.id.tv_detail_alarm_date);
        mTitle = (TextView) findViewById(R.id.tv_detail_title);
        mDetails = (TextView) findViewById(R.id.tv_detail_description);

        mDatePath = fetchString(ScheduleUtils.SCHEDULE_DATE_PATH);
        mUpperHeader.setText(fetchString(ScheduleUtils.SCHEDULE_UPPER_HEADER));
        mLowerHeader.setText(fetchString(ScheduleUtils.SCHEDULE_LOWER_HEADER));
        String startTimeString = fetchString(ScheduleUtils.SCHEDULE_START_TIME);
        mStartTime.setText(ScheduleUtils
                .displayQueryString(startTimeString));
        String endTimeString = fetchString(ScheduleUtils.SCHEDULE_END_TIME);
        if (endTimeString.equals("none")) {
            mEndTime.setVisibility(View.GONE);
            mTimeDivider.setVisibility(View.GONE);
            mEndTime.setText(mStartTime.getText());
        } else {
            mEndTime.setVisibility(View.VISIBLE);
            mTimeDivider.setVisibility(View.VISIBLE);
            mEndTime.setText(ScheduleUtils.displayQueryString(endTimeString));
        }
        final String alarmOffset = fetchString(ScheduleUtils.SCHEDULE_ALARM_OFFSET);
        String alarmTime = new ClockTime(startTimeString)
                .subtract(new ClockTime(alarmOffset)).toString();
        mAlarmTime.setText(alarmTime);
        mTitle.setText(fetchString(ScheduleUtils.SCHEDULE_TITLE));
        mDetails.setText(fetchString(ScheduleUtils.SCHEDULE_DETAILS));
        String alarmDate = mUpperHeader.getText().toString() + ", "
                + mLowerHeader.getText().toString();
        mAlarmDate.setText(ScheduleUtils.getAlarmDate(alarmDate, startTimeString, alarmOffset));

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailActivity.this.finish();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, EditEventActivity.class);
                intent.putExtra(ScheduleUtils.SCHEDULE_UPPER_HEADER, mUpperHeader.getText());
                intent.putExtra(ScheduleUtils.SCHEDULE_LOWER_HEADER, mLowerHeader.getText());
                intent.putExtra(ScheduleUtils.SCHEDULE_START_TIME, mStartTime.getText());
                intent.putExtra(ScheduleUtils.SCHEDULE_END_TIME, mEndTime.getText());
                intent.putExtra(ScheduleUtils.SCHEDULE_TITLE, mTitle.getText());
                intent.putExtra(ScheduleUtils.SCHEDULE_DETAILS, mDetails.getText());
                intent.putExtra(ScheduleUtils.SCHEDULE_ALARM_OFFSET, alarmOffset);
                intent.putExtra(ScheduleUtils.SCHEDULE_DATE_PATH, mDatePath);
                intent.putExtra(ScheduleUtils.SCHEDULE_ID,
                        getIntent().getIntExtra(ScheduleUtils.SCHEDULE_ID, 0));
                startActivityForResult(intent, ScheduleUtils.REQUEST_EDIT_EVENT);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                builder.setMessage(R.string.delete_event_warning)
                        .setPositiveButton(R.string.delete_event_positive,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int id = getIntent()
                                                .getIntExtra(ScheduleUtils.SCHEDULE_ID, 0);
                                        dialog.dismiss();
                                        Uri contentUri = ScheduleDbContract.ScheduleEntry.CONTENT_URI
                                                .buildUpon().appendPath(mDatePath)
                                                .appendPath(String.valueOf(id))
                                                .build();
                                        getContentResolver().delete(contentUri, null, null);
                                        ScheduleUtils.cancelEvent(EventDetailActivity.this, id);
                                        setResult(ScheduleUtils.RESULT_EVENT_DELETED);
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.delete_event_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    private String fetchString(String key) {
        return getIntent().getStringExtra(key);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ScheduleUtils.RESULT_EVENT_UPDATED) {
            setResult(resultCode);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

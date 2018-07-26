package com.melonfishy.sleepycat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;

import com.melonfishy.sleepycat.utils.ScheduleUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ScheduleUtils.initializeLogo(this);
        mCalendar = (MaterialCalendarView) findViewById(R.id.calendar_view);
        OnDateSelectedListener listener = new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year = date.getYear();
                int month = date.getMonth() + 1;
                int day = date.getDay();
                String upperHeader = ScheduleUtils.formatMonthAndDay(month, day);
                String lowerHeader = String.valueOf(year);
                String datePath = ScheduleUtils.formatDatePath(year, month, day);
                Intent intent = new Intent(CalendarActivity.this, ScheduleActivity.class);
                intent.putExtra(ScheduleUtils.SCHEDULE_UPPER_HEADER, upperHeader);
                intent.putExtra(ScheduleUtils.SCHEDULE_LOWER_HEADER, lowerHeader);
                intent.putExtra(ScheduleUtils.SCHEDULE_DATE_PATH, datePath);
                startActivity(intent);
            }
        };
        mCalendar.setOnDateChangedListener(listener);
    }
}

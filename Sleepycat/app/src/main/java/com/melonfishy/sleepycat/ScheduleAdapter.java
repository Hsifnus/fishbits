package com.melonfishy.sleepycat;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.melonfishy.sleepycat.data.ScheduleDbContract;
import com.melonfishy.sleepycat.utils.ScheduleUtils;

import static android.view.View.GONE;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    final private ScheduleAdapterOnClickHandler mClickHandler;

    class ScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView startTimeView, dividerTimeView, endTimeView, eventDescriptionView, eventIdView;

        ScheduleViewHolder(View itemView) {
            super(itemView);

            startTimeView = (TextView) itemView.findViewById(R.id.tv_start_time);
            dividerTimeView = (TextView) itemView.findViewById(R.id.tv_time_divider);
            endTimeView = (TextView) itemView.findViewById(R.id.tv_end_time);
            eventDescriptionView = (TextView) itemView.findViewById(R.id.tv_event_desc);
            eventIdView = (TextView) itemView.findViewById(R.id.tv_event_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String startTime = mCursor.getString(ScheduleActivity.INDEX_WEATHER_START_TIME);
            String endTime = mCursor.getString(ScheduleActivity.INDEX_WEATHER_END_TIME);
            String title = mCursor.getString(ScheduleActivity.INDEX_WEATHER_TITLE);
            String description = mCursor.getString(ScheduleActivity.INDEX_WEATHER_DETAILS);
            String date = mCursor.getString(ScheduleActivity.INDEX_WEATHER_DATE);
            String alarmOffset = mCursor.getString(ScheduleActivity.INDEX_WEATHER_ALARM_OFFSET);
            int id = mCursor.getInt(ScheduleActivity.INDEX_WEATHER_ID);
            mClickHandler.onClick(date, startTime, endTime, alarmOffset, title, description, id);
        }
    }

    public interface ScheduleAdapterOnClickHandler {
        void onClick(String date, String startTime, String endTime, String alarmOffset,
                     String title, String detail, int id);
    }

    public ScheduleAdapter(Context context, ScheduleAdapterOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(ScheduleDbContract.ScheduleEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(ScheduleDbContract.ScheduleEntry.COLUMN_TITLE);
        int startTimeIndex = mCursor.getColumnIndex(ScheduleDbContract.ScheduleEntry.COLUMN_START_TIME);
        int endTimeIndex = mCursor.getColumnIndex(ScheduleDbContract.ScheduleEntry.COLUMN_END_TIME);

        mCursor.moveToPosition(position);

        int idTag = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);
        String startTime = ScheduleUtils.displayQueryString(mCursor.getString(startTimeIndex));

        if (mCursor.getString(endTimeIndex).equals("none")) {
            holder.endTimeView.setVisibility(GONE);
            holder.dividerTimeView.setVisibility(GONE);
        } else {
            holder.endTimeView.setVisibility(View.VISIBLE);
            holder.dividerTimeView.setVisibility(View.VISIBLE);
            String endTime = ScheduleUtils.displayQueryString(mCursor.getString(endTimeIndex));
            holder.endTimeView.setText(endTime);
        }

        holder.itemView.setTag(idTag);
        holder.eventIdView.setText(String.valueOf(idTag));
        holder.startTimeView.setText(startTime);
        holder.eventDescriptionView.setText(description);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}

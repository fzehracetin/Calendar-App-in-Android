package com.example.calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    public TextView eventNameTV, eventTimeTV;
    public CheckBox eventCheckbox;
    public RelativeLayout parentLayout;

    public EventAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTV = itemView.findViewById(R.id.eventName);
            eventTimeTV = itemView.findViewById(R.id.time);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            eventCheckbox = itemView.findViewById(R.id.eventCheck);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.calendar_list_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        final String name = mCursor.getString(mCursor.getColumnIndex(EventDB.Event.COLUMN_NAME));
        final String startDate = mCursor.getString(mCursor.getColumnIndex(EventDB.Event.COLUMN_START));
        final String endDate = mCursor.getString(mCursor.getColumnIndex(EventDB.Event.COLUMN_END));
        final int state = mCursor.getInt(mCursor.getColumnIndex(EventDB.Event.COLUMN_STATE));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date end = df.parse(endDate);
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();
            if (end != null && end.before(currentDate)) {
                eventNameTV.setTextColor(Color.parseColor("#EB9791"));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String date = startDate.split(" ")[1] + " - " + endDate.split(" ")[1];
        eventNameTV.setText(name);
        eventTimeTV.setText(date);

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editEventIntent = new Intent(view.getContext(), EditEventActivity.class);
                editEventIntent.putExtra("EDIT", "true");
                editEventIntent.putExtra("EVENT NAME", name);
                editEventIntent.putExtra("START", startDate);
                editEventIntent.putExtra("END", endDate);
                view.getContext().startActivity(editEventIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount(); // database'deki tüm elemanlar
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}

package com.example.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView eventNameTV, eventTimeTV;
    private RelativeLayout parentLayout;
    private SQLiteDatabase mDatabase;
    private int ID;
    private String type;
    private String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Friday",
            "Saturday"};
    private String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    public EventAdapter(Context context, Cursor cursor, String type) {
        mContext = context;
        mCursor = cursor;
        EventDBHelper dbHelper = new EventDBHelper(context);
        mDatabase = dbHelper.getWritableDatabase();
        this.type = type;
    }


    public class EventViewHolder extends RecyclerView.ViewHolder {

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTV = itemView.findViewById(R.id.eventName);
            eventTimeTV = itemView.findViewById(R.id.time);
            parentLayout = itemView.findViewById(R.id.parentLayout);
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
        ID = mCursor.getInt(mCursor.getColumnIndex(EventDB.Event.COLUMN_ID));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date end = df.parse(endDate);
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();
            if (end != null && end.getTime() - currentDate.getTime() < 0) {
                eventNameTV.setTextColor(Color.parseColor("#BAA4CE"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String date = "hey";
        Calendar c = Calendar.getInstance();
        eventNameTV.setText(name);
        if (type.equals("Time"))
            date = startDate.split(" ")[1] + " - " + endDate.split(" ")[1];
        else if (type.equals("Weekly")){
            try {
                Date start = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDate);
                c.setTime(start);
                date = days[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + months[c.get(Calendar.MONTH)]
                        + " " + c.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            String text = startDate.split(" ")[0];
            date = months[Integer.parseInt(text.split("-")[1]) - 1] + " " + text.split("-")[2];
        }
        eventTimeTV.setText(date);

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editEventIntent = new Intent(view.getContext(), EventDetailsActivity.class);
                editEventIntent.putExtra("EVENT NAME", name);
                editEventIntent.putExtra("START", startDate);
                editEventIntent.putExtra("END", endDate);
                view.getContext().startActivity(editEventIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
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

package com.example.calendar;
import android.app.Activity;
import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MyAlarmManager {

    private int eid, id;
    private String eventName, startDate, endDate, note;
    private SQLiteDatabase mDatabase;
    private Context context;


    public MyAlarmManager(int eid, Context context) {
        this.eid = eid;
        this.context = context;

        EventDBHelper dbHelper = new EventDBHelper(context);
        mDatabase = dbHelper.getWritableDatabase();
        findEvent();
    }

    private void findEvent() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_ID + "='" + eid + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);

        if (cursor.moveToFirst()) {
            do {
                eventName = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_NAME));
                startDate = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_START));
                endDate = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_END));
            } while (cursor.moveToNext());
        }
    }

    public void findDates() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.REMINDER_TABLE_NAME +
                " WHERE " + EventDB.Event.REMINDER_COLUMN_EID + "='" + eid + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(EventDB.Event.REMINDER_COLUMN_DATE));
                id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.REMINDER_COLUMN_ID));
                createAlarm(date);
            } while (cursor.moveToNext());
        }
    }

    public void createAlarm(String date) {
        Calendar c = strToCal(date);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Intent intent  = new Intent(context, AlertReceiver.class);
        intent.putExtra("name", eventName);
        intent.putExtra("eid", eid);
        intent.putExtra("start", startDate);
        intent.putExtra("end", endDate);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    public Calendar strToCal(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            Date startD = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            cal.setTime(startD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }


}

package com.example.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.calendar.EventDB.*;

public class EventDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "calendar.db";
    public static final int DATABASE_VERSION = 1;
    public String dbPath;

    public EventDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        Log.e("DB Path", dbPath);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + Event.TABLE_NAME +
                "(" + Event.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Event.COLUMN_NAME + " TEXT NOT NULL, " +
                Event.COLUMN_START + " TEXT NOT NULL, " +
                Event.COLUMN_END + " TEXT NOT NULL, " +
                Event.COLUMN_SERI + " INTEGER DEFAULT -1, " +
                Event.COLUMN_ALERT + " TEXT, " +
                Event.COLUMN_LOCATION + " TEXT, " +
                Event.COLUMN_INVITEES + " TEXT, " +
                Event.COLUMN_NOTE + " TEXT" + ");";

        final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + Event.REMINDER_TABLE_NAME +
                "(" + Event.REMINDER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Event.REMINDER_COLUMN_EID + " INTEGER NOT NULL, " +
                Event.REMINDER_COLUMN_DATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REMINDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Event.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Event.REMINDER_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

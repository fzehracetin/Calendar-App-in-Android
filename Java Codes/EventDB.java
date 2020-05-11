package com.example.calendar;

import android.provider.BaseColumns;

public class EventDB {

    private EventDB() {}

    public static final class Event implements BaseColumns {
        public static final String TABLE_NAME = "EVENTS";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_START = "START_DATE";
        public static final String COLUMN_END = "END_DATE";
        public static final String COLUMN_SERI = "SERI_NO";
        public static final String COLUMN_ALERT = "ALERT";
        public static final String COLUMN_LOCATION = "LOCATION";
        public static final String COLUMN_INVITEES = "INVITEES";
        public static final String COLUMN_NOTE = "NOTE";
        public static final String COLUMN_STATE = "STATE";
    }
}

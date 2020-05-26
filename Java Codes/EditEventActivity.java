package com.example.calendar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.calendar.SettingsActivity.MyPreferences;
import static com.example.calendar.SettingsActivity.ReminderAmount;
import static com.example.calendar.SettingsActivity.ReminderDate;

public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private SQLiteDatabase mDatabase;
    private EditText eventNameET, noteET;
    private TextView title;
    private ImageButton startDateButton, endDateButton, startTimeButton, endTimeButton;
    private Button saveButton, deleteButton, repeatButton, reminderButton, locationButton;
    private String currentDateString, start, end, eventName, repeatType, untilDate, repeatCount,
            note, location, link, repeatFrequency, durationType, startDB, endDB, realStart,
            seriText, amountSP;
    private Calendar c;
    public int ID = -1, SERI = -1, dateSP;
    public boolean sTimeSet, eTimeSet, isStart, reminded =false, saved=false, repeated=false,
            deleteAll = false, located=false;
    public boolean[] daysOfWeek = new boolean[7];
    private Bundle args;
    private ContentValues cv;
    private MyDate startDatem, endDatem, diffDatem;
    private ArrayList<Integer> minReminder, hourReminder, dayReminder;
    private ArrayList<String> dateReminder;
    SharedPreferences sharedPreferences;


    public class MyDate {
        public int year, month, day, hour, minute;
        public String dateString;
        private Calendar c;

        public MyDate(int year, int month, int day, int hour, int minute) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
        }

        public void setDateString() {
            Date date = getDate();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            this.dateString = df.format(date);
        }

        public String getDateString() {
            setDateString();
            return dateString;
        }

        private void setCalendar() {
            c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
        }

        public Date getDate() {
            setCalendar();
            return c.getTime();
        }

        public Calendar getCalendar() {
            setCalendar();
            return c;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        EventDBHelper dbHelper = new EventDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        amountSP = sharedPreferences.getString(ReminderAmount, "10");
        dateSP = sharedPreferences.getInt(ReminderDate, 0);

        eventNameET = findViewById(R.id.eventName);
        noteET = findViewById(R.id.noteET);
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        startTimeButton = findViewById(R.id.startTimeButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        reminderButton = findViewById(R.id.reminderButton);
        title = findViewById(R.id.title);
        repeatButton = findViewById(R.id.repeatButton);
        locationButton = findViewById(R.id.locationButton);

        if (getIntent().getStringExtra("EDIT") != null) {
            eventName = getIntent().getStringExtra("EVENT NAME");
            start = getIntent().getStringExtra("START");
            end = getIntent().getStringExtra("END");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date startDate = df.parse(start);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);

                startDatem = new MyDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Date endDate = df.parse(end);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                endDatem = new MyDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventNameET.setText(eventName);
            title.setText("EDIT EVENT");
            sTimeSet = true;
            eTimeSet = true;
            findRow();
            if (note != null) {
                noteET.setText(note);
            }
        }
        else {
            c = Calendar.getInstance();
            startDatem = new MyDate(getIntent().getIntExtra("YEAR", 1970),
                    getIntent().getIntExtra("MONTH", 0),
                    getIntent().getIntExtra("DAY", 1),
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE));
            endDatem = new MyDate(getIntent().getIntExtra("YEAR", 1970),
                    getIntent().getIntExtra("MONTH", 0),
                    getIntent().getIntExtra("DAY", 1),
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE));

            sTimeSet = false;
            eTimeSet = false;
        }

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = true;
                DialogFragment datePicker = new DatePickerFragment();
                args = new Bundle();
                args.putInt("DAY", startDatem.day);
                args.putInt("YEAR", startDatem.year);
                args.putInt("MONTH", startDatem.month);
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = true;
                DialogFragment timePicker = new TimePickerFragment();
                if (sTimeSet) {
                    args = new Bundle();
                    args.putInt("HOUR", startDatem.hour);
                    args.putInt("MINUTE", startDatem.minute);
                    timePicker.setArguments(args);
                }
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = false;
                DialogFragment datePicker = new DatePickerFragment();
                args = new Bundle();
                args.putInt("DAY", endDatem.day);
                args.putInt("YEAR", endDatem.year);
                args.putInt("MONTH", endDatem.month);
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = false;
                DialogFragment timePicker = new TimePickerFragment();
                if (eTimeSet) {
                    args = new Bundle();
                    args.putInt("HOUR", endDatem.hour);
                    args.putInt("MINUTE", endDatem.minute);
                    timePicker.setArguments(args);
                }
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeated = true;
                Intent repeatIntent = new Intent(EditEventActivity.this, RepeatActivity.class);
                if (getIntent().getStringExtra("EDIT") != null) {
                    repeatIntent.putExtra("ID", ID);
                    repeatIntent.putExtra("SERI", SERI);
                }
                repeatIntent.putExtra("DAY", startDatem.day);
                repeatIntent.putExtra("YEAR", startDatem.year);
                repeatIntent.putExtra("MONTH", startDatem.month);
                startActivityForResult(repeatIntent, 1);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateBoundValid()) {
                    Snackbar mySnackbar = Snackbar.make(v, "Date interval is not valid." +
                            " Reminder could not be created.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else {
                    reminded = true;
                    Intent reminderIntent = new Intent(EditEventActivity.this, ReminderActivity.class);
                    reminderIntent.putExtra("DAY", startDatem.day);
                    reminderIntent.putExtra("YEAR", startDatem.year);
                    reminderIntent.putExtra("MONTH", startDatem.month);
                    reminderIntent.putExtra("HOUR", startDatem.hour);
                    reminderIntent.putExtra("MINUTE", startDatem.minute);
                    reminderIntent.putExtra("ID", ID);
                    startActivityForResult(reminderIntent, 2);
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(EditEventActivity.this,
                        PlacePickerActivity.class);
                startActivityForResult(locationIntent, 3);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                eventName = eventNameET.getText().toString();
                start = startDatem.getDateString();
                end = endDatem.getDateString();
                note = noteET.getText().toString();

                Snackbar mySnackbar;
                if (!dateBoundValid()) {
                    mySnackbar = Snackbar.make(view, "Date interval is not valid." +
                            " Event could not be saved.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                } else if (eventNameET.getText().toString().trim().length() == 0) {
                    mySnackbar = Snackbar.make(view, "Event name can not be null.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else if (SERI != -1 && getIntent().getStringExtra("EDIT") != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
                    builder.setTitle("Update Event");
                    builder.setPositiveButton("Only this event", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            insertToCV(eventName, start, end, note);
                            cv.put(EventDB.Event.COLUMN_SERI, SERI);
                            updateRow(view, ID);
                            if (!reminded)
                                defaultReminderSettings();
                            addReminder(cv);
                            MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
                            manager.findDates();
                        }
                    });
                    if (!isFirst()) {
                        builder.setNegativeButton("This and future events", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                insertToCV(eventName, start, end, note);
                                cv.put(EventDB.Event.COLUMN_SERI, SERI);
                                updateRow(view, ID);
                                if (!reminded)
                                    defaultReminderSettings();
                                addReminder(cv);
                                MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
                                manager.findDates();
                                futureEvents(view, "Update");
                            }
                        });
                    }
                    builder.setNeutralButton("All events in series", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            allEvents(view, "Update");
                        }
                    });
                    builder.show();
                }
                else {
                    insertToCV(eventName, start, end, note);
                    if (repeatType != null && !repeatType.equals("Never"))
                        repeater(view);
                    else {
                        cv.put(EventDB.Event.COLUMN_SERI, -1);
                        if (getIntent().getStringExtra("EDIT") == null) { // new
                            insertToDB(view);
                            if (!reminded)
                                defaultReminderSettings();
                            addReminder(cv);
                            MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
                            manager.findDates();
                        }
                        else if (ID != -1) { // update
                            updateRow(view, ID);
                            if (!reminded)
                                defaultReminderSettings();
                            addReminder(cv);
                            MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
                            manager.findDates();
                        }
                    }
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (SERI != -1 && getIntent().getStringExtra("EDIT") != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
                    builder.setTitle("Delete Event");
                    builder.setPositiveButton("Only this event", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRow(view, ID);
                            deleteReminder(ID);
                        }
                    });
                    if (!isFirst()) {
                        builder.setNegativeButton("This and future events", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRow(view, ID);
                                deleteReminder(ID);
                                futureEvents(view, "Delete");
                            }
                        });
                    }
                    builder.setNeutralButton("All events in series", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            allEvents(view, "Delete");
                        }
                    });
                    builder.show();
                }
                else if (ID != -1) {
                    deleteRow(view, ID);
                    deleteReminder(ID);
                }
                else {
                    eventNameET.getText().clear();
                    noteET.getText().clear();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("Repeat Type")) {
                seriText = data.getStringExtra("Seri Type");
                repeatType = data.getStringExtra("Repeat Type");
                if (!repeatType.equals("Never")) {
                    repeatFrequency = data.getStringExtra("Repeat Frequency");
                    durationType = data.getStringExtra("Duration Type");
                    if (durationType.equals("Repetitions"))
                        repeatCount = data.getStringExtra("Repeat Count");
                    else if (durationType.equals("Until"))
                        untilDate = data.getStringExtra("Until Date");
                    if (repeatType.equals("Weekly")) {
                        // starts from sunday
                        daysOfWeek = data.getBooleanArrayExtra("Days of Week");
                    }
                }
                if (data.hasExtra("Deleted")) {
                    SERI = -1;
                }
            }
        }
        else if (resultCode == RESULT_OK && requestCode == 2) {
            deleteAll = data.getBooleanExtra("deleteAll", false);
            if (deleteAll) {
                mDatabase.delete(EventDB.Event.REMINDER_TABLE_NAME,
                        EventDB.Event.REMINDER_COLUMN_EID + "=" + ID,
                        null);
            }
            else {
                dateReminder = data.getStringArrayListExtra("dates");
                hourReminder = data.getIntegerArrayListExtra("hours");
                minReminder = data.getIntegerArrayListExtra("minutes");
                dayReminder = data.getIntegerArrayListExtra("days");
            }

            if (ID == -1)
                ID = getMaxID() + 1;
        }
        else if  (resultCode == RESULT_OK && requestCode == 3) {
            if(data.hasExtra("address")) {
                location = data.getStringExtra("address");
                link = data.getStringExtra("link");
                located = true;
            }
        }
    }

    public void defaultReminderSettings() {
        minReminder = new ArrayList<Integer>();
        hourReminder = new ArrayList<Integer>();
        dayReminder = new ArrayList<Integer>();
        dateReminder = new ArrayList<String>();
        int amount = Integer.parseInt(amountSP);
        ID = getMaxID();

        if (dateSP == 0) { // minute
            minReminder.add(amount);
        }
        else if (dateSP == 1) { // hour
            hourReminder.add(amount);
        }
        else if (dateSP == 2) { // day
            dayReminder.add(amount);
        }
    }

    public void addReminder(ContentValues cv) {

        String eventName = cv.getAsString(EventDB.Event.COLUMN_NAME);
        String start = cv.getAsString(EventDB.Event.COLUMN_START);
        String end = cv.getAsString(EventDB.Event.COLUMN_END);
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_START + "='" + start + "' AND " +
                EventDB.Event.COLUMN_END + "='" + end + "' AND " + EventDB.Event.COLUMN_NAME + "='"
                + eventName + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
            } while (cursor.moveToNext()); }

        if (id == ID) {
            realStart = start;
        }
        reminderListTraverse(id, start);
    }

    public void reminderListTraverse(int id, String start) {

        for (int minute: minReminder) {
            Calendar cal = strToCal(start);
            cal.add(Calendar.MINUTE, -minute);
            insertToReminderTable(cal, id);
        }
        for (int hour: hourReminder) {
            Calendar cal = strToCal(start);
            cal.add(Calendar.HOUR_OF_DAY, -hour);
            insertToReminderTable(cal, id);
        }
        for (int day: dayReminder) {
            Calendar cal = strToCal(start);
            cal.add(Calendar.DAY_OF_MONTH, -day);
            insertToReminderTable(cal, id);
        }
        for (String date: dateReminder) {
            Calendar cal = strToCal(date);
            if (start.equals(realStart))
                insertToReminderTable(cal, id);
            else {
               Calendar real = strToCal(realStart);
               MyDate diff = new MyDate(-(real.get(Calendar.YEAR) - cal.get(Calendar.YEAR)),
                       -(real.get(Calendar.MONTH) - cal.get(Calendar.MONTH)),
                       -(real.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH)),
                       -(real.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY)),
                       -(real.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE)));
               Calendar startCal = strToCal(start);
               startCal = addBuffer(diff, startCal);
               insertToReminderTable(startCal, id);
            }
        }
    }

    public void insertToReminderTable(Calendar c, int id) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(c.getTime());

        ContentValues cv2 = new ContentValues();
        cv2.put(EventDB.Event.REMINDER_COLUMN_EID, id);
        cv2.put(EventDB.Event.REMINDER_COLUMN_DATE, dateStr);
        mDatabase.insert(EventDB.Event.REMINDER_TABLE_NAME, null, cv2);
    }

    public Calendar strToCal(String date) {
        Calendar startCal = Calendar.getInstance();
        try {
            Date startD = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            startCal.setTime(startD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startCal;
    }


    private void deleteReminder(int id) {
        mDatabase.delete(EventDB.Event.REMINDER_TABLE_NAME,
                EventDB.Event.REMINDER_COLUMN_EID + "=" + id,
                null);
    }

    public void repeater(View view) {
        SERI = findMaxSeri() + 1;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        diffDatem = new MyDate(endDatem.year - startDatem.year,endDatem.month - startDatem.month,
                endDatem.day - startDatem.day, endDatem.hour - startDatem.hour,
                endDatem.minute - startDatem.minute);
        Date until = startDatem.getDate();
        Date today = new Date();
        if (durationType.equals("Until")) {
            try {
                until = df.parse(untilDate);
                today = startDatem.getDate();
                repeatCount = "0";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            Calendar c = Calendar.getInstance();
            c.setTime(startDatem.getDate());
            c.add(Calendar.DAY_OF_MONTH, 1);
            today = c.getTime();
        }

        if (durationType.equals("Forever"))
            repeatCount = "100";
        int i = 0;

        while( i < Integer.parseInt(repeatCount) || today.before(until)) { // repetitions
            int every = Integer.parseInt(repeatFrequency);
            if (repeatType.equals("Daily")) {
                MyDate bufferDate = new MyDate(0, 0, i*every, 0, 0);
                today = repeatEventCreator(bufferDate, until, view);
            }
            else if (repeatType.equals("Weekly")) {
                int dayOfWeek = startDatem.getCalendar().get(Calendar.DAY_OF_WEEK);
                if (i == 0) {
                    for (int k = dayOfWeek; k < 8; k++) {
                        if (daysOfWeek[k - 1] && !(dayOfWeek == 1 && k == 1)) {
                            MyDate bufferDate = new MyDate(0, 0, k - dayOfWeek,
                                    0, 0);
                            today = repeatEventCreator(bufferDate, until, view);
                        }
                        if (k == 7 && daysOfWeek[0]) {  // bu hala neden olmadı!!!!
                            int day = 7 - dayOfWeek + 1;
                            if (dayOfWeek == 1)
                                day = 0;
                            MyDate bufferDate = new MyDate(0, 0, day,
                                    0, 0);
                            today = repeatEventCreator(bufferDate, until, view);
                        }
                    }
                }
                else {
                    for (int k = 1; k < 7; k++) {
                        int sunday = 7 - dayOfWeek + 1;
                        if (daysOfWeek[k] && !((i == Integer.parseInt(repeatCount) - 1) && (dayOfWeek == 1))) {
                            MyDate bufferDate = new MyDate(0, 0,
                                    (sunday + k) + 7 * (i - 1), 0, 0);
                            today = repeatEventCreator(bufferDate, until, view);
                        }
                        if (k == 6 && daysOfWeek[0]) {
                            MyDate bufferDate;
                            if (dayOfWeek == 1)
                                bufferDate = new MyDate(0, 0,
                                        (sunday) + 7 * (i - 1), 0, 0);
                            else
                                bufferDate = new MyDate(0, 0,
                                        (sunday) + 7 * i, 0, 0);
                            today = repeatEventCreator(bufferDate, until, view);
                        }
                    }
                }
            }
            else if (repeatType.equals("Monthly")) {
                MyDate bufferDate = new MyDate(0, i * every, 0, 0, 0);
                today = repeatEventCreator(bufferDate, until, view);
            }
            else if (repeatType.equals("Yearly")) {
                MyDate bufferDate = new MyDate(i * every, 0, 0, 0, 0);
                today = repeatEventCreator(bufferDate, until, view);
            }
            if (!durationType.equals("Until"))
                today = until;
            i++;
        }

        MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
        manager.findDates();

    }

    public Date repeatEventCreator(MyDate buffer, Date until, View view) {
        Calendar startT = startDatem.getCalendar();
        Calendar endT = endDatem.getCalendar();
        Calendar start = addBuffer(buffer, startT);
        Calendar end = addBuffer(buffer, endT);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startStr = df.format(start.getTime());
        String endStr = df.format(end.getTime());

        if ((durationType.equals("Until") && start.getTime().before(until) &&
                end.getTime().before(until)) || !durationType.equals("Until")) {
            cv = new ContentValues();
            cv.put(EventDB.Event.COLUMN_NAME, eventName);
            cv.put(EventDB.Event.COLUMN_START, startStr);
            cv.put(EventDB.Event.COLUMN_END, endStr);
            cv.put(EventDB.Event.COLUMN_NOTE, noteET.getText().toString());
            cv.put(EventDB.Event.COLUMN_SERI, SERI);
            cv.put(EventDB.Event.COLUMN_SERI_TYPE, seriText);
            if (located) {
                cv.put(EventDB.Event.COLUMN_LOCATION, location);
                cv.put(EventDB.Event.COLUMN_LOCATION_LINK, link);
            }
            insertToDB(view);
            if (!reminded)
                defaultReminderSettings();
            addReminder(cv);

        }
        return end.getTime();
    }

    public Calendar addBuffer (MyDate buffer, Calendar c) {
        c.add(Calendar.DAY_OF_MONTH, buffer.day);
        c.add(Calendar.MONTH, buffer.month);
        c.add(Calendar.YEAR, buffer.year);
        c.add(Calendar.HOUR_OF_DAY, buffer.hour);
        c.add(Calendar.MINUTE, buffer.minute);
        return c;
    }


    public boolean isFirst() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_SERI + "='" + SERI + "' ORDER BY datetime(" +
                EventDB.Event.COLUMN_START + ") ASC;";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        int id = 0;
        if (cursor.moveToFirst())
            id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));

        return id == ID;
    }

    public void allEvents(View view, String type) {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_SERI + "='" + SERI + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
                if (type.equals("Delete")) {
                    deleteRow(view, id);
                    deleteReminder(id);
                }
                else if (type.equals("Update")) {
                    String date = findStartDate(id);
                    String newStartDate = date.split(" ")[0] + " " + start.split(" ")[1];
                    String newEndDate = date.split(" ")[0] + " " + end.split(" ")[1];
                    insertToCV(eventName, newStartDate, newEndDate, note);
                    cv.put(EventDB.Event.COLUMN_SERI, SERI);
                    updateRow(view, id);
                    if (reminded && deleteAll) {
                        mDatabase.delete(EventDB.Event.REMINDER_TABLE_NAME,
                                EventDB.Event.REMINDER_COLUMN_EID + "=" + ID,
                                null);
                    }
                    if (!reminded)
                        defaultReminderSettings();
                    addReminder(cv);

                }
            } while (cursor.moveToNext());
        }

        MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
        manager.findDates();

    }

    public void futureEvents(View view, String type) {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_SERI + "='" + SERI + "' AND " +
                EventDB.Event.COLUMN_START + " > '" + startDB + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
                if (type.equals("Delete")) {
                    deleteRow(view, id);
                    deleteReminder(id);
                }
                else if (type.equals("Update")) {
                    String date = findStartDate(id);
                    String newStartDate = date.split(" ")[0] + " " + start.split(" ")[1];
                    String newEndDate = date.split(" ")[0] + " " + end.split(" ")[1];
                    insertToCV(eventName, newStartDate, newEndDate, note);
                    cv.put(EventDB.Event.COLUMN_SERI, SERI);
                    updateRow(view, id);
                    if (reminded && deleteAll) {
                        mDatabase.delete(EventDB.Event.REMINDER_TABLE_NAME,
                                EventDB.Event.REMINDER_COLUMN_EID + "=" + ID,
                                null);
                    }
                    if (!reminded)
                        defaultReminderSettings();
                    addReminder(cv);
                }
            } while (cursor.moveToNext());
        }
        MyAlarmManager manager = new MyAlarmManager(ID, getApplicationContext());
        manager.findDates();
    }

    public String findStartDate(int id) {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_ID + "='" + id + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        String start = null;
        if (cursor.moveToFirst()) {
            do {
                start = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_START));
            } while (cursor.moveToNext());
        }
        return start;
    }

    public void findRow() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_START + "='" + start + "' AND " +
                EventDB.Event.COLUMN_END + "='" + end + "' AND " + EventDB.Event.COLUMN_NAME + "='"
                + eventName + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ID = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
                note = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_NOTE));
                SERI = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_SERI));
                startDB = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_START));
                endDB = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_END));
            } while (cursor.moveToNext());
        }
    }

    public int findMaxSeri() {
        String SQLQuery = EventDB.Event.COLUMN_SERI +  "=(SELECT MAX(" + EventDB.Event.COLUMN_SERI +
                ") FROM " + EventDB.Event.TABLE_NAME + ")";
        Cursor cursor = mDatabase.query(EventDB.Event.TABLE_NAME, null, SQLQuery,
                null, null, null, null);
        int seri;
        if (cursor.moveToFirst()) {
            do {
                seri = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_SERI));
            } while (cursor.moveToNext());
            return seri;
        }
        return -1;
    }

    public int getMaxID() {
        String SQLQuery = EventDB.Event.COLUMN_ID +  "=(SELECT MAX(" + EventDB.Event.COLUMN_ID +
                ") FROM " + EventDB.Event.TABLE_NAME + ")";
        Cursor cursor = mDatabase.query(EventDB.Event.TABLE_NAME, null, SQLQuery,
                null, null, null, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
            } while (cursor.moveToNext());
        }
        return id;
    }

    public void deleteRow(View view, int id) {
        mDatabase.delete(EventDB.Event.TABLE_NAME, EventDB.Event.COLUMN_ID + "=" + id,
                null);

        Snackbar mySnackbar = Snackbar.make(view, "Event deleted.", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void insertToCV(String eventName, String start, String end, String note) {
        cv = new ContentValues();
        cv.put(EventDB.Event.COLUMN_NAME, eventName);
        cv.put(EventDB.Event.COLUMN_START, start);
        cv.put(EventDB.Event.COLUMN_END, end);
        cv.put(EventDB.Event.COLUMN_NOTE, note);
        if (located) {
            cv.put(EventDB.Event.COLUMN_LOCATION, location);
            cv.put(EventDB.Event.COLUMN_LOCATION_LINK, link);
        }
    }

    public void updateRow(View view, int id) {
        mDatabase.update(EventDB.Event.TABLE_NAME, cv, EventDB.Event.COLUMN_ID + "=" + id,
                null);
        Snackbar mySnackbar = Snackbar.make(view, "Event updated.", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
        saved = true;
        eventNameET.getText().clear();
    }

    public void insertToDB(View view) {
        mDatabase.insert(EventDB.Event.TABLE_NAME, null, cv);
        Snackbar mySnackbar = Snackbar.make(view, "Event created.", Snackbar.LENGTH_SHORT);
        mySnackbar.show();

        eventNameET.getText().clear();
        saved = true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        if (isStart) {
            startDatem.day = dayOfMonth;
            startDatem.month = month;
            startDatem.year = year;
        } else {
            endDatem.day = dayOfMonth;
            endDatem.month = month;
            endDatem.year = year;
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (isStart) {
            eTimeSet = true;
            startDatem.hour = hourOfDay;
            startDatem.minute = minute;
        } else {
            sTimeSet = true;
            endDatem.hour = hourOfDay;
            endDatem.minute = minute;
        }
    }

    public boolean dateBoundValid() {
        if (startDatem.year > endDatem.year)
            return false;
        else if (startDatem.year < endDatem.year)
            return true;
        else {
            if (startDatem.month > endDatem.month)
                return false;
            else if (startDatem.month < endDatem.month)
                return true;
            else {
                if (startDatem.day > endDatem.day)
                    return false;
                else if (startDatem.day < endDatem.day)
                    return true;
                else {
                    if (startDatem.hour > endDatem.hour)
                        return false;
                    else if (startDatem.hour < endDatem.hour)
                        return true;
                    else {
                        if (startDatem.minute > endDatem.minute)
                            return false;
                        else if (startDatem.minute == endDatem.minute)
                            return false;
                        else if (startDatem.minute < endDatem.minute)
                            return true;
                    }
                }
            }
        }
        return false;
    }

}

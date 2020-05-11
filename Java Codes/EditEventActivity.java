package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    private SQLiteDatabase mDatabase;
    private EditText eventNameET;
    private TextView title;
    private ImageButton startDateButton, endDateButton, startTimeButton, endTimeButton;
    private Button saveButton, deleteButton, repeatButton;
    private String currentDateString, start, end, eventName;
    private Calendar c;
    public int SDAY, SYEAR, SMONTH, EDAY, EYEAR, EMONTH, SHOUR, SMINUTE, EHOUR, EMINUTE, ID = -1;
    public boolean sTimeSet, eTimeSet, isStart;
    private Bundle args;
    ContentValues cv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        EventDBHelper dbHelper = new EventDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        eventNameET = findViewById(R.id.eventName);
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        startTimeButton = findViewById(R.id.startTimeButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        title = findViewById(R.id.title);
        repeatButton = findViewById(R.id.repeatButton);

        if (getIntent().getStringExtra("EDIT") != null) {
            start = getIntent().getStringExtra("START");
            end = getIntent().getStringExtra("END");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date startDate = df.parse(start);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                SDAY = calendar.get(Calendar.DAY_OF_MONTH);
                SMONTH = calendar.get(Calendar.MONTH);
                SYEAR = calendar.get(Calendar.YEAR);
                SMINUTE = calendar.get(Calendar.MINUTE);
                SHOUR = calendar.get(Calendar.HOUR_OF_DAY);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Date endDate = df.parse(end);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                EDAY = calendar.get(Calendar.DAY_OF_MONTH);
                EYEAR = calendar.get(Calendar.YEAR);
                EMONTH = calendar.get(Calendar.YEAR);
                EMINUTE = calendar.get(Calendar.MINUTE);
                EHOUR = calendar.get(Calendar.HOUR_OF_DAY);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventName = getIntent().getStringExtra("EVENT NAME");
            eventNameET.setText(eventName);
            title.setText("EDIT EVENT");
            sTimeSet = true;
            eTimeSet = true;
            ID = findID();
            deleteButton.setVisibility(View.VISIBLE);

        }
        else {
            SDAY = getIntent().getIntExtra("DAY", 1);
            SYEAR = getIntent().getIntExtra("YEAR", 1970);
            SMONTH = getIntent().getIntExtra("MONTH", 0);
            EDAY = getIntent().getIntExtra("DAY", 1);
            EYEAR = getIntent().getIntExtra("YEAR", 1970);
            EMONTH = getIntent().getIntExtra("MONTH", 0);

            c = Calendar.getInstance();
            EHOUR = c.get(Calendar.HOUR_OF_DAY);
            SHOUR = c.get(Calendar.HOUR_OF_DAY);
            EMINUTE = c.get(Calendar.MINUTE);
            SMINUTE= c.get(Calendar.MINUTE);
            sTimeSet = false;
            eTimeSet = false;
            deleteButton.setVisibility(View.INVISIBLE);
        }

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = true;
                DialogFragment datePicker = new DatePickerFragment();
                args = new Bundle();
                args.putInt("DAY", SDAY);
                args.putInt("YEAR", SYEAR);
                args.putInt("MONTH", SMONTH);
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
                    args.putInt("HOUR", SHOUR);
                    args.putInt("MINUTE", SMINUTE);
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
                args.putInt("DAY", EDAY);
                args.putInt("YEAR", EYEAR);
                args.putInt("MONTH", EMONTH);
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
                    args.putInt("HOUR", EHOUR);
                    args.putInt("MINUTE", EMINUTE);
                    timePicker.setArguments(args);
                }
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent repeatIntent = new Intent(EditEventActivity.this, RepeatActivity.class);
                repeatIntent.putExtra("DAY", SDAY);
                repeatIntent.putExtra("YEAR", SYEAR);
                repeatIntent.putExtra("MONTH", SMONTH);
                startActivityForResult(repeatIntent, 1);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dateBoundValid()) {
                    Toast.makeText(EditEventActivity.this, "Date interval is not valid." +
                            " Event could not be saved.", Toast.LENGTH_LONG).show();
                }
                else if (eventNameET.getText().toString().trim().length() == 0)
                    Toast.makeText(EditEventActivity.this, "Event name can not be null.",
                            Toast.LENGTH_LONG).show();
                else {
                    eventName = eventNameET.getText().toString();
                    c = Calendar.getInstance();
                    c.set(Calendar.YEAR, SYEAR);
                    c.set(Calendar.MONTH, SMONTH);
                    c.set(Calendar.DAY_OF_MONTH, SDAY);
                    c.set(Calendar.HOUR_OF_DAY, SHOUR);
                    c.set(Calendar.MINUTE, SMINUTE);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    start = df.format(c.getTime());

                    c = Calendar.getInstance();
                    c.set(Calendar.YEAR, EYEAR);
                    c.set(Calendar.MONTH, EMONTH);
                    c.set(Calendar.DAY_OF_MONTH, EDAY);
                    c.set(Calendar.HOUR_OF_DAY, EHOUR);
                    c.set(Calendar.MINUTE, EMINUTE);
                    end = df.format(c.getTime());

                    cv = new ContentValues();
                    cv.put(EventDB.Event.COLUMN_NAME, eventName);
                    cv.put(EventDB.Event.COLUMN_START, start);
                    cv.put(EventDB.Event.COLUMN_END, end);
                    cv.put(EventDB.Event.COLUMN_SERI, -1);

                    if (getIntent().getStringExtra("EDIT") == null) // new
                        insertToDB();
                    else if (ID != -1){ // update
                        updateRow();
                    }

                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID != -1)
                    deleteRow();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("Repeat Type")) {
                Toast.makeText(this, data.getExtras().getString("Repeat Type"),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int findID() {
        String SQLQuery = "SELECT " + EventDB.Event.COLUMN_ID + " FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_START + "='" + start + "' AND " +
                EventDB.Event.COLUMN_END + "='" + end + "' AND " + EventDB.Event.COLUMN_NAME + "='"
                + eventName + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        int id;
        if (cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
            } while(cursor.moveToNext());
            return id;
        }
        return -1;
    }

    public void deleteRow() {
        mDatabase.delete(EventDB.Event.TABLE_NAME,EventDB.Event.COLUMN_ID + "=" + ID,
                null);
        Toast.makeText(EditEventActivity.this, "Event deleted.",
                Toast.LENGTH_LONG).show();
        eventNameET.getText().clear();

    }

    public void updateRow() {
        mDatabase.update(EventDB.Event.TABLE_NAME, cv, EventDB.Event.COLUMN_ID + "=" + ID,
                null);
        Toast.makeText(EditEventActivity.this, "Event updated.",
                Toast.LENGTH_LONG).show();
        eventNameET.getText().clear();

    }
    public void insertToDB() {

        mDatabase.insert(EventDB.Event.TABLE_NAME, null, cv);
        Toast.makeText(EditEventActivity.this, "Event created.",
                Toast.LENGTH_LONG).show();

        eventNameET.getText().clear();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        if (isStart) {
            SDAY = dayOfMonth;
            SMONTH = month;
            SYEAR = year;
        }
        else {
            EDAY = dayOfMonth;
            EMONTH = month;
            EYEAR = year;
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        if (isStart) {
            eTimeSet = true;
            SHOUR = hourOfDay;
            SMINUTE = minute;
        }
        else {
            sTimeSet = true;
            EHOUR = hourOfDay;
            EMINUTE = minute;
        }
    }

    public boolean dateBoundValid() {
        if (SYEAR > EYEAR)
            return false;
        else if (SYEAR < EYEAR)
            return true;
        else {
            if (SMONTH > EMONTH)
                return false;
            else if (SMONTH < EMONTH)
                return true;
            else {
                if (SDAY > EDAY)
                    return false;
                else if (SDAY < EDAY)
                    return true;
                else {
                    if (SHOUR > EHOUR)
                        return false;
                    else if (SHOUR < EHOUR)
                        return true;
                    else {
                        if (SMINUTE > EMINUTE)
                            return false;
                        else if (SMINUTE == EMINUTE)
                            return false;
                        else if(SMINUTE < EMINUTE)
                            return true;
                    }
                }
            }
        }
        return false;
    }

}

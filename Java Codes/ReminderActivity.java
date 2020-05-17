package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    private SQLiteDatabase mDatabase;
    private Spinner dateSpinner;
    private ImageButton calButton, timeButton;
    private EditText amountET;
    private Button addButton, deleteButton;
    private RadioGroup reminderRG;
    private RadioButton reminderRB;
    private Bundle args;
    private int pos = 0, DAY, YEAR, MONTH, HOUR, MINUTE, ID;
    private Calendar c, startCal;
    private Date startDate;
    private ArrayList<Integer> minutes, hours, days;
    private ArrayList<String> dates;
    private boolean deleteAll = false;
    Intent datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        EventDBHelper dbHelper = new EventDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        DAY = getIntent().getIntExtra("DAY", 1);
        YEAR = getIntent().getIntExtra("YEAR", 2020);
        MONTH = getIntent().getIntExtra("MONTH", 0);
        HOUR = getIntent().getIntExtra("HOUR", 0);
        MINUTE = getIntent().getIntExtra("MINUTE", 0);
        ID = getIntent().getIntExtra("ID", 0);

        dateSpinner = findViewById(R.id.spinner);
        calButton = findViewById(R.id.calendarButton);
        timeButton = findViewById(R.id.timeButton);
        amountET = findViewById(R.id.amountET);
        addButton = findViewById(R.id.addButton);
        reminderRG = findViewById(R.id.reminderRG);
        deleteButton = findViewById(R.id.deleteButton);

        c = Calendar.getInstance();
        startCal = Calendar.getInstance();
        startCal.set(Calendar.DAY_OF_MONTH, DAY);
        startCal.set(Calendar.HOUR, HOUR);
        startCal.set(Calendar.MONTH, MONTH);
        startCal.set(Calendar.YEAR, YEAR);
        startCal.set(Calendar.MINUTE, MINUTE);

        startDate = startCal.getTime();

        dates = new ArrayList<String>();
        minutes = new ArrayList<Integer>();
        hours = new ArrayList<Integer>();
        days = new ArrayList<Integer>();
        datas = new Intent();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ReminderActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.date_array));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerAdapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = dateSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                args = new Bundle();
                args.putInt("DAY", DAY);
                args.putInt("YEAR", YEAR);
                args.putInt("MONTH", MONTH);
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                args = new Bundle();
                args.putInt("HOUR", HOUR);
                args.putInt("MINUTE", MINUTE);
                timePicker.setArguments(args);
                timePicker.show(getSupportFragmentManager(), "time picker");

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderRB.getText().toString().equals("Before")) {
                    Calendar start = createCalendar(YEAR, MONTH, DAY, HOUR, MINUTE);
                    int amount = Integer.parseInt(amountET.getText().toString());
                    if (pos == 0) // minute
                        minutes.add(amount);
                        //start.add(Calendar.MINUTE, -amount);
                    else if (pos == 1)  // hour
                        hours.add(amount);
                        //start.add(Calendar.HOUR, -amount);
                    else if (pos == 2) // day
                        days.add(amount);
                        //start.add(Calendar.DAY_OF_MONTH, -amount);
                    //insertToDB(start, v);
                    Snackbar mySnackbar = Snackbar.make(v, "Reminder created.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                    cleanView();
                }
                else if (reminderRB.getText().toString().equals("Custom")) {
                    if(c.getTime().after(startDate)) {
                        Snackbar mySnackbar = Snackbar.make(v, "Reminder can not be after the " +
                                "event.", Snackbar.LENGTH_SHORT);
                        mySnackbar.show();
                    }
                    else {
                        //insertToDB(c, v);
                        dates.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(c.getTime()));
                        cleanView();
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mDatabase.delete(EventDB.Event.REMINDER_TABLE_NAME,
                        EventDB.Event.REMINDER_COLUMN_EID + "=" + ID,
                        null);*/
                deleteAll = true;
                dates = new ArrayList<String>();
                minutes = new ArrayList<Integer>();
                hours = new ArrayList<Integer>();
                days = new ArrayList<Integer>();
                Snackbar mySnackbar = Snackbar.make(v, "All reminders deleted.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        datas.putStringArrayListExtra("dates", dates);
        datas.putIntegerArrayListExtra("minutes", minutes);
        datas.putIntegerArrayListExtra("hours", hours);
        datas.putIntegerArrayListExtra("days", days);
        datas.putExtra("deleteAll", deleteAll);

        setResult(RESULT_OK, datas);
        finish();

    }

    public void cleanView() {
        amountET.setVisibility(View.INVISIBLE);
        dateSpinner.setVisibility(View.INVISIBLE);
        calButton.setVisibility(View.INVISIBLE);
        timeButton.setVisibility(View.INVISIBLE);
    }

    public void insertToDB(Calendar c, View view) {
        ContentValues cv = new ContentValues();
        Date date = c.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = df.format(date);
        cv.put(EventDB.Event.REMINDER_COLUMN_EID, ID);
        cv.put(EventDB.Event.REMINDER_COLUMN_DATE, dateStr);
        mDatabase.insert(EventDB.Event.REMINDER_TABLE_NAME, null, cv);
        Snackbar mySnackbar = Snackbar.make(view, "Reminder created.", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void reminderCheck(View view) {
        int radioId = reminderRG.getCheckedRadioButtonId();
        reminderRB = findViewById(radioId);
        String name = reminderRB.getText().toString();

        if (reminderRB.getText().toString().equals("Before")) {
            amountET.setVisibility(View.VISIBLE);
            dateSpinner.setVisibility(View.VISIBLE);
            calButton.setVisibility(View.INVISIBLE);
            timeButton.setVisibility(View.INVISIBLE);
        }
        else if (reminderRB.getText().toString().equals("Custom")) {
            amountET.setVisibility(View.INVISIBLE);
            dateSpinner.setVisibility(View.INVISIBLE);
            calButton.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.VISIBLE);
        }
    }

    public Calendar createCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        return c;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
    }

}

package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;
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
    private Button addButton, deleteButton, clearButton1, clearButton2, clearButton3, clearButton4,
            clearButton5, clearButton6;
    private TextView reminder1, reminder2, reminder3, reminder4, reminder5, reminder6;
    private RadioGroup reminderRG;
    private RadioButton reminderRB;
    private Bundle args;
    private int pos = 0, DAY, YEAR, MONTH, HOUR, MINUTE, ID, index = 0, gonnaDelete = 0;
    private Calendar c, startCal;
    private Date startDate;
    private ArrayList<Integer> minutes, hours, days;
    private ArrayList<String> dates;
    private ArrayList<Button> clearButtons = new ArrayList<Button>();
    private ArrayList<TextView> reminders = new ArrayList<TextView>();
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
        ID = getIntent().getIntExtra("ID", -1);

        dateSpinner = findViewById(R.id.timeSpinner);
        calButton = findViewById(R.id.calendarButton);
        timeButton = findViewById(R.id.timeButton);
        amountET = findViewById(R.id.amountET);
        addButton = findViewById(R.id.addButton);
        reminderRG = findViewById(R.id.reminderRG);
        deleteButton = findViewById(R.id.deleteButton);

        clearButton1 = findViewById(R.id.clearButton1);
        clearButton2 = findViewById(R.id.clearButton2);
        clearButton3 = findViewById(R.id.clearButton3);
        clearButton4 = findViewById(R.id.clearButton4);
        clearButton5 = findViewById(R.id.clearButton5);
        clearButton6 = findViewById(R.id.clearButton6);

        reminder1 = findViewById(R.id.reminder1);
        reminder2 = findViewById(R.id.reminder2);
        reminder3 = findViewById(R.id.reminder3);
        reminder4 = findViewById(R.id.reminder4);
        reminder5 = findViewById(R.id.reminder5);
        reminder6 = findViewById(R.id.reminder6);

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

        initializeLists();
        makeInvisible();
        findReminders();

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
                else if (pos == 1)  // hour
                    hours.add(amount);
                else if (pos == 2) // day
                    days.add(amount);
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
                    dates.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(c.getTime()));
                    cleanView();
                }
            }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll = true;
                dates = new ArrayList<String>();
                minutes = new ArrayList<Integer>();
                hours = new ArrayList<Integer>();
                days = new ArrayList<Integer>();
                Snackbar mySnackbar = Snackbar.make(v, "All reminders deleted.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        clearButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonnaDelete = 0;
                deleteReminder();
            }
        });

        clearButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonnaDelete = 1;
                deleteReminder();
            }
        });

        clearButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonnaDelete = 2;
                deleteReminder();
            }
        });

        clearButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonnaDelete = 3;
                deleteReminder();
            }
        });

        clearButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonnaDelete = 4;
                deleteReminder();
            }
        });
        clearButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonnaDelete = 5;
                deleteReminder();
            }
        });
    }

    public void deleteReminder() {
        findReminder();
        mDatabase.delete(EventDB.Event.REMINDER_TABLE_NAME,
                EventDB.Event.REMINDER_COLUMN_EID + "='" + ID + "' AND " +
                        EventDB.Event.REMINDER_COLUMN_DATE + "='" +
                        reminders.get(gonnaDelete).getText().toString()+"'",
                null);
        if (gonnaDelete < index - 1 ) {
            for (int i = gonnaDelete; i < index - 1; i++) {
                reminders.get(i).setText(reminders.get(i + 1).getText());
            }
        }
        reminders.get(index - 1).setVisibility(View.INVISIBLE);
        clearButtons.get(index - 1).setVisibility(View.INVISIBLE);
        index--;
    }

    private void findReminder() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.REMINDER_TABLE_NAME +
                " WHERE " + EventDB.Event.REMINDER_COLUMN_EID + "='" + ID + "' AND "
                + EventDB.Event.REMINDER_COLUMN_DATE + "='" +
                reminders.get(gonnaDelete).getText().toString() + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.REMINDER_COLUMN_ID));
                cancelAlarm(id);
            } while (cursor.moveToNext());
        }
    }

    private void cancelAlarm(int id) {
        AlarmManager alarmManager = (AlarmManager)getApplicationContext()
                .getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent intent  = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, 0);
        alarmManager.cancel(pendingIntent);
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

    public void findReminders() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.REMINDER_TABLE_NAME +
                " WHERE " + EventDB.Event.REMINDER_COLUMN_EID + "='" + ID + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (index < 6) {
                    String date = cursor.getString(cursor.getColumnIndex(EventDB.Event.REMINDER_COLUMN_DATE));
                    reminders.get(index).setText(date);
                    clearButtons.get(index).setVisibility(View.VISIBLE);
                    reminders.get(index).setVisibility(View.VISIBLE);
                    index++;
                }
            } while (cursor.moveToNext());
        }
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

    public void makeInvisible() {

        clearButton1.setVisibility(View.INVISIBLE);
        clearButton2.setVisibility(View.INVISIBLE);
        clearButton3.setVisibility(View.INVISIBLE);
        clearButton4.setVisibility(View.INVISIBLE);
        clearButton5.setVisibility(View.INVISIBLE);
        clearButton6.setVisibility(View.INVISIBLE);

        reminder1.setVisibility(View.INVISIBLE);
        reminder2.setVisibility(View.INVISIBLE);
        reminder3.setVisibility(View.INVISIBLE);
        reminder4.setVisibility(View.INVISIBLE);
        reminder5.setVisibility(View.INVISIBLE);
        reminder6.setVisibility(View.INVISIBLE);
    }

    public void initializeLists() {
        clearButtons.add(clearButton1);
        clearButtons.add(clearButton2);
        clearButtons.add(clearButton3);
        clearButtons.add(clearButton4);
        clearButtons.add(clearButton5);
        clearButtons.add(clearButton6);

        reminders.add(reminder1);
        reminders.add(reminder2);
        reminders.add(reminder3);
        reminders.add(reminder4);
        reminders.add(reminder5);
        reminders.add(reminder6);
    }

}

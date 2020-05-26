package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RepeatActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    public RadioGroup repeatRadioGroup, durationRadioGroup;
    public RadioButton repeatRB, durationRB;
    public CheckBox monCB, tueCB, wenCB, thuCB, friCB, satCB, sunCB;
    public EditText everyET, repetitionET;
    public TextView dayTW, durationTW, everyTW, oldRepeatTV;
    public ImageButton calendarButton;
    public Button saveButton, clearButton;
    public Calendar c;
    public String currentDateString;
    public int DAY, YEAR, MONTH, ID, SERI;
    public boolean mon, tue, wed, thu, fri, sat, sun, oldRepeat = false, deleted = false,
            newRepeat = true;
    Intent data;
    private SQLiteDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        EventDBHelper dbHelper = new EventDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        repeatRadioGroup = findViewById(R.id.repeatRadioGroup);
        durationRadioGroup = findViewById(R.id.durationRadioGroup);
        monCB = findViewById(R.id.mondayCB);
        tueCB = findViewById(R.id.tuesdayCB);
        wenCB = findViewById(R.id.wednesdayCB);
        thuCB = findViewById(R.id.thursdayCB);
        friCB = findViewById(R.id.fridayCB);
        satCB = findViewById(R.id.saturdayCB);
        sunCB = findViewById(R.id.sundayCB);
        everyET = findViewById(R.id.everyET);
        repetitionET = findViewById(R.id.repeatET);
        calendarButton = findViewById(R.id.calendarButton);
        dayTW = findViewById(R.id.dayTW);
        everyTW = findViewById(R.id.everyText);
        durationTW = findViewById(R.id.durationText);
        saveButton = findViewById(R.id.saveButton);
        clearButton = findViewById(R.id.clearButton);
        oldRepeatTV = findViewById(R.id.oldRepeat);

        DAY = getIntent().getIntExtra("DAY", 1);
        YEAR = getIntent().getIntExtra("YEAR", 2020);
        MONTH = getIntent().getIntExtra("MONTH", 0);

        if (getIntent().hasExtra("ID")) {
            ID = getIntent().getIntExtra("ID", -1);
            SERI = getIntent().getIntExtra("SERI", -1);
            hasOldRepeat();
        }
        else {
            clearButton.setVisibility(View.INVISIBLE);
            oldRepeatTV.setVisibility(View.INVISIBLE);
        }

        data = new Intent();

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt("DAY", DAY);
                args.putInt("YEAR", YEAR);
                args.putInt("MONTH", MONTH);
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOldRepeats();
                clearButton.setVisibility(View.INVISIBLE);
                oldRepeatTV.setVisibility(View.INVISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!oldRepeat) {
                    String day = dayTW.getText().toString();
                    String every = everyET.getText().toString();
                    String text = "Every " + every + " " + day + ".";
                    data.putExtra("Seri Type", text);
                    data.putExtra("Repeat Type", repeatRB.getText().toString());
                    if (!repeatRB.getText().toString().equals("Never")) {
                        data.putExtra("Repeat Frequency", everyET.getText().toString());
                        data.putExtra("Duration Type", durationRB.getText().toString());
                        newRepeat = true;
                    }
                    if (durationRB.getText().toString().equals("Repetitions")) {
                        data.putExtra("Repeat Count", repetitionET.getText().toString());
                        newRepeat = true;
                    } else if (durationRB.getText().toString().equals("Until")) {
                        data.putExtra("Until Date", currentDateString);
                        newRepeat = true;
                    }
                    if (repeatRB.getText().toString().equals("Weekly")) {
                        boolean[] daysOfWeek = new boolean[7];
                        daysOfWeek[0] = sun;
                        daysOfWeek[1] = mon;
                        daysOfWeek[2] = tue;
                        daysOfWeek[3] = wed;
                        daysOfWeek[4] = thu;
                        daysOfWeek[5] = fri;
                        daysOfWeek[6] = sat;
                        data.putExtra("Days of Week", daysOfWeek);
                        newRepeat = true;
                    }
                    if (!oldRepeat && !newRepeat) {
                        data.putExtra("Deleted", true);
                    }
                    setResult(RESULT_OK, data);
                    finish();
                }
                else {
                    Snackbar mySnackbar = Snackbar.make(v, "Events can't repeat twice. " +
                                    "Could not saved.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });
    }

    public void deleteOldRepeats() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_SERI + "='" + SERI + "' ORDER BY datetime(" +
                EventDB.Event.COLUMN_START + ") ASC;";

        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_ID));
                mDatabase.delete(EventDB.Event.TABLE_NAME, EventDB.Event.COLUMN_ID + "=" +
                                id,null);
            }
            oldRepeat = false;
            deleted = true;
            ContentValues cv = new ContentValues();
            cv.put(EventDB.Event.COLUMN_SERI, -1);
            mDatabase.update(EventDB.Event.TABLE_NAME, cv, EventDB.Event.COLUMN_ID + "="
                            + ID,null);
        }
    }


    public void hasOldRepeat() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_ID + "='" + ID + "';";
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex(EventDB.Event.COLUMN_SERI)) != -1) {
                    String seriType = cursor.getString(cursor.getColumnIndex(EventDB.Event.COLUMN_SERI_TYPE));
                    clearButton.setVisibility(View.VISIBLE);
                    oldRepeatTV.setVisibility(View.VISIBLE);
                    oldRepeatTV.setText(seriType);
                    oldRepeat = true;
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDateString = df.format(c.getTime());
    }

    public void dayChecked(View view) {
        mon = monCB.isChecked();
        tue = tueCB.isChecked();
        wed = wenCB.isChecked();
        thu = thuCB.isChecked();
        fri = friCB.isChecked();
        sat = satCB.isChecked();
        sun = sunCB.isChecked();
    }

    public void repeatCheck(View view) {
        int radioId = repeatRadioGroup.getCheckedRadioButtonId();
        repeatRB = findViewById(radioId);

        String name = repeatRB.getText().toString();
        name = name.substring(0, name.length() - 2);

        if(name.equals("Dai"))
            dayTW.setText("Days");
        else
            dayTW.setText(name + "s");

        visibilityController(name);
    }

    public void durationCheck(View view) {
        int radioID = durationRadioGroup.getCheckedRadioButtonId();
        durationRB = findViewById(radioID);

        if (durationRB.getText().toString().equals("Repetitions")) {
            repetitionET.setVisibility(View.VISIBLE);
            calendarButton.setVisibility(View.INVISIBLE);
        }
        else if (durationRB.getText().toString().equals("Until")) {
            calendarButton.setVisibility(View.VISIBLE);
            repetitionET.setVisibility(View.INVISIBLE);
        }
        else {
            calendarButton.setVisibility(View.INVISIBLE);
            repetitionET.setVisibility(View.INVISIBLE);
        }
    }

    public void visibilityController(String name) {
        // days of week
        if (name.equals("Week")) {
            monCB.setVisibility(View.VISIBLE);
            tueCB.setVisibility(View.VISIBLE);
            wenCB.setVisibility(View.VISIBLE);
            thuCB.setVisibility(View.VISIBLE);
            friCB.setVisibility(View.VISIBLE);
            satCB.setVisibility(View.VISIBLE);
            sunCB.setVisibility(View.VISIBLE);

        }
        else {
            monCB.setVisibility(View.INVISIBLE);
            tueCB.setVisibility(View.INVISIBLE);
            wenCB.setVisibility(View.INVISIBLE);
            thuCB.setVisibility(View.INVISIBLE);
            friCB.setVisibility(View.INVISIBLE);
            satCB.setVisibility(View.INVISIBLE);
            sunCB.setVisibility(View.INVISIBLE);
        }
        // others
        if (name.equals("Nev")) {
            durationRadioGroup.setVisibility(View.INVISIBLE);
            everyET.setVisibility(View.INVISIBLE);
            dayTW.setVisibility(View.INVISIBLE);
            everyTW.setVisibility(View.INVISIBLE);
            durationTW.setVisibility(View.INVISIBLE);
        }
        else {
            durationRadioGroup.setVisibility(View.VISIBLE);
            everyET.setVisibility(View.VISIBLE);
            dayTW.setVisibility(View.VISIBLE);
            everyTW.setVisibility(View.VISIBLE);
            durationTW.setVisibility(View.VISIBLE);

        }
        repetitionET.setVisibility(View.INVISIBLE);
        calendarButton.setVisibility(View.INVISIBLE);

    }
}

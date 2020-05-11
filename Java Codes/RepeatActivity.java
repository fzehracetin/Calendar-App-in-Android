package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RepeatActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    public RadioGroup repeatRadioGroup, durationRadioGroup;
    public RadioButton repeatRB, durationRB;
    public CheckBox monCB, tueCB, wenCB, thuCB, friCB, satCB, sunCB;
    public EditText everyET, repetitionET;
    public TextView dayTW, durationTW, everyTW;
    public ImageButton calendarButton;
    public Button saveButton;
    public Calendar c;
    public String currentDateString;
    public int DAY, YEAR, MONTH;
    public boolean mon, tue, wed, thu, fri, sat, sun;
    Intent data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
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

        DAY = getIntent().getIntExtra("DAY", 1);
        YEAR = getIntent().getIntExtra("YEAR", 2020);
        MONTH = getIntent().getIntExtra("MONTH", 0);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.putExtra("Repeat Type", repeatRB.getText().toString());
                if (! repeatRB.getText().toString().equals("Never")) {
                    data.putExtra("Repeat Frequency", everyTW.getText().toString());
                }
                if (durationRB.getText().toString().equals("Repetitions")) {
                    data.putExtra("Repeat Count", repetitionET.getText().toString());
                }
                else if (durationRB.getText().toString().equals("Until")) {
                    data.putExtra("Until Date", currentDateString);
                }
                if (! repeatRB.getText().toString().equals("Weekly")) {
                    data.putExtra("Sunday", sun);
                    data.putExtra("Monday", mon);
                    data.putExtra("Tuesday", tue);
                    data.putExtra("Wednesday", wed);
                    data.putExtra("Thursday", thu);
                    data.putExtra("Friday", fri);
                    data.putExtra("Saturday", sat);
                }
                setResult(RESULT_OK, data);
                finish();
            }
        });

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

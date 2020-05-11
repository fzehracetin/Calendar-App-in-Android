package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    private CalendarView mCalendarView;
    private Calendar c;
    private TextView dayTV;
    private String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendarView = findViewById(R.id.calendarView);
        dayTV = findViewById(R.id.today_text);
        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String date = day + " " + months[month] + " " + year;
        dayTV.setText(date);

        date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        String dateName = date.split(",")[0];

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month,
                                            int day) {
                String date = day + " " + months[month] + " " + year;
                dayTV.setText(date);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

                Intent eventListIntent = new Intent(MainActivity.this, EventListActivity.class);
                eventListIntent.putExtra("DAY", day);
                eventListIntent.putExtra("MONTH", month);
                eventListIntent.putExtra("YEAR", year);
                eventListIntent.putExtra("DAY_NAME", currentDateString.split(",")[0]);
                startActivity(eventListIntent);
            }
        });
    }
}

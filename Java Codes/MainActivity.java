package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    private CalendarView mCalendarView;
    private Calendar c;
    private Toolbar toolbar;
    private TextView monthTV, yearTV;
    private String[] months = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                                            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private Button todayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendarView = findViewById(R.id.calendarView);
        monthTV = findViewById(R.id.monthText);
        yearTV = findViewById(R.id.yearText);
        todayButton = findViewById(R.id.todayButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        c = Calendar.getInstance();
        monthTV.setText(months[c.get(Calendar.MONTH)]);
        yearTV.setText(Integer.toString(c.get(Calendar.YEAR)));


        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month,
                                            int day) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                monthTV.setText(months[c.get(Calendar.MONTH)]);
                yearTV.setText(Integer.toString(c.get(Calendar.YEAR)));
                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

                Intent eventListIntent = new Intent(MainActivity.this, EventListActivity.class);
                eventListIntent.putExtra("DAY", day);
                eventListIntent.putExtra("MONTH", month);
                eventListIntent.putExtra("YEAR", year);
                eventListIntent.putExtra("DAY_NAME", currentDateString.split(",")[0]);
                startActivity(eventListIntent);
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.setDate(c.getTimeInMillis());
                c = Calendar.getInstance();
                monthTV.setText(months[c.get(Calendar.MONTH)]);
                yearTV.setText(Integer.toString(c.get(Calendar.YEAR)));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.weekly:
                Toast.makeText(this, "Weekly selected.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.monthly:
                Toast.makeText(this, "Monthly selected.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                Toast.makeText(this, "Settings selected.", Toast.LENGTH_SHORT).show();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}

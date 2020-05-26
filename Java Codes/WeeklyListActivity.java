package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeeklyListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private String TYPE;
    private RecyclerView recyclerView;
    private TextView monthTV, yearTV;
    private EventAdapter mAdapter;
    private SQLiteDatabase mDatabase;
    String startDate, endDate;
    private String[] months = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_list);

        EventDBHelper dbHelper = new EventDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        ImageButton weeklyButton = findViewById(R.id.weekButton);
        recyclerView = findViewById(R.id.weeklyRecyclerView);
        monthTV = findViewById(R.id.monthText);
        yearTV = findViewById(R.id.yearText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TYPE = getIntent().getStringExtra("Type");
        c = Calendar.getInstance();
        startDate = "2020-05-11";
        endDate = "2020-05-17";

        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt("DAY", c.get(Calendar.DAY_OF_MONTH));
                args.putInt("YEAR", c.get(Calendar.YEAR));
                args.putInt("MONTH", c.get(Calendar.MONTH));
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    public void recycleMaker() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new EventAdapter(this, sqlQuery(), TYPE);
        recyclerView.setAdapter(mAdapter);
    }

    public Cursor sqlQuery() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_START + ">= '" + startDate +
                "' AND " + EventDB.Event.COLUMN_START + "<= '"+ endDate + "' ORDER BY datetime("
                + EventDB.Event.COLUMN_START + ") ASC;" ;
        Cursor cursor = mDatabase.rawQuery(SQLQuery, null);
        return cursor;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Calendar firstDay = initializeCalendar(year, month, dayOfMonth);
        Calendar lastDay = initializeCalendar(year, month, dayOfMonth);
        String text;
        if (TYPE.equals("Weekly")) {
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek != 1) {
                lastDay.add(Calendar.DAY_OF_MONTH, 7 - dayOfWeek + 1);
                if (dayOfWeek > 2) {
                    firstDay.add(Calendar.DAY_OF_MONTH, 2 - dayOfWeek);
                }
            }
            else {
                firstDay.add(Calendar.DAY_OF_MONTH, -6);
            }
            text = firstDay.get(Calendar.DAY_OF_MONTH) + " " + months[firstDay.get(Calendar.MONTH)]
                    + "-" + lastDay.get(Calendar.DAY_OF_MONTH) + " " + months[lastDay.get(Calendar.MONTH)];
            //intervalTV.setText(text);
            monthTV.setText(months[firstDay.get(Calendar.MONTH)]);
            if (firstDay.get(Calendar.MONTH) != lastDay.get(Calendar.MONTH))
                yearTV.setText(Integer.toString(firstDay.get(Calendar.DAY_OF_MONTH)) + "-" +
                        Integer.toString(lastDay.get(Calendar.DAY_OF_MONTH)) + " " +
                        months[lastDay.get(Calendar.MONTH)]);
            else
                yearTV.setText(Integer.toString(firstDay.get(Calendar.DAY_OF_MONTH)) + "-" +
                        Integer.toString(lastDay.get(Calendar.DAY_OF_MONTH)));
            lastDay.add(Calendar.DAY_OF_MONTH, 1);
        }
        else { // Monthly
            firstDay.set(Calendar.DAY_OF_MONTH, firstDay.getActualMinimum(Calendar.DAY_OF_MONTH));
            lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
            monthTV.setText(months[firstDay.get(Calendar.MONTH)]);
            yearTV.setText(Integer.toString(firstDay.get(Calendar.YEAR)));
        }
        startDate = df.format(firstDay.getTime());
        endDate = df.format(lastDay.getTime());
        recycleMaker();
    }

    public Calendar initializeCalendar(int year, int month, int dayOfMonth) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return c;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        mAdapter = new EventAdapter(this, sqlQuery(), TYPE);
        recyclerView.setAdapter(mAdapter);

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.calendar:
                Intent mainIntent = new Intent(WeeklyListActivity.this, MainActivity.class);
                startActivity(mainIntent);
                return true;
            case R.id.weekly:
                if (TYPE.equals("Monthly")) {
                    Intent weeklyIntent = new Intent(WeeklyListActivity.this, WeeklyListActivity.class);
                    weeklyIntent.putExtra("Type", "Weekly");
                    startActivity(weeklyIntent);
                }
                return true;
            case R.id.monthly:
                if (TYPE.equals(("Weekly"))) {
                    Intent monthlyIntent = new Intent(WeeklyListActivity.this, WeeklyListActivity.class);
                    monthlyIntent.putExtra("Type", "Monthly");
                    startActivity(monthlyIntent);
                }
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(WeeklyListActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}

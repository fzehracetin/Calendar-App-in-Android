package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventListActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private EventAdapter mAdapter;
    private int DAY, MONTH, YEAR;
    private String DAY_NAME, currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        EventDBHelper dbHelper = new EventDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        DAY = getIntent().getIntExtra("DAY", 1);
        YEAR = getIntent().getIntExtra("YEAR", 1970);
        MONTH = getIntent().getIntExtra("MONTH", 0);
        DAY_NAME = getIntent().getStringExtra("DAY_NAME");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, YEAR);
        c.set(Calendar.MONTH, MONTH);
        c.set(Calendar.DAY_OF_MONTH, DAY);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(c.getTime());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new EventAdapter(this, getAllItems());
        recyclerView.setAdapter(mAdapter);

        Button addButton = findViewById(R.id.addButton);
        TextView dayNumberTV = findViewById(R.id.day_number);
        TextView dayNameTV = findViewById(R.id.day_name);

        dayNumberTV.setText(Integer.toString(DAY));
        dayNameTV.setText(DAY_NAME);


        // new event
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventListIntent = new Intent(EventListActivity.this, EditEventActivity.class);
                eventListIntent.putExtra("DAY", DAY);
                eventListIntent.putExtra("MONTH", MONTH);
                eventListIntent.putExtra("YEAR", YEAR);
                eventListIntent.putExtra("DAY_NAME", DAY_NAME);
                startActivity(eventListIntent);
            }
        });

    }

    private Cursor getAllItems() {
        String SQLQuery = "SELECT * FROM " + EventDB.Event.TABLE_NAME +
                " WHERE " + EventDB.Event.COLUMN_START + " GLOB '" + currentDate + "*';";

        return mDatabase.rawQuery(SQLQuery, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new EventAdapter(this, getAllItems());
        recyclerView.setAdapter(mAdapter);

    }

}

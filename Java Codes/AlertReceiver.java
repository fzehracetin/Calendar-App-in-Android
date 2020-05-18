package com.example.calendar;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.example.calendar.CalendarNotificationChannel.CHANNEL_ID;

public class AlertReceiver extends BroadcastReceiver {

    private NotificationManagerCompat manager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name");
        int id = intent.getIntExtra("id", 0);
        String startDate = intent.getStringExtra("start");

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDate);
            startDate = new SimpleDateFormat("dd MMMM yyyy, HH:mm").format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        manager = NotificationManagerCompat.from(context);
        Intent activityIntent = new Intent(context, EditEventActivity.class);
        activityIntent.putExtra("EDIT", "true");
        activityIntent.putExtra("EVENT NAME", name);

        Random random = new Random();
        int uniqueNum = random.nextInt(9999 - 1000) + 1000;

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                uniqueNum, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_looks_one_black_24dp)
                .setContentTitle(name)
                .setContentText(startDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        manager.notify(0, notification);
    }
}

package com.example.calendar;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.calendar.CalendarNotificationChannel.CHANNEL_ID;
import static com.example.calendar.SettingsActivity.MyPreferences;
import static com.example.calendar.SettingsActivity.NotificationSound;

public class AlertReceiver extends BroadcastReceiver {

    private NotificationManagerCompat manager;
    SharedPreferences sharedPreferences;
    String notificationSP;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);

        String name = intent.getStringExtra("name");
        String startDate = intent.getStringExtra("start");
        String endDate = intent.getStringExtra("end");
        String startDate2 = "";
        int eid = intent.getIntExtra("eid", 0);
        int id = intent.getIntExtra("id", 0);


        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDate);
            startDate2 = new SimpleDateFormat("dd MMMM yyyy, HH:mm").format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        manager = NotificationManagerCompat.from(context);
        Intent activityIntent = new Intent(context, EventDetailsActivity.class);
        activityIntent.putExtra("EVENT NAME", name);
        activityIntent.putExtra("START", startDate);
        activityIntent.putExtra("END", endDate);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                eid, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_looks_one_black_24dp)
                .setContentTitle(name)
                .setContentText(startDate2)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();

        manager.notify(eid, notification);
        ring(context);
    }

    public void ring(Context context) {
        notificationSP = sharedPreferences.getString(NotificationSound,
                RingtoneManager.getActualDefaultRingtoneUri(context,
                        RingtoneManager.TYPE_NOTIFICATION).toString());
        Ringtone r = RingtoneManager.getRingtone(context, Uri.parse(notificationSP));
        r.play();
    }
}

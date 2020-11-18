package com.project.focus;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SilentMode extends AppCompatActivity {

    int hours = 0, minutes = 0;
    TextView silentTime;
    Button stopSilent;
    public Timer timer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.silent_mode);

        silentTime = findViewById(R.id.silentTime);
        stopSilent = findViewById(R.id.stopSilent);

        Date futureDate = getTimes();
        Date nowDate = Calendar.getInstance().getTime();

        long totalSeconds = getDateDiff(nowDate, futureDate, TimeUnit.SECONDS);
        long minutes = 0, hours = 0, seconds = totalSeconds;
        while(seconds >= 60){
            minutes++;
            seconds -= 60;
        }
        while(minutes >= 60){
            hours++;
            minutes -= 60;
        }

        //acum avem minute si ore
        if(hours != 0)
            silentTime.setText("This phone is in silent mode.\nFocusing for " + String.valueOf(hours) + " hrs and " + String.valueOf(minutes + 1) + " mins");
        else
            silentTime.setText("This phone is in silent mode.\nFocusing for " + String.valueOf(minutes + 1) + " mins");

        //updateTime();


        stopSilent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
                rememberTimes(Calendar.getInstance().getTime());
                timer.cancel();
                timer.purge();
                reDo();
                startMainActivity();
            }
        });

    }

    void updateTime(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date futureDate = getTimes();
                        Date nowDate = Calendar.getInstance().getTime();
                        long totalSeconds = getDateDiff(nowDate, futureDate, TimeUnit.SECONDS);
                        long minutes = 0, hours = 0, seconds = totalSeconds;
                        while(seconds >= 60){
                            minutes++;
                            seconds -= 60;
                        }
                        while(minutes >= 60){
                            hours++;
                            minutes -= 60;
                        }
                        if(hours != 0)
                            silentTime.setText("This phone is in silent mode.\nFocusing for " + String.valueOf(hours) + " hrs and " + String.valueOf(minutes + 1) + " mins");
                        else if(minutes != 0)
                            silentTime.setText("This phone is in silent mode.\nFocusing for " + String.valueOf(minutes + 1) + " mins");
                        else
                            silentTime.setText("This phone is in silent mode.\nFocusing for " + String.valueOf(seconds) + " seconds");
                    }
                });
            }
        },1000, 1000);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    void cancelAlarm(){
        Intent intent = new Intent(this, Receiver.class);
        intent.setAction("timesup");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_NO_CREATE);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

        if(pendingIntent != null) {
            am.cancel(pendingIntent);
        }

    }

    void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    void setStopRunning(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Running", "false");
        editor.apply();
    }

    void reDo(){
        Log.i("Service time", "User stopped the timer!");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if ( notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }

        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    void rememberTimes(Date futureDate){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Date",String.valueOf(futureDate));
        editor.apply();
    }

    Date getTimes(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dateString = preferences.getString("Date", "0");
        //Wed Nov 11 14:02:00 GMT+02:00 2020
        DateFormat format = new SimpleDateFormat("EEE LLL dd HH:mm:ss Z yyyy");
        try {
            Date futureDate = format.parse(dateString);
            //Log.i("Date converted: ", String.valueOf(futureDate));
            return futureDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        timer.purge();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!hasFocus){
            timer.cancel();
            timer.purge();
        }
        else{
            timer = new Timer();
            updateTime();
        }
        super.onWindowFocusChanged(hasFocus);
    }
}

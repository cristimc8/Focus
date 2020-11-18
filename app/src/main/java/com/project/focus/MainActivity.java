package com.project.focus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button lock;
    ImageButton isLockedBtn, isUnlockedBtn, editCategory;
    EditText hrs, mnts;
    Intent mServiceIntent;
    PieChart piechart;
    TextView categoryString;
    int hours = 0, minutes = 0;
    boolean firstTime = false;

    public SilenceState silenceState;
    public Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstMeeting();
        if(!firstTime) {
            Log.i("First: ", String.valueOf(firstTime));
            startupCheck();

            Date futureDate = getTimes();
            Log.i("Future: ", String.valueOf(futureDate));
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

            if (totalSeconds > 0) {
                Intent intent = new Intent(this, SilentMode.class);
                this.startActivity(intent);
                this.finish();
            }

            declarations();
            pieChartSetup();
            updateLockedState();
            updateCurrentCategory();
            listeners();
        }

    }

    void updateLockedState(){
        boolean isLockedMode = silenceState.getLockedState();
        if(isLockedMode){
            isLockedBtn.setBackgroundResource(R.drawable.pressedsemibtnleft);
            isUnlockedBtn.setBackgroundResource(R.drawable.semiroundbtn2);
        }
        else{
            isUnlockedBtn.setBackgroundResource(R.drawable.pressedsemibtnright);
            isLockedBtn.setBackgroundResource(R.drawable.semiroundbtn);
        }
    }

    void updateCurrentCategory(){
        categoryString.setText("Category: " + category.getEmojiFromCategoryName(category.currentName()));
    }

    void pieChartSetup(){
        piechart.setUsePercentValues(true);
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(40, "Study"));
        value.add(new PieEntry(60, "Sports"));

        Description desc = new Description();
        desc.setText("");
        desc.setTextSize(1f);
        desc.setTextColor(Color.WHITE);

        piechart.setDescription(desc);
        piechart.setDrawHoleEnabled(false);

        piechart.setDrawRoundedSlices(true);

        PieDataSet pieDataSet = new PieDataSet(value, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextColor(ColorTemplate.getHoloBlue());
        //pieDataSet.setValueLineColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueFormatter(new PercentFormatter(piechart));
        piechart.setEntryLabelColor(Color.WHITE);
        PieData pieData = new PieData(pieDataSet);
        Legend legend = piechart.getLegend();
        legend.setTextColor(Color.WHITE);
        piechart.setData(pieData);

        pieData.setValueTextColor(Color.WHITE);



        piechart.animateXY(1200, 1200);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    void firstMeeting(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String first = preferences.getString("first", "yes");
        Log.i("was here: ", first);
        if(first.equals("yes")){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("first", "no");
            editor.apply();
            Intent firstImpression = new Intent(this, FirstImpression.class);
            firstTime = true;
            this.startActivity(firstImpression);
            this.finish();
        }
    }

    void startupCheck(){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // if user granted access else ask for permission
        if ( notificationManager.isNotificationPolicyAccessGranted()) {
            //ok
        } else{
            // Open Setting screen to ask for permisssion
            startGrantAccess();
        }
    }

    void startGrantAccess(){
        Intent intent = new Intent(this, PermissionClass.class);
        this.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "We need that permission in order to work!", Toast.LENGTH_SHORT).show();
            }

    }


    private void requestMutePermissions() {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if( Build.VERSION.SDK_INT >= 23 ) {
                this.requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp();
            }
        } catch ( SecurityException e ) {

        }
    }

    private void requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp() {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // if user granted access else ask for permission
        if ( notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            da();
        } else{
            // Open Setting screen to ask for permisssion
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult( intent, da());
        }
    }


    public Date addHoursToJavaUtilDate(Date date, int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    int da(){
        Date nowDate = Calendar.getInstance().getTime();
        Date futureDate = addHoursToJavaUtilDate(nowDate, hours, minutes);

        rememberTimes(futureDate);
        getTimes();

        Log.i("Future date: ", String.valueOf(futureDate));

        Intent intent = new Intent(this, Receiver.class);
        intent.setAction("timesup");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, toCalendar(futureDate).getTimeInMillis(), pendingIntent);
        Intent intent2 = new Intent(this, SilentMode.class);
        this.startActivity(intent2);

        return 1;
    }

    void declarations(){
        silenceState = new SilenceState(getApplicationContext());
        category = new Category(getApplicationContext());

        lock = findViewById(R.id.lock);
        hrs = findViewById(R.id.hours);
        mnts = findViewById(R.id.minutes);
        piechart = findViewById(R.id.pieChart);
        isLockedBtn = findViewById(R.id.isLockedBtn);
        isUnlockedBtn = findViewById(R.id.isUnlockedBtn);
        categoryString = findViewById(R.id.categoryString);
        editCategory = findViewById(R.id.editCategory);
    }
    void listeners(){
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hours = Integer.parseInt(String.valueOf(hrs.getText()));
                }
                catch (NumberFormatException exc){
                    hours = 0;
                }
                try {
                    minutes = Integer.parseInt(String.valueOf(mnts.getText()));
                }
                catch (NumberFormatException exc){
                    minutes = 0;
                }
                if(hours == 0 && minutes == 0) return;
                requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp();
                requestMutePermissions();
            }
        });

        isLockedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                silenceState.setLockedState(true);
                updateLockedState();
            }
        });
        isUnlockedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                silenceState.setLockedState(false);
                updateLockedState();
            }
        });
    }

    void rememberTimes(Date futureDate){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Date",String.valueOf(futureDate));
        editor.apply();
    }

    Date getTimes(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dateString = preferences.getString("Date", "Wed Nov 11 14:02:00 GMT+02:00 2020");
        //Wed Nov 11 14:02:00 GMT+02:00 2020
        DateFormat format = new SimpleDateFormat("EEE LLL dd HH:mm:ss Z yyyy");
        try {
            Date futureDate = format.parse(dateString);
            Log.i("Date converted: ", String.valueOf(futureDate));
            return futureDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    boolean getRunning(){
        boolean isRunning;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString("Running", "false").equals("false")){
            return false;
        }
        return true;
    }

    void setRunning(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Running", "true");
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        //if(!isMyServiceRunning(Receiver.class)) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        //}
        super.onDestroy();
    }
    int back = 0;
    @Override
    public void onBackPressed() {
        back++;
        if(back == 1){
            Toast.makeText(this, "Press `back` again to close", Toast.LENGTH_SHORT).show();
        }
        if(back == 2){
            this.finishAffinity();
        }
        //super.onBackPressed();
    }
}
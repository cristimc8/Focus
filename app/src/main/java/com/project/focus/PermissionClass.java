package com.project.focus;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionClass extends AppCompatActivity {

    Button access;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission);


        access = findViewById(R.id.access);

        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp();
            }
        });

    }


    private void requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp() {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // if user granted access else ask for permission
        if ( notificationManager.isNotificationPolicyAccessGranted()) {
            //ok
            goBackToMain();
        } else{
            // Open Setting screen to ask for permisssion
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult( intent, goBackToMain());
        }
    }

    int goBackToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
        return 1;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}

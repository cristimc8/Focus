package com.project.focus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class FirstImpression extends AppCompatActivity {

    TextView slideText;
    Animation slide, bringBack, fadeIn;
    Button start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_impression);

        slideText = findViewById(R.id.slidingText);
        slide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_text);
        bringBack = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bring_text_back);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadein);
        start = findViewById(R.id.start);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                slideText.startAnimation(slide);
            }
        }, 1600);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        slideText.setText("Boost your productivity!");
                    }
                });
                slideText.startAnimation(bringBack);
            }
        }, 2200);

        final Timer timer3 = new Timer();
        timer3.schedule(new TimerTask() {
            @Override
            public void run() {
                slideText.startAnimation(slide);
            }
        }, 4000);

        final Timer timer4 = new Timer();
        timer4.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        slideText.setText("Let go of distractions!");
                    }
                });
                slideText.startAnimation(bringBack);
            }
        }, 4600);

        final Timer timer5 = new Timer();
        timer5.schedule(new TimerTask() {
            @Override
            public void run() {
                start.animate().alpha(1).setDuration(800);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMainActivity();
                    }
                });
            }
        }, 4800);

    }

    void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}

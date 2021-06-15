package com.kotlin.cybindigoproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.kotlin.cybindigoproject.R;
import com.kotlin.cybindigoproject.Utils.SharedPreferenceUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    SharedPreferenceUtils preferances;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferances = SharedPreferenceUtils.getInstance(this);
        final TextView im1 = findViewById(R.id.im1);
        final Animation zoomanimation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.animated_logo);
        im1.startAnimation(zoomanimation);
        zoomanimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation){
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if(!preferances.getStringValue("USER_ID","").equalsIgnoreCase("")){
                            Intent intent =new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent =new Intent(SplashActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,3000);
            }
            public void onAnimationEnd(Animation animation){

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }




}
package com.example.rurbansoft;


import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    private int SLEEP_TIMER = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(Window.FEATURE_NO_TITLE,Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        //getSupportActionBar().hide();
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();
    }

    private class LogoLauncher extends Thread {
        public void run()
        {
            try
            {
                sleep(1000 * SLEEP_TIMER);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
            SplashScreen.this.finish();
        }
    }
}

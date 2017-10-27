package com.perfection.newkeyboard;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import com.perfection.newkeyboard.controller.AppSettingsActivity;
import com.perfection.newkeyboard.utils.PrefUtils;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_splash_screen);
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent mInHome;
                if (PrefUtils.getSharedPrefBoolean(splash_screen.this, PrefUtils.IS_LOGGEDIN)) {
                    mInHome = new Intent(splash_screen.this, AppSettingsActivity.class);
                } else {
                    mInHome = new Intent(splash_screen.this, LoginActivity.class);
                }

                splash_screen.this.startActivity(mInHome);
                splash_screen.this.finish();
            }
        }, 3000);
    }
}

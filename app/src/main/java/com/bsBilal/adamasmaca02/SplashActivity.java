package com.bsBilal.adamasmaca02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


    Window window =getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            Intent home=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(home);
            finish();
        }
    },3000);

    }
}

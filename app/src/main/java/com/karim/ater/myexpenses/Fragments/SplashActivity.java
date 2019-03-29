package com.karim.ater.myexpenses.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProgressBar splashBalancePb = findViewById(R.id.splashBalancePb);
        DatabaseConnector databaseConnector = new DatabaseConnector(SplashActivity.this);
        float cost = databaseConnector.getTotalCost(AppController.getCurrentMonth());
        int income=(int) databaseConnector.getMonthlyIncome(AppController.getCurrentMonth());
        splashBalancePb.setMax(income);
        splashBalancePb.setProgress(0);
        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofInt(splashBalancePb, "progress", (int) cost)
                .setDuration(2000);
        progressAnimator.start();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 500);



//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(intent);
//        finish();
    }
}

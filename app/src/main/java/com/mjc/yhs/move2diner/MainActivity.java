package com.mjc.yhs.move2diner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences prefs;
    final int countTime = 2050 / 2;
    final int stepTime = 1000 / 2;
    CountDownTimer timer;
    public static FirebaseDatabase secondDatabase;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        timer.cancel();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        if (secondDatabase == null) {
            //리뷰데이터는 사용자쪽에 저장해놓고 가져오는걸로 하자
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyC6qXo2aEQBYFaZFpzXbn1VUt81jwEojys")
                    .setApplicationId("com.mjc.yhs.move2diner")
                    .setDatabaseUrl("https://movetodiner.firebaseio.com/")
                    .build();

            FirebaseApp secondApp = FirebaseApp.initializeApp(getApplicationContext(), options, "second app");
            secondDatabase = FirebaseDatabase.getInstance(secondApp);  //이거 스태틱으로 만들어서 쓰게 함
        }
        

        timer = new CountDownTimer(countTime, stepTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                int cnt = (int) (millisUntilFinished);
                if (cnt < 1) onFinish();
            }

            @Override
            public void onFinish() {
                checkFirstRun();
            }
        };
        timer.start();
    }

    public void checkFirstRun() {
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        Intent i;
        if (!isFirstRun) //첫실행이 아닐경우
            i = new Intent(MainActivity.this, LoginActivity.class);
        else //첫 실행
            i = new Intent(MainActivity.this, LoginDefaultActivity.class);
        startActivity(i);
        finish();
    }
}

package com.mjc.yhs.move2diner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class LoginDefaultActivity extends AppCompatActivity {
    public SharedPreferences prefs;

    Button signin, login;
    Animation greetAnim;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_default);

        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        signin = (Button)findViewById(R.id.btnSignin);
        login = (Button)findViewById(R.id.btnLogin);

        greetAnim = AnimationUtils.loadAnimation(this, R.anim.fade);
        findViewById(R.id.tvGreeting).startAnimation(greetAnim);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginDefaultActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("isFirstRun",false).apply();
                Intent intent = new Intent(LoginDefaultActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}

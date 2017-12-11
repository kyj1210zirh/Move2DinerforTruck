package com.mjc.yhs.move2diner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText etdID, etdPass;
    Button btnLogin, btnSignin;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener; //리스터 부착 자동로그인
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        etdID = (EditText)findViewById(R.id.sellerID);
        etdPass = (EditText)findViewById(R.id.sellerPassword);
        mAuth = FirebaseAuth.getInstance();

        //자동로그인 리스너 처리
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //로그인 정보 있으면 자동으로 ActivityMain 클래스로 이동, 로그인 정보 없으면 ActivitySelectAuth 화면 그대로
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, FragmentTab.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.getInstance().progressON(LoginActivity.this,null);
                confirm(etdID.getText().toString(), etdPass.getText().toString());
            }
        });

        findViewById(R.id.btnSignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SigninActivity.class);
                startActivity(i);
            }
        });
    }

    private void confirm(final String email, final String password) {
        if(TextUtils.isEmpty(email)){
            BaseApplication.getInstance().progressOFF();
            etdID.setError("이메일를 입력하세요.");
            return;
        } else if(TextUtils.isEmpty(password)){
            BaseApplication.getInstance().progressOFF();
            etdPass.setError("비밀번호를 입력하세요.");
            return;
        } else {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent =new Intent(LoginActivity.this,FragmentTab.class);
                        intent.putExtra("userID",user.getUid());
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        BaseApplication.getInstance().progressOFF();
                        Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    //자동로그인 처리
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.getInstance().progressOFF();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

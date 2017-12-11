package com.mjc.yhs.move2diner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import static com.mjc.yhs.move2diner.MainActivity.secondDatabase;

public class SigninActivity extends AppCompatActivity {
    TextInputEditText sellerEmail, sellerPassword;
    EditText sellerTruckName;
    Button btnSignin;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();
    private FirebaseAuth mAuth;
    private TextInputLayout emailcheck, passwordcheck;
    private Pattern patternEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signin);

        initVIew();

        mAuth = FirebaseAuth.getInstance();

        sellerEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                CharSequence s = ((EditText) v).getText();
                if (hasFocus == false) {
                    if (!patternEmail.matcher(s).find() || s.length() == 0) {
                        sellerEmail.setError("이메일을 입력하세요(이메일 형식)");
                    }
                } else {
                    sellerEmail.setError(null);
                }
            }
        });


        sellerPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                CharSequence s = ((EditText) v).getText();

                if (hasFocus == false) {
                    if (s.length() >= 0 && s.length() < 6)
                        sellerPassword.setError("패스워드를 확인 하세요(6자 이상)");
                } else {
                    sellerPassword.setError(null);
                }
            }
        });


        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sellerEmail.getError() != null || TextUtils.isEmpty(sellerEmail.getText().toString())) {
                    sellerEmail.setError("이메일을 입력하세요(이메일 형식)");
                    return;
                }

                if (sellerPassword.getError() != null || TextUtils.isEmpty(sellerPassword.getText().toString())) {
                    sellerPassword.setError("비밀번호는 6자리 이상이어야 합니다");
                    return;
                }
                if (TextUtils.isEmpty(sellerTruckName.getText().toString())) {
                    sellerTruckName.setError("트럭이름을 입력해주세요");
                    return;
                }

                BaseApplication.getInstance().progressON(SigninActivity.this, null);
                createUser(sellerEmail.getText().toString(), sellerPassword.getText().toString(), "test");
            }
        });
        findViewById(R.id.btnSigninCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initVIew() {
        emailcheck = (TextInputLayout) findViewById(R.id.emailcheck);
        passwordcheck = (TextInputLayout) findViewById(R.id.passwordcheck);
        sellerEmail = (TextInputEditText) findViewById(R.id.sellerEmail);
        sellerPassword = (TextInputEditText) findViewById(R.id.sellerPassword);
        sellerTruckName = (EditText) findViewById(R.id.sellerTruckName);

        emailcheck.setErrorEnabled(true);
        passwordcheck.setCounterEnabled(true);
        passwordcheck.setCounterMaxLength(15);
        passwordcheck.setErrorEnabled(true);

        if (secondDatabase == null) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyC6qXo2aEQBYFaZFpzXbn1VUt81jwEojys")
                    .setApplicationId("com.mjc.yhs.move2diner")
                    .setDatabaseUrl("https://movetodiner.firebaseio.com/")
                    .build();

            FirebaseApp secondApp = FirebaseApp.initializeApp(getApplicationContext(), options, "second app");
            secondDatabase = FirebaseDatabase.getInstance(secondApp);
        }


    }

    private void createUser(final String email, final String password, final String nickname) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    final FirebaseUser user = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(sellerTruckName.getText().toString()).build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        secondDatabase.getReference().child("trucks").child("info")
                                                .child(user.getUid()).child("truckName")
                                                .setValue(user.getDisplayName());
                                        sellerTruckName.setText(user.getDisplayName());

                                        secondDatabase.getReference().child("trucks/info")
                                                .child(user.getUid()).child("onBusiness").setValue(false);

                                        secondDatabase.getReference().child("trucks/info")
                                                .child(user.getUid()).child("payCard").setValue(false);

                                        loginUser(email, password);
                                    }
                                }
                            });
                } else {
                    // If sign in fails, display a message to the user.
                    BaseApplication.getInstance().progressOFF();
                    Toast.makeText(SigninActivity.this, "가입실패\n" + email + "은 이미 존재하는 계정입니다", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SigninActivity.this, "처음 오신것을 환영합니다\n나의 트럭 정보에서 사진을 등록해주세요", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SigninActivity.this, FragmentTab.class);
                    startActivity(intent);
                    BaseApplication.getInstance().progressOFF();
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    BaseApplication.getInstance().progressOFF();
                    Toast.makeText(SigninActivity.this, "로그인유저 메소드 Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

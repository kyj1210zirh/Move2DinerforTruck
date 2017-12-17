package com.mjc.yhs.move2diner;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.TedPermission;
import com.mjc.yhs.move2diner.DTO.MenuListItem;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by Kang on 2017-12-05.
 */

public class DMMenuActivity extends AppCompatActivity implements View.OnClickListener, PermissionCheck.PermissionCallback {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    PermissionCheck permissionCheck;

    private Uri imagePath;

    ImageView img_FD;
    EditText edt_FDName, edt_FDPrice, edt_FDDescribe;
    String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_menu);

        permissionCheck = new PermissionCheck(this, 200);
        permissionCheck.registerCallback(this);

        img_FD = (ImageView) findViewById(R.id.img_FD);
        edt_FDName = (EditText) findViewById(R.id.edt_FDName);
        edt_FDPrice = (EditText) findViewById(R.id.edt_FDPrice);
        edt_FDDescribe = (EditText) findViewById(R.id.edt_FDDescribe);

        img_FD.setOnClickListener(this);
        findViewById(R.id.edit_FD_img).setOnClickListener(this);
        findViewById(R.id.btn_addFD).setOnClickListener(this);

        uid = getIntent().getStringExtra("PrimaryKey");
        if (uid != null) {
            new CustomTitlebar(this, "메뉴 수정");
            ((Button)findViewById(R.id.btn_addFD)).setText("메뉴 수정하기");
            settingData();
        } else {
            new CustomTitlebar(this, "메뉴 추가");
        }
    }

    private void settingData() {
        mDatabase.child("trucks").child("menu").child(auth.getCurrentUser().getUid()).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MenuListItem menuListItem = dataSnapshot.getValue(MenuListItem.class);
                edt_FDName.setText(menuListItem.getFoodName());
                edt_FDPrice.setText(String.valueOf(menuListItem.getFoodPrice()));
                edt_FDDescribe.setText(menuListItem.getFoodDescribe());
                if (menuListItem.getFoodStoragePath() != null) {
                    imagePath = Uri.parse(menuListItem.getFoodStoragePath());
                    Glide.with(DMMenuActivity.this).load(imagePath).into(img_FD);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_FD:
            case R.id.edit_FD_img:
                new TedPermission(this)
                        .setPermissionListener(permissionCheck)
                        .setDeniedMessage("사진 접근을 허용해 주셔야 합니다")
                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                        .setGotoSettingButton(true)
                        .setGotoSettingButtonText("설정")
                        .check();
                break;
            case R.id.btn_addFD:
                if (TextUtils.isEmpty(edt_FDName.getText().toString())) {
                    edt_FDName.setError("메뉴 이름을 입력하세요.");
                } else if (TextUtils.isEmpty(edt_FDPrice.getText().toString())) {
                    edt_FDPrice.setError("메뉴 가격을 입력하세요.");
                } else {
                    AddMenu(imagePath);
                }
                break;
        }
    }

    public void AddMenu(Uri uri) {
        if (uri != null && uid==null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://move2diner.appspot.com");

            Uri file = uri;
            StorageReference riversRef = storageRef.child("images/menus/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("Add Menu Exception ", exception.getMessage());
                    Toast.makeText(getApplicationContext(), "메뉴 추가 실패", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") final //BUG , 적어줘야 에러안남
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    MenuListItem fdDTO = new MenuListItem(edt_FDName.getText().toString(),
                            edt_FDDescribe.getText().toString(),
                            Integer.parseInt(edt_FDPrice.getText().toString()),
                            taskSnapshot.getDownloadUrl().toString());

                    mDatabase.child("trucks").child("menu").child(auth.getCurrentUser().getUid()).push().setValue(fdDTO);
                }
            });
        } else if(uid==null) {
            MenuListItem fdDTO = new MenuListItem(edt_FDName.getText().toString(),
                    edt_FDDescribe.getText().toString(),
                    Integer.parseInt(edt_FDPrice.getText().toString()),
                    "https://firebasestorage.googleapis.com/v0/b/move2diner.appspot.com/o/images%2Fmenus%2FJPEG_20171113165628_1722086011.jpg?alt=media&token=0ce34944-f687-4e7a-a4ea-f63234e10703");
            mDatabase.child("trucks").child("menu").child(auth.getCurrentUser().getUid()).push().setValue(fdDTO);
        } else{
            MenuListItem fdDTO = new MenuListItem(edt_FDName.getText().toString(),
                    edt_FDDescribe.getText().toString(),
                    Integer.parseInt(edt_FDPrice.getText().toString()),
                    imagePath.toString());
            mDatabase.child("trucks").child("menu").child(auth.getCurrentUser().getUid()).child(uid).setValue(fdDTO);
        }
        finish();
    }

    @Override
    public void callbackMethod() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(DMMenuActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
//                        BaseApplication.getInstance().progressON(DMMenuActivity.this, null);
                        imagePath = uri;
                        Glide.with(DMMenuActivity.this).load(imagePath).into(img_FD);
//                        uploadImage(imagePath);
                    }
                })
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }
}

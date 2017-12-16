package com.mjc.yhs.move2diner;

import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mjc.yhs.move2diner.DTO.PictureDTO;
import com.mjc.yhs.move2diner.DTO.TruckInfoItem;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

import static com.mjc.yhs.move2diner.MainActivity.secondDatabase;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Uri imagePath;
    private ImageView img_thumbnail;
    private TextView text_truckName, text_description, text_tags, text_noPicture;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ThumbnailPermission thumbnailPermission = new ThumbnailPermission();
    private PicturesPermission picturesPermission = new PicturesPermission();
    private PictureAdapter pictureAdapter;
    private RecyclerView recyclerView;
    private ArrayList<PictureDTO> pictures = new ArrayList<>();
    private String truckname, description;
    private ScrollView scrollView;
    private Switch switch_paycard;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);
        new CustomTitlebar(this, "나의 트럭 정보");

        BaseApplication.getInstance().progressON(ProfileActivity.this, null);

        initView();

        //파이어 베이스에서 데이터 가져오기
        //기본 프로필에도 트럭이름과,썸네일을 저장해놓고 디비에도 저장해놨어
        //둘중 어디에서 가져와도 괜찮음
        text_truckName.setText(auth.getCurrentUser().getDisplayName());

        Query infoQuery = secondDatabase.getReference().child("trucks").child("info").child(auth.getCurrentUser().getUid());
        infoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TruckInfoItem info = dataSnapshot.getValue(TruckInfoItem.class);
                truckname = info.getTruckName();
                description = info.getTruckDes();


                StringBuilder sb = new StringBuilder();

                if (info.getTags() != null) {
                    for (int i = 0; i < info.getTags().size(); i++) {
                        sb.append(info.getTags().get(i) + ",");
                    }
                    sb.delete(sb.length()-1, sb.length());
                    text_tags.setText(sb);
                }

                text_description.setText(description);
                Glide.with(ProfileActivity.this).load(info.getThumbnail()).placeholder(R.drawable.ic_account_circle_black_24dp)
                        .error(R.drawable.ic_account_circle_black_24dp).into(img_thumbnail);

                Boolean payCard = info.getPayCard();
                if (payCard == true) {
                    switch_paycard.setChecked(true);
                } else {
                    switch_paycard.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query picQuery = mDatabase.child("trucks").child("pictures").child(auth.getCurrentUser().getUid());
        picQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pictures.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    text_noPicture.setVisibility(View.VISIBLE);
                } else {
                    text_noPicture.setVisibility(View.GONE);
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PictureDTO pic = new PictureDTO();
                    pic.setUserID(auth.getCurrentUser().getUid());
                    pic.setPushid(snapshot.getKey());
                    pic.setStoragePath(snapshot.getValue().toString());
                    pictures.add(pic);
                }
                pictureAdapter.notifyDataSetChanged();
                BaseApplication.getInstance().progressOFF();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {

        switch_paycard = (Switch) findViewById(R.id.switch_paycard);
        img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        text_truckName = (TextView) findViewById(R.id.text_truckName);
        text_description = (TextView) findViewById(R.id.text_description);
        text_tags = (TextView) findViewById(R.id.text_tags);
        text_noPicture = (TextView) findViewById(R.id.text_noPicture);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        text_truckName.setOnClickListener(this);
        text_description.setOnClickListener(this);
        text_tags.setOnClickListener(this);
        img_thumbnail.setOnClickListener(this);

        findViewById(R.id.edit_thumbnail).setOnClickListener(this);
        findViewById(R.id.text_addPicture).setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        pictureAdapter = new PictureAdapter(pictures, ProfileActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(pictureAdapter);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });


        switch_paycard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                secondDatabase.getReference().child("trucks/info").child(auth.getCurrentUser().getUid()).child("payCard").setValue(isChecked);
            }
        });

    }

    public void uploadImage(Uri uri) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://move2diner.appspot.com");

        Uri file = uri;
        StorageReference riversRef = storageRef.child("images/thumbnail/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        //지금까지 스토리지에는 사진을 저장했고, 이제 프로필에 연결(저장)해야해 (아래의 콜백으로)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") final //BUG , 적어줘야 에러안남
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(downloadUrl.toString()))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    secondDatabase.getReference().child("trucks").child("info")
                                            .child(auth.getCurrentUser().getUid())
                                            .child("thumbnail").setValue(downloadUrl.toString());
                                    BaseApplication.getInstance().progressOFF();
                                    Toast.makeText(ProfileActivity.this, "저장 완료!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                BaseApplication.getInstance().progressOFF();
                            }
                        });
            }
        });

    }

    public void uploadPictures(Uri uri) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://move2diner.appspot.com");

        Uri file = uri;
        StorageReference riversRef = storageRef.child("images/trucks/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") final //BUG , 적어줘야 에러안남
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mDatabase.child("trucks").child("pictures").child(auth.getCurrentUser().getUid()).push().setValue(downloadUrl.toString());
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_thumbnail:
            case R.id.edit_thumbnail:
                new TedPermission(this)
                        .setPermissionListener(thumbnailPermission)
                        .setDeniedMessage("사진 접근을 허용해 주셔야 합니다")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .setGotoSettingButton(true)
                        .setGotoSettingButtonText("설정")
                        .check();
                break;
            case R.id.text_truckName:
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
                alert.setTitle("트럭이름 변경");
                final EditText editText = new EditText(getApplicationContext());
                alert.setView(editText);
                editText.setText(text_truckName.getText().toString());
                alert.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(editText.getText().toString()).build();
                        final FirebaseUser user = auth.getCurrentUser();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            secondDatabase.getReference().child("trucks").child("info")
                                                    .child(user.getUid()).child("truckName")
                                                    .setValue(user.getDisplayName());
                                            text_truckName.setText(user.getDisplayName());
                                        }
                                    }
                                });
                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
                break;

            case R.id.text_description:
                AlertDialog.Builder alert2 = new AlertDialog.Builder(ProfileActivity.this);
                alert2.setTitle("한줄 소개");
                final EditText editText2 = new EditText(ProfileActivity.this);
                alert2.setView(editText2);
                editText2.setText(text_description.getText().toString());
                alert2.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        secondDatabase.getReference().child("trucks").child("info").child(auth.getCurrentUser().getUid())
                                .child("truckDes").setValue(editText2.getText().toString());
                        text_description.setText(editText2.getText().toString());
                    }
                });
                alert2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert2.show();

                break;
            case R.id.text_tags:
                AlertDialog.Builder alert3 = new AlertDialog.Builder(ProfileActivity.this);
                alert3.setTitle("태그 입력 (ex) 강남,홍대");

                final EditText editText3 = new EditText(ProfileActivity.this);
                alert3.setView(editText3);

                editText3.setText(text_tags.getText().toString());


                alert3.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] splited = editText3.getText().toString().split(",");

                        ArrayList<String> tags = new ArrayList<>();
                        for (int i = 0; i < splited.length; i++) {
                            tags.add(splited[i]);
                        }

                        secondDatabase.getReference().child("trucks").child("info").child(auth.getCurrentUser().getUid())
                                .child("tags").setValue(tags);

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < tags.size(); i++) {
                            sb.append(tags.get(i) + ",");
                        }
                        sb.delete(sb.length()-1, sb.length());
                        text_tags.setText(sb);

                    }
                });
                alert3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert3.show();
                break;
            case R.id.text_addPicture:
                //다중선택
                new TedPermission(ProfileActivity.this)
                        .setPermissionListener(picturesPermission)
                        .setDeniedMessage("사진 접근을 허용해 주셔야 합니다")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .setGotoSettingButton(true)
                        .setGotoSettingButtonText("설정")
                        .check();
                break;
        }
    }


    private class ThumbnailPermission implements PermissionListener {

        @Override
        public void onPermissionGranted() {
            TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(ProfileActivity.this)
                    .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(final Uri uri) {
                            BaseApplication.getInstance().progressON(ProfileActivity.this, null);
                            imagePath = uri;
                            Glide.with(ProfileActivity.this).load(imagePath).into(img_thumbnail);
                            uploadImage(imagePath);
                        }
                    })
                    .create();
            bottomSheetDialogFragment.show(getSupportFragmentManager());

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    }

    private class PicturesPermission implements PermissionListener {

        @Override
        public void onPermissionGranted() {
            TedBottomPicker bottomSheetDialogFragment2 = new TedBottomPicker.Builder(ProfileActivity.this)
                    .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                        @Override
                        public void onImagesSelected(ArrayList<Uri> uriList) {
                            BaseApplication.getInstance().progressON(ProfileActivity.this, null);
                            for (Uri uri : uriList) {
                                uploadPictures(uri);
                            }
                            text_noPicture.setVisibility(View.GONE);
                            pictureAdapter.notifyDataSetChanged();
                            BaseApplication.getInstance().progressOFF();
                        }
                    })
                    .setSelectMaxCount(10)
                    .setPeekHeight(1600)
                    .showTitle(false)
                    .setCompleteButtonText("완료")
                    .setEmptySelectionText("No Select")
                    .create();

            bottomSheetDialogFragment2.show(getSupportFragmentManager());

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    }
}

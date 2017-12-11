package com.mjc.yhs.move2diner.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mjc.yhs.move2diner.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.mjc.yhs.move2diner.MainActivity.secondDatabase;


public class SalesSituationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    static private View rootView;
    boolean btnSituation = false;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();
    private DatabaseReference pushid;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MyPermissionListener myPermissionListener;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private boolean isGPSEnabled, isNetworkEnabled;
    private double latitude, longitude;
    private Marker lastLocationMarker;
    private SupportMapFragment mapFragment;

    private Button BtnSalesSituation;
    private TextView txtTruckName;
    private ImageView ivSalesStartTime, ivSalesLocation;
    private String SalesStartTime, SalesLocation;

    public SalesSituationFragment() {

    }

    public static SalesSituationFragment newInstance() {
        return new SalesSituationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.layout_sales_status, container, false);
        } catch (InflateException e) {
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        geocoder = new Geocoder(getContext(), Locale.KOREA);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.gmap);
        txtTruckName = (TextView) rootView.findViewById(R.id.txtTruckName);
        BtnSalesSituation = (Button) rootView.findViewById(R.id.BtnSalesStatus);
        ivSalesStartTime = (ImageView) rootView.findViewById(R.id.ivSalesStartTime);
        ivSalesLocation = (ImageView) rootView.findViewById(R.id.ivSalesLocation);
        txtTruckName.setText(mAuth.getCurrentUser().getDisplayName());

        myPermissionListener = new MyPermissionListener();

        new TedPermission(getActivity())
                .setPermissionListener(myPermissionListener)
                .setDeniedMessage("내 위치를 찾기 위해서는 GPS와 네트워크 권한이 필요합니다.")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                .setGotoSettingButton(true)
                .setGotoSettingButtonText("설정")
                .check();


        //영업 시작 버튼 눌렀을때
        BtnSalesSituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
                System.out.println("영업시작");
                if (latitude == 0.0 || longitude == 0.0) {
                    alertDialog("알림", "현재 위치를 확인한 후 다시 시도하세요.");
                } else {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdfNow2 = new SimpleDateFormat("HH:mm:ss");
                    String formatDate1 = sdfNow1.format(date);
                    String formatDate2 = sdfNow2.format(date);

                    if (!btnSituation) {

                        pushid = mDatabase.child("trucks").child("salessituation").child(user.getUid()).push();
                        btnSituation = true;
                        BtnSalesSituation.setText("영업 종료하기");
                        BtnSalesSituation.setBackgroundColor(Color.BLUE);

                        SalesStartTime = formatDate2;

                        //사용자가 트럭들의 데이터 조회할때 트럭이름과 트럭키 필요함
                        pushid.child("onBusiness").setValue(btnSituation);  //영업중 표시 함 (사용자들이 검색할때 이필드가 true 인것만 가져와야함)
                        pushid.child("salesdate").setValue(formatDate1);
                        pushid.child("starttime").setValue(formatDate2);
                        pushid.child("truckName").setValue(mAuth.getCurrentUser().getDisplayName());

                        secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("onBusiness").setValue(true);
                        secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("startTime").setValue(formatDate2);

                        Uri photoUri = mAuth.getCurrentUser().getPhotoUrl();
                        if (photoUri != null)
                            secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("thumbnail").setValue(photoUri.toString());
                        else
                            secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("thumbnail")
                                    .setValue("https://firebasestorage.googleapis.com/v0/b/move2diner.appspot.com/o/images%2Fthumbnail%2Floadingimage.jpg?alt=media&token=750464a5-7133-4f56-a722-5648466c71a7");


                        //경도와 위도로 주소 취득 및 서버 저장
                        List<Address> list = null;
                        try {
                            double d1 = latitude;
                            double d2 = longitude;

                            list = geocoder.getFromLocation(d1, d2, 5);

                            if (list != null) {
                                if (list.size() == 0) {
                                } else {
                                    SalesLocation = list.get(0).getAddressLine(0);
                                    SalesLocation= SalesLocation.replace("대한민국 ","");

                                    pushid.child("addressLine").setValue(SalesLocation);
                                    pushid.child("locationlat").setValue(String.valueOf(d1));
                                    pushid.child("locationlon").setValue(String.valueOf(d2));
                                    secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("recentAddress").setValue(SalesLocation);
                                    secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("recentLat").setValue(String.valueOf(d1));
                                    secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("recentLon").setValue(String.valueOf(d2));
                                }
                            }
                            addMarker(d1, d2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //종료버튼 눌렀을때
                        secondDatabase.getReference().child("trucks").child("info").child(user.getUid()).child("onBusiness").setValue(false);
                        btnSituation = false;
                        BtnSalesSituation.setText("스위치 OFF");
                        BtnSalesSituation.setBackgroundColor(Color.DKGRAY);
                        pushid.child("onBusiness").setValue(btnSituation);  //-> false 세팅(더이상 검색 할수없음)
                        pushid.child("endtime").setValue(formatDate2);
                        SalesStartTime = null;
                        SalesLocation = null;
                        if (lastLocationMarker != null)
                            lastLocationMarker.remove();
                    }
                }
            }
        });

        ivSalesStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SalesStartTime != null) {
                    alertDialog("시작 시각", SalesStartTime);
                }
            }
        });

        ivSalesLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SalesLocation != null) {
                    alertDialog("영업 위치", SalesLocation);
                }
            }
        });
        return rootView;
    }

    private void alertDialog(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setTitle(title);
        alert.setMessage(message);
        alert.show();
    }

    private void mapSync() {
        mapFragment.getMapAsync(this);
    }

    private void addMarker(double d1, double d2) {
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d1, d2), 17));
        lastLocationMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(d1, d2)).title("영업위치"));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.setOnMyLocationButtonClickListener(this);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.setMyLocationEnabled(true);
        System.out.println("맵싱크 실행");
        //영업중 정보가 없을경우 최근위치로 카메라 이동.
        if (!isBusinessCheck()) {
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getCurrentLocation();
        return false;
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        OnCompleteListener<Location> completeListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    latitude = task.getResult().getLatitude();
                    longitude = task.getResult().getLongitude();
                    mapSync();
                } else {
                }
            }
        };

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(getActivity(), completeListener);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
                mapSync();
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "위치서비스 기능을 사용하도록 설정해주세요.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //테드에 연결된 퍼미션
    private class MyPermissionListener implements PermissionListener {
        private LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        @Override
        public void onPermissionGranted() {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled || !isGPSEnabled) {
                showSettingsAlert();
            } else {
                getCurrentLocation();

            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getContext(), "다음과 같은 추가적인 권한이 필요합니다 : " + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isBusinessCheck() {
        //사업자의 잔여정보가 남아있는지 확인하여 있을경우 데이터 셋팅. (강제종료시 onBusiness가 계속 true상태로 남아있음.)
        mDatabase.child("trucks").child("salessituation").child(user.getUid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (((Boolean) snapshot.child("onBusiness").getValue()) == true) {
                        pushid = snapshot.getRef();
                        btnSituation = true;
                        BtnSalesSituation.setText("스위치 ON");
                        BtnSalesSituation.setBackgroundColor(Color.BLUE);
                        SalesStartTime = snapshot.child("starttime").getValue().toString();
                        SalesLocation = snapshot.child("addressLine").getValue().toString();

                        double lat, lon;
                        lat = Double.parseDouble(snapshot.child("locationlat").getValue().toString());
                        lon = Double.parseDouble(snapshot.child("locationlon").getValue().toString());

                        latitude = lat;
                        longitude = lon;
                        addMarker(latitude, longitude);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return btnSituation;
    }
}

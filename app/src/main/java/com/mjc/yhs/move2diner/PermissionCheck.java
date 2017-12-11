package com.mjc.yhs.move2diner;

import android.content.Context;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;

import java.util.ArrayList;

/**
 * Created by Kang on 2017-12-05.
 */

public class PermissionCheck implements PermissionListener {
    /*
        100 : ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
        200 : WRITE_EXTERNAL_STORAGE, CAMERA
     */
    int Permission_CODE;
    Context c;
    PermissionCallback permissionCallback;

    public PermissionCheck(Context context, int permission_CODE) {
        c = context;
        Permission_CODE = permission_CODE;
    }

    @Override
    public void onPermissionGranted() {
        switch (Permission_CODE){
            default:
                doWork();
        }
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        Toast.makeText(c, "권한을 얻지 못해 일부 기능이 작동하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show();
    }

    public interface PermissionCallback{
        void callbackMethod();
    }

    void registerCallback(PermissionCallback callback){
        permissionCallback = callback;
    }

    void doWork(){
        permissionCallback.callbackMethod();
    }
}

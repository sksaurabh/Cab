package com.bhash.cabtask;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashScreenActivity extends BaseActivity {

    private static final String TAG = SplashScreenActivity.class.getName();

    //handler for splash screen

    private static final int PERMISSION_REQUEST_FINE_LOCATION = ApplicationConstants.PERMISSION_REQUEST_FINE_LOCATION;
    LocationManager manager;

    //handler for splashscreen
    Handler mHandlerTime;
    Runnable mRunnableTimeOut;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandlerTime=new Handler();
        mRunnableTimeOut = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, DashBoardActivity.class);
                startActivity(intent);

            }
        };
    }

    @Override
    protected void initializeView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    private void gotoEnableLocation() {
        if (checkFineLocation()) {


        }


    }

    private boolean checkFineLocation() {
        boolean isPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
        return isPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoEnableLocation();
                } else {
                    gotoEnableLocation();
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        mHandlerTime.removeCallbacks(mRunnableTimeOut);
        super.onPause();

    }

    @Override
    protected void onResume() {
        mHandlerTime.postDelayed(mRunnableTimeOut, ApplicationConstants.SPLASH_TIME_OUT);
        super.onResume();
    }
}
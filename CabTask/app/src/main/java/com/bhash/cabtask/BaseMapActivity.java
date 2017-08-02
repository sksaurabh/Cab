package com.bhash.cabtask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public abstract class BaseMapActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = BaseMapActivity.class.getSimpleName();
    protected Boolean isGoogleMapReady = false;
    protected GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    protected Marker mPickupLocationMarker;
    protected Marker mDropLocationMarker;
    protected Marker mDriverMarker;
    protected LocationRequest mLocationRequest;
    protected MarkerOptions mPickupMarkerOptions;
    protected MarkerOptions mDropMarkerOptions;
    protected boolean isLocationPermissionGranted;
    protected ArrayList<LatLng> mPolylinePoints;
    private Boolean isGoogleApiClientConnected = false;
    private LatLng mCurrentLatLng;
    protected static final LatLngBounds BOUNDS_BANGALORE = new LatLngBounds(new LatLng(12.864162, 77.438610), new LatLng(13.139807, 77.711895));
    protected static final AutocompleteFilter LOCATION_SEARCH_FILTER = new AutocompleteFilter.Builder()
            .setCountry("IN")
            .setTypeFilter(Place.TYPE_SUBLOCALITY_LEVEL_2)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isLocationPermissionGranted = checkLocationPermission();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isGoogleMapReady = true;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isLocationPermissionGranted) {
            if (buildGoogleApiClient() != null) {

                mGoogleApiClient.connect();
                //noinspection MissingPermission
                mMap.setMyLocationEnabled(true);
                mMap.setIndoorEnabled(false);
                mMap.setBuildingsEnabled(true);
                //mMap.getUiSettings().setZoomControlsEnabled(false);
                //moveMylocationButton();

                mMap.setLatLngBoundsForCameraTarget(BOUNDS_BANGALORE);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                mPickupMarkerOptions = new MarkerOptions();
                mPickupMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(ApplicationConstants.HUE_PICKUP_PIN));

                mDropMarkerOptions = new MarkerOptions();
                mDropMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(ApplicationConstants.HUE_DROP_PIN));
            }
        }
    }

    protected void onGoogleMapReady(GoogleMap googleMap) {

    }

    protected synchronized GoogleApiClient buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        return mGoogleApiClient;
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        /*mLocationRequest.setInterval(ApplicationConstants.LOCATION_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(ApplicationConstants.FAST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

        if (checkLocationPermission() && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    private void startFusedLocation() {

    }

    @Override
    public void onConnectionSuspended(int i) {
        AlertUtils.showDebugToast("onConnectionSuspended", this);
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLatLng = latLng;
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ApplicationConstants.DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions()
                .position(mCurrentLatLng));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            //addMarkerOnMap(latLng, true);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, connectionResult.getErrorMessage());
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertUtils.showDebugToast("Location Request Is Necessary For The App!", getApplicationContext());

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ApplicationConstants.MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ApplicationConstants.MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ApplicationConstants.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient().connect();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    AlertUtils.showDebugToast("Location Permission Required.", this);
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    // call bak for navigation icon on appbar.
    public void onNavigationIconPressed(View view) {

    }

    //------------------------hiding the keypad ontouch of outside----------------------------------------

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    //-------------------------------------------setup_Bar--------------------------------------------

    //Add toolbar as a application bar by default navigation icon is back arrow to get default behaviour navigationIconId should be zero
    // For no navigation icon send navigationIconId as null.
    public void setupAppBar(int toolBarId, String title, Integer navigationIconId) {
        Toolbar toolbar = (Toolbar) findViewById(toolBarId);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title_textview);
            if (mTitle != null) {
                mTitle.setText(title);
            } else {
                getSupportActionBar().setTitle(title);
            }
            if (navigationIconId != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                if (navigationIconId == 0) {
                    toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    toolbar.setNavigationIcon(navigationIconId);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onNavigationIconPressed(view);
                        }
                    });
                }
            }
        }
    }

    protected void addMarkerOnMap(Place place, Boolean isPickup) {
        if (isPickup) {
            mPickupMarkerOptions.position(place.getLatLng());
            if (mPickupLocationMarker != null)
                mPickupLocationMarker.remove();
            mPickupLocationMarker = mMap.addMarker(mPickupMarkerOptions);
        } else {
            mDropMarkerOptions.position(place.getLatLng());
            if (mDropLocationMarker != null)
                mDropLocationMarker.remove();
            mDropLocationMarker = mMap.addMarker(mDropMarkerOptions);
        }

        animateCameraToMarkerWithZoom(place.getLatLng());
    }

    protected LatLng getCurrentLocation() {
        return mCurrentLatLng;
    }


    protected void addSourceMarkerOnMap(LatLng latLng) {
        mPickupMarkerOptions.position(latLng);
        if (mPickupLocationMarker != null) {
            mPickupLocationMarker.remove();
        }
        mPickupLocationMarker = mMap.addMarker(mPickupMarkerOptions);

        animateCameraToMarkerWithZoom(latLng);
    }

    protected void addDestinationMarkerOnMap(LatLng latLng) {
        mDropMarkerOptions.position(latLng);
        if (mDropLocationMarker != null) {
            mDropLocationMarker.remove();
        }
        mDropLocationMarker = mMap.addMarker(mDropMarkerOptions);
        animateCameraToMarkerWithZoom(latLng);
    }

    private void animateCameraToMarkerWithZoom(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
        if (mMap != null) {
            mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);
        }
    }

    protected void animateCameraToPickupAndDropMarker() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Boolean animate = false;
        if (mPickupLocationMarker != null) {
            builder.include(mPickupLocationMarker.getPosition());
            animate = true;
        }
        if (animate && mDropLocationMarker != null) {
            builder.include(mDropLocationMarker.getPosition());
        }
        if (animate) {
            LatLngBounds bounds = builder.build();
            int padding = 30; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }
}

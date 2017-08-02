package com.bhash.cabtask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DashBoardActivity extends BaseMapActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    RecyclerView mRecyclerView;
    private VehicleSelectionRecyclerAdapter mVehicleSelectionRecyclerAdapter;

    private RelativeLayout editProfileLinearLayout;
    private TextView mPassengerNameTextView;
    private ImageView mPassengerProfileCircleImageView;

    private String mPickupAddress;
    private String mDropAddress;
    private LatLng mPickupLatLng;
    private LatLng mDropLatLng;
    private boolean mWithoutCache = false;
    private boolean mHasPickupSetOnce = false;
    private boolean mPickupLocationClicked;
    private int mVehicleTypeSelected = -1;
    Button mDashboardBooknowButton;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    LinearLayout mBooknowLinearlayout;
    TextView mFromLocationTextView;
    TextView mDropLocationTextView;
    private Handler mHandler;
    private boolean hasReceivedNearestCars = false;
    private Runnable mGetNearestCarsRunnable;
    private boolean isMapReady;
    private String mProfileImageURL;
    private int mVehicleTypeSectected = -1;
    private List<VehicleSelectionAdapterModel> mVehicleSelectionAdapterModelList;
    private VehicleSelectionRecyclerAdapter vehicleSelectionRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        initializeView();
    }

    @Override
    public void initializeView() {

        setupAppBar(R.id.dash_board_toolbar, getResources().getString(R.string.Dashboard), 0);
        toolbar = (Toolbar) findViewById(R.id.dash_board_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.dash_board_car_selection_recyclerView);
        mDashboardBooknowButton = (Button) findViewById(R.id.al_dashboard_booknow);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mBooknowLinearlayout = (LinearLayout) findViewById(R.id.al_dashbord_booknow_linearlayout);
        mFromLocationTextView = (TextView) findViewById(R.id.pickup_location_text_view);
        mDropLocationTextView = (TextView) findViewById(R.id.drop_location_text_view);
        initializeDrawer();
        initializeMap();
        vehicleSelectionRecyclerAdapter = new VehicleSelectionRecyclerAdapter(getVehicleList(), this, new VehicleSelectionRecyclerAdapter.VehicleClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mVehicleTypeSectected = position;
            }
        });
        // Vehicle list RecyclerView
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this, android.support.v7.widget.LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(vehicleSelectionRecyclerAdapter);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        getData();

    }

    private void initializeMap() {
        // Initialize Map & Markers
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dash_board_map);
        mapFragment.getMapAsync(this);

        mFromLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPickupLocationClicked = true;
                openPlaceSearchFragment();
            }
        });

        mDropLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPickupLocationClicked = false;
                openPlaceSearchFragment();
            }
        });
    }

    private void initializeDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        //View header = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                mWithoutCache = true;
                super.onDrawerOpened(drawerView);
            }
        };

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        mPassengerNameTextView = (TextView) headerView.findViewById(R.id.vT_pl_userName);

        mPassengerProfileCircleImageView = (ImageView) headerView.findViewById(R.id.vI_pl_userPic);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        disableNavigationViewScrollbars(navigationView);

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_dummy_menu, getApplicationContext().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    private List<VehicleSelectionAdapterModel> getVehicleList() {
        mVehicleSelectionAdapterModelList = new ArrayList<>();

        VehicleSelectionAdapterModel model1 = new VehicleSelectionAdapterModel();
        model1.setVehicleImageId(R.drawable.car_hatchback);
        model1.setVehicleName("Hatchback");
        mVehicleSelectionAdapterModelList.add(model1);

        VehicleSelectionAdapterModel model2 = new VehicleSelectionAdapterModel();
        model2.setVehicleImageId(R.drawable.car_sedan);
        model2.setVehicleName("Sedan");
        mVehicleSelectionAdapterModelList.add(model2);

        VehicleSelectionAdapterModel model3 = new VehicleSelectionAdapterModel();
        model3.setVehicleImageId(R.drawable.car_suv);
        model3.setVehicleName("SUV");
        mVehicleSelectionAdapterModelList.add(model3);

        VehicleSelectionAdapterModel model4 = new VehicleSelectionAdapterModel();
        model4.setVehicleImageId(R.drawable.car_outstation);
        model4.setVehicleName("Outstation");
        mVehicleSelectionAdapterModelList.add(model4);

        VehicleSelectionAdapterModel model5 = new VehicleSelectionAdapterModel();
        model5.setVehicleImageId(R.drawable.car_sedan);
        model5.setVehicleName("Sedan");
        mVehicleSelectionAdapterModelList.add(model5);

        VehicleSelectionAdapterModel model6 = new VehicleSelectionAdapterModel();
        model6.setVehicleImageId(R.drawable.car_suv);
        model6.setVehicleName("SUV");
        mVehicleSelectionAdapterModelList.add(model6);

        VehicleSelectionAdapterModel model7 = new VehicleSelectionAdapterModel();
        model7.setVehicleImageId(R.drawable.car_outstation);
        model7.setVehicleName("Outstation");
        mVehicleSelectionAdapterModelList.add(model7);

        VehicleSelectionAdapterModel model8 = new VehicleSelectionAdapterModel();
        model8.setVehicleImageId(R.drawable.car_sedan);
        model8.setVehicleName("Sedan");
        mVehicleSelectionAdapterModelList.add(model8);

        VehicleSelectionAdapterModel model9 = new VehicleSelectionAdapterModel();
        model9.setVehicleImageId(R.drawable.car_suv);
        model9.setVehicleName("SUV");
        mVehicleSelectionAdapterModelList.add(model9);

        return mVehicleSelectionAdapterModelList;
    }

    public void getData() {
        mDashboardBooknowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupForAlert();
            }
        });
    }

    private void openPlaceSearchFragment() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(LOCATION_SEARCH_FILTER)
                            .setBoundsBias(BOUNDS_BANGALORE)
                            .build(DashBoardActivity.this);

            startActivityForResult(intent, ApplicationConstants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            AlertUtils.showInfoDialog(DashBoardActivity.this, getString(R.string.app_name), getString(R.string.alert_autocomplete_error));
        }
    }


    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.nav_dashboard:
                break;
            case R.id.nav_payments:

                break;
            case R.id.nav_tripHistory:

                break;

            case R.id.nav_settings:

                break;
            case R.id.nav_invites:
                shareLinkButtonClicked();
                break;
            case R.id.nav_faq:

                break;
            case R.id.nav_aboutus:

                break;
            case R.id.nav_logout:
                finish();
                break;
            default:
        }

        if (intent != null) {
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


        }
    }

    //-------------------------invites code------------------------------------


    public void shareLinkButtonClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_link));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is the text that will be shared");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ApplicationConstants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                if (mPickupLocationClicked) {
                    mFromLocationTextView.setText(place.getName());
                    mPickupAddress = place.getAddress().toString();
                    mPickupLatLng = place.getLatLng();
                } else {
                    mDropLocationTextView.setText(place.getName());
                    mDropAddress = place.getAddress().toString();
                    mDropLatLng = place.getLatLng();
                }
                updateMapWithPath();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                //Status status = PlaceAutocomplete.getStatus(this, data);
                AlertUtils.showInfoDialog(DashBoardActivity.this, getString(R.string.app_name), getString(R.string.alert_autocomplete_error));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation. No Message required.
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        mMap = googleMap;
        isMapReady = true;
        updateMapWithPath();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dash_board_map);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        setPickupMarker();
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        setPickupMarker();
    }

    private void setPickupMarker() {
        //Do it only when its the first time
        if (!mHasPickupSetOnce) {
            mHasPickupSetOnce = true;
            LatLng currentLocation = getCurrentLocation();
            if (currentLocation != null) {
                mPickupLatLng = new LatLng(currentLocation.latitude, currentLocation.longitude);
                String address = MapUtil.getAddressFromLatLng(this, mPickupLatLng);
                mPickupAddress = address;
                mFromLocationTextView.setText(address);
            }
        }
    }
    private void updateMapWithPath() {
        if (isMapReady && mPickupLatLng != null) {
            addSourceMarkerOnMap(mPickupLatLng);
        }
        if (isMapReady && mDropLatLng != null) {
            addDestinationMarkerOnMap(mDropLatLng);
        }

        if (isMapReady && mPickupLatLng != null && mDropLatLng != null) {
            mMap.clear();
            //drawPathOnBetweenPoints(mPickupLatLng, mDropLatLng);
            animateCameraToPickupAndDropMarker();
        }
    }
    public void popupForAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Confirmation..");
        builder1.setMessage("Do you want to book the ride..?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(),"Your cab is booked sucessfully",Toast.LENGTH_LONG).show();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}

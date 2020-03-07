package com.anasberbar.coupon.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.adapter.RestaurantListAdapter;
import com.anasberbar.coupon.models.Common;

import com.anasberbar.coupon.models.Restaurant;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;


public class RestaurantActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private ArrayList<Restaurant> mRestaurants=new ArrayList<>();
    private ArrayList<Restaurant> mTempRestaurantList;
    private ListView _lstRestaurant;
    private RestaurantListAdapter mAdapter;
    private Menu theMenu;
    private Context mContext;

    double gcLatitude;
    double gcLongitude;
    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        mContext=this;

        initForGetLocation();

        addEventListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getRestaurantData();
        showRestaurantList();

        //get location
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        //remove cursor
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void addEventListener(){
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.restaurant);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backbutton);
        toolbar.setNavigationOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            }
        );

        findViewById(R.id.imgSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editSearch=(EditText)findViewById(R.id.editSearch);
                String pattern=editSearch.getText().toString().trim();
                searchRestaurant(pattern);
            }
        });

        findViewById(R.id.imgSearch_Close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTempRestaurantList=new ArrayList<Restaurant>(mRestaurants);
                findViewById(R.id.imgSearch_Close).setVisibility(View.INVISIBLE);
                showRestaurantList();
            }
        });

        _lstRestaurant = findViewById(R.id.lstRestaurants);
        _lstRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Restaurant theRestaurant=mTempRestaurantList.get(position);
            //Toast.makeText(getBaseContext(), theRestaurant.getmName(), Toast.LENGTH_LONG).show();

            Intent intent=new Intent(getBaseContext(),IndividualRestaurantActivity.class)
                    .putExtra("restaurantId",mTempRestaurantList.get(position).getmId());
            startActivity(intent);
            }
        });
    }
    //toolbar============
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        theMenu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSort_name:
                theMenu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sort_name_unselected));
                theMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sort_position_unselected));

                Collections.sort(mTempRestaurantList, Restaurant.Comparators.NAME);
                mAdapter = new RestaurantListAdapter(this, mTempRestaurantList);
                _lstRestaurant.setAdapter(mAdapter);
                break;
            case R.id.mnuSort_position:
                theMenu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sort_name));
                theMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_sort_position));

                Collections.sort(mTempRestaurantList, Restaurant.Comparators.DISTANCE);
                mAdapter = new RestaurantListAdapter(this, mTempRestaurantList);
                _lstRestaurant.setAdapter(mAdapter);

                break;
            default:
                break;
        }
        return true;
    }


    private void getRestaurantData(){
        mRestaurants= Common.getInstance().getmRestaurants();
        mTempRestaurantList=new ArrayList<Restaurant>(mRestaurants);
    }

    private void showRestaurantList(){
        mAdapter = new RestaurantListAdapter(this, mTempRestaurantList);
        _lstRestaurant.setAdapter(mAdapter);
    }

    private void searchRestaurant(String pattern){
        mTempRestaurantList.clear();
        if(pattern.isEmpty()){
             mTempRestaurantList=new ArrayList<Restaurant>(mRestaurants);
             findViewById(R.id.imgSearch_Close).setVisibility(View.INVISIBLE);
        }
        else{
            for(Restaurant theRestaurant:mRestaurants){
                if(theRestaurant.getmName().toLowerCase().contains(pattern.toLowerCase())){
                    mTempRestaurantList.add(theRestaurant);
                }
            }
            findViewById(R.id.imgSearch_Close).setVisibility(View.VISIBLE);
        }
        showRestaurantList();
    }

    private void initForGetLocation(){
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Common.getInstance().showAlert(mContext,"Location Setting","You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            gcLatitude=location.getLatitude();
            gcLongitude=location.getLongitude();

            Log.d("pos", "Latitude : " + gcLatitude + "\nLongitude : " + gcLongitude);

            //float[] distances = new float[1];
            //Location.distanceBetween(gcLatitude, gcLongitude,32.91138340626445, 44.38229072761436,distances);
            //Log.d("Distance: ",String.valueOf(distances[0]));

            Common.getInstance().updateDistance(gcLatitude,gcLongitude);
            mTempRestaurantList=new ArrayList<>(mRestaurants);
            Collections.sort(mTempRestaurantList, Restaurant.Comparators.DISTANCE);
            mAdapter = new RestaurantListAdapter(this, mTempRestaurantList);
            _lstRestaurant.setAdapter(mAdapter);
        }

        //startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //add notification
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //add notification
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            gcLatitude=location.getLatitude();
            gcLongitude=location.getLongitude();

            Log.d("pos changed", "Latitude : " + gcLatitude + "\nLongitude : " + gcLongitude);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(RestaurantActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

}

package com.hemant.ecofoodtrackerapp.donor.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityDonorMainBinding;
import com.hemant.ecofoodtrackerapp.donor.ui.fragments.DonorChatsFragment;
import com.hemant.ecofoodtrackerapp.donor.ui.fragments.DonorHistoryFragment;
import com.hemant.ecofoodtrackerapp.donor.ui.fragments.DonorHomeFragment;
import com.hemant.ecofoodtrackerapp.donor.ui.fragments.DonorProfileFragment;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

public class DonorMainActivity extends AppCompatActivity {

    ActivityDonorMainBinding binding;
    private static final int LOCATION_CODE = 100;
    FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDonorMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.donorBottomNav.setOnNavigationItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.donorHomeMenu) {
                loadFragment(new DonorHomeFragment(), false);
            } else if (id == R.id.donorChatMenu) {
                loadFragment(new DonorChatsFragment(), false);
            } else if (id == R.id.donorHistoryMenu) {
                loadFragment(new DonorHistoryFragment(), false);
            } else if (id == R.id.donorProfileMenu) {
                loadFragment(new DonorProfileFragment(), false);
            }
            return true;
        });

        loadFragment(new DonorHomeFragment(), false);
    }

    public void loadFragment(Fragment fragment, boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag) {
            ft.add(R.id.fragmentContainerView, fragment);
        } else {
            ft.replace(R.id.fragmentContainerView, fragment);
        }
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentLocations();
    }

    private void getCurrentLocations() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            getCurrentLocations();
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location == null) {
                    //if not found location
                    requestNewLocationData();
                } else {
                    LocationModel currentLocation = new LocationModel(location.getLatitude(), location.getLongitude());
                    FirebaseUtil.setDonorCurrentLocation(DonorMainActivity.this, currentLocation);
                }
            }
        });
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // on FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            LocationModel currentLocation = null;
            if (mLastLocation != null) {
                currentLocation = new LocationModel(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                FirebaseUtil.setDonorCurrentLocation(DonorMainActivity.this, currentLocation);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
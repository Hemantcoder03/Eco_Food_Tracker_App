package com.hemant.ecofoodtrackerapp.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityMainBinding;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.fragments.CartFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.ChatsFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.HomeFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.MapFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.ProfileFragment;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ImageButton openCloseNavBtn;
    DrawerLayout drawerLayout;
    ActivityMainBinding binding;
    TextView toolbarTitle;
    FirebaseAuth mAuth;
    SharedPreferences sharedPref;
    private static final int LOCATION_CODE = 101;

    FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("My_Pref", 0);

        openCloseNavBtn = findViewById(R.id.openCloseNavBtn);
        bottomNav = findViewById(R.id.bottomNav);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        setUserName();

        //set side navigation bar item selected listener
        binding.sideNav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.map) {
                Toast.makeText(MainActivity.this, "map", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.chat) {
                Toast.makeText(MainActivity.this, "chat", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.profile) {
                Toast.makeText(MainActivity.this, "profile", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.helpSupport) {
                Toast.makeText(MainActivity.this, "help and support", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.theme) {
                Toast.makeText(MainActivity.this, "Theme", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        openCloseNavBtn.setOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //set bottom navigation redirection
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            //get the item id and according to that set the menu of bottom navigation bar
            int id = item.getItemId();

            if (id == R.id.homeBottomBtn) {
                loadFragment(new HomeFragment(), false);
            } else if (id == R.id.mapBottomBtn) {
                loadFragment(new MapFragment(), false);
            } else if (id == R.id.chatBottomBtn) {
                loadFragment(new ChatsFragment(), false);
            } else if (id == R.id.cartBottomBtn) {
                loadFragment(new CartFragment(), false);
            } else if (id == R.id.profileBottomBtn) {
                loadFragment(new ProfileFragment(), false);
            }
            return true;
        });

        //set default fragment as home fragment
        loadFragment(new HomeFragment(), false);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadFragment(Fragment fragment, boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag) {
            ft.add(R.id.frame, fragment);
        } else {
            ft.replace(R.id.frame, fragment);
        }
        ft.commit();

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
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

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
                    FirebaseUtil.setUserCurrentLocation(MainActivity.this, currentLocation);
                }
            }
        });
    }


    private void setUserName() {
        FirebaseUtil.getCurrentUserDetails().get().addOnCompleteListener(v -> {

            if (v.isSuccessful()) {
                UserDataModel user = v.getResult().toObject(UserDataModel.class);
                String userName;
                if (user != null) {
                    userName = user.getUserName();
                    if (userName.length() > 10) {
                        toolbarTitle.setText(getString(R.string.hello) + " " + userName.substring(0, 10) + "...");
                    } else {
                        toolbarTitle.setText(getString(R.string.hello) + " " + userName);
                    }
                } else {
                    AndroidUtil.setToast(this, "Please check your internet connection");
                }
            } else {
                AndroidUtil.setToast(this, "Please check your internet connection");
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                FirebaseUtil.setUserCurrentLocation(MainActivity.this, currentLocation);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

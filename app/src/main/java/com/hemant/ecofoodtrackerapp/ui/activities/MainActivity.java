package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityMainBinding;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.fragments.CartFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.ChatsFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.HomeFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.MapFragment;
import com.hemant.ecofoodtrackerapp.ui.fragments.ProfileFragment;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ImageButton openCloseNavBtn;
    DrawerLayout drawerLayout;
    ActivityMainBinding binding;
    TextView toolbarTitle;
    FirebaseAuth mAuth;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("My_Pref",0);

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

        openCloseNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                else{
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //set bottom navigation redirection
        bottomNav.setOnNavigationItemSelectedListener(item ->{
            //get the item id and according to that set the menu of bottom navigation bar
            int id = item.getItemId();

            if(id == R.id.homeBottomBtn){
                loadFragment(new HomeFragment(), false);
            }
            else if(id == R.id.mapBottomBtn){
                loadFragment(new MapFragment(), false);
            }
            else if(id == R.id.chatBottomBtn){
                loadFragment(new ChatsFragment(), false);
            }
            else if(id == R.id.cartBottomBtn){
                loadFragment(new CartFragment(), false);
            }
            else if(id == R.id.profileBottomBtn){
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

    public void loadFragment(Fragment fragment, boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag) {
            ft.add(R.id.frame, fragment);
        }else {
            ft.replace(R.id.frame,fragment);
        }
        ft.commit();

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setUserName(){
        FirebaseUtil.getCurrentUserDetails().get().addOnCompleteListener(v ->{

            if(v.isSuccessful()){
                UserDataModel user = v.getResult().toObject(UserDataModel.class);
                String userName = Objects.requireNonNull(user).getUserName();
                if(userName.length() > 10){
                    toolbarTitle.setText(getString(R.string.hello)+" "+userName.substring(0,10)+"...");
                }
                else {
                    toolbarTitle.setText(getString(R.string.hello)+" "+userName);
                }
            }
            else{
                AndroidUtil.setToast(this, "Please check your internet connection");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}



/*

package com.hemant.ecofoodtrackerapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hemant.ecofoodtrackerapp.R;;
import com.hemant.ecofoodtrackerapp.adapters.FoodListAdapter;
import com.hemant.ecofoodtrackerapp.models.FoodData;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    public HomeFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase db;
    DatabaseReference ref;
    SearchView searchFood;
    ImageButton searchFilterBtn;
    RecyclerView foodListRV;
    ArrayList<FoodData> foodDataList;
    FoodListAdapter foodListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //get the database reference
//        db = FirebaseDatabase.getInstance();
//        ref = db.getReference();
//
//        searchFood = view.findViewById(R.id.searchFood);
//        searchFilterBtn = view.findViewById(R.id.searchFilterBtn);
//        foodListRV = view.findViewById(R.id.foodListRV);

//        FirebaseRecyclerOptions<FoodData> options =
//                new FirebaseRecyclerOptions.Builder<FoodData>()
//                        .setQuery(ref.child("Food_list"), FoodData.class)
//                        .build();

        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));
        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));
        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));
        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));
        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));
        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));
        foodDataList.add(new FoodData("dljhfjk","gdhgjhfs","gdshgh","ghsgjgdhf","gdhgoishd","gshdhg"));

//        foodListRV.setLayoutManager(new LinearLayoutManager(getActivity().getParent()));
//        foodListAdapter = new FoodListAdapter(foodDataList,getActivity().getParent());
//        foodListRV.setAdapter(foodListAdapter);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
 */



/*


<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="@dimen/_5sdp"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    tools:context=".fragments.HomeFragment">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <androidx.appcompat.widget.SearchView
                android:layout_width="0dp"
                android:layout_height="@dimen/_30sdp"
                android:id="@+id/searchFood"
                android:layout_weight="1"
                app:iconifiedByDefault="false"
                app:queryHint="Search Here"
                android:layout_margin="@dimen/_2sdp"
                android:background="@drawable/search_border"/>

            <ImageButton
                android:layout_width="wrap_content"
                android:layout_height="@dimen/_30sdp"
                android:id="@+id/searchFilterBtn"
                android:padding="@dimen/_5sdp"
                android:background="@color/transparent"
                android:src="@drawable/filter_icon"/>
        </LinearLayout>

        <androidx.recyclerview.widget.RecyclerView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/foodListRV"
            tools:listitem="@layout/food_item"
            android:layout_margin="@dimen/_5sdp"/>
    </LinearLayout>

</FrameLayout>
 */
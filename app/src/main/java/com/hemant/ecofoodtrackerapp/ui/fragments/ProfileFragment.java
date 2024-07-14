package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.FragmentProfileBinding;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.HistoryActivity;
import com.hemant.ecofoodtrackerapp.ui.activities.SettingsActivity;
import com.hemant.ecofoodtrackerapp.ui.activities.UpdateProfileActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

public class ProfileFragment extends Fragment {

    Button logoutBtn;
    FirebaseAuth mAuth;
    FragmentProfileBinding binding;
    View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();

        setUpdatedUserInformation();

        binding.profileEditBtn.setOnClickListener(v -> {

//            SharedPreferences sharedPref = requireActivity().getSharedPreferences("My_Pref",0);
//            SharedPreferences.Editor editor = sharedPref.edit();
//            boolean editable = sharedPref.getBoolean("editable",false);
//
//            if(!editable){
//                binding.profilePhoneInput.setFocusableInTouchMode(true);
//                binding.profileNameInput.setFocusableInTouchMode(true);
//                binding.profileAddressInput.setFocusableInTouchMode(true);
//                binding.profileNameInput.requestFocus();
//                editor.putBoolean("editable",true);
//            }
//            else{
//                binding.profilePhoneInput.setFocusableInTouchMode(false);
//                binding.profilePhoneInput.setFocusable(false);
//                binding.profileNameInput.setFocusableInTouchMode(false);
//                binding.profileNameInput.setFocusable(false);
//                binding.profileAddressInput.setFocusableInTouchMode(false);
//                binding.profileAddressInput.setFocusable(false);
//                editor.putBoolean("editable",false);
//
//                FirebaseUtil.updateCurrentUserDetails(binding.profileNameInput.getText().toString(),
//                        binding.profilePhoneInput.getText().toString(),
//                        binding.profileAddressInput.getText().toString());
//
//                //get updated data
//                setUpdatedUserInformation();
//            }
//            editor.apply();

            Intent intent = new Intent(requireActivity(), UpdateProfileActivity.class);
            intent.putExtra("name",binding.profileNameInput.getText().toString());
            intent.putExtra("phone",binding.profilePhoneInput.getText().toString());
            intent.putExtra("address",binding.profileAddressInput.getText().toString());
            startActivity(intent);
        });

        binding.profileChatBtn.setOnClickListener(v ->{
            //firstly replace the fragment content
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame,new ChatsFragment());
            transaction.commit();

            //then set the bottom navigation selected item
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNav);
            bottomNavigationView.setSelectedItemId(R.id.chatBottomBtn);
        });

        binding.profileHistoryLayout.setOnClickListener(v ->{
            startActivity(new Intent(requireActivity(), HistoryActivity.class));
        });

        binding.profileSettingsLayout.setOnClickListener(v ->{
            startActivity(new Intent(requireActivity(), SettingsActivity.class));
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void setUpdatedUserInformation(){
        //set the updated data
        FirebaseUtil.getCurrentUserDetails().get().addOnCompleteListener(v ->{
                if (v.isSuccessful()) {
                    UserDataModel userDataModel = v.getResult().toObject(UserDataModel.class);
                    if (userDataModel != null) {
                        binding.profileNameInput.setText(userDataModel.getUserName());
                        binding.profileName.setText(userDataModel.getUserName());
                        binding.profileEmail.setText(userDataModel.getUserEmail());
                        binding.profilePhoneInput.setText(userDataModel.getUserPhone());
                        binding.profileAddressInput.setText(userDataModel.getUserAddress());
                    }

                } else {
                    AndroidUtil.setToast(requireActivity(), "Please check your internet connection");
                }
        });
    }
}
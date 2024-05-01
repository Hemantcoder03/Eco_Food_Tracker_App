package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorProfileBinding;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

public class DonorProfileFragment extends Fragment {

    FragmentDonorProfileBinding binding;
    View view;

    public DonorProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDonorProfileBinding.inflate(inflater,container,false);
        view = binding.getRoot();

        setUpdatedDonorInformation();

        binding.donorProfileEditBtn.setOnClickListener(v -> {

            SharedPreferences sharedPref = requireActivity().getSharedPreferences("My_Pref",0);
            SharedPreferences.Editor editor = sharedPref.edit();
            boolean editable = sharedPref.getBoolean("editable",false);

            if(!editable){
                binding.donorProfileNameInput.setFocusableInTouchMode(true);
                binding.donorProfilePhoneInput.setFocusableInTouchMode(true);
                binding.donorProfileAddressInput.setFocusableInTouchMode(true);
                binding.donorProfileNameInput.requestFocus();
                editor.putBoolean("editable",true);
            }
            else{
                binding.donorProfilePhoneInput.setFocusableInTouchMode(false);
                binding.donorProfilePhoneInput.setFocusable(false);
                binding.donorProfileNameInput.setFocusableInTouchMode(false);
                binding.donorProfileNameInput.setFocusable(false);
                binding.donorProfileAddressInput.setFocusableInTouchMode(false);
                binding.donorProfileAddressInput.setFocusable(false);
                editor.putBoolean("editable",false);

                FirebaseUtil.updateCurrentDonorDetails(binding.donorProfileNameInput.getText().toString(),
                        binding.donorProfilePhoneInput.getText().toString(),
                        binding.donorProfileAddressInput.getText().toString(),v);

                //get updated data
                setUpdatedDonorInformation();
            }
            editor.apply();
        });

        binding.donorProfileChatBtn.setOnClickListener(v ->{
            //firstly replace the fragment content
            replaceFragment(new DonorChatsFragment());

            //then set the bottom navigation selected item
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.donorBottomNav);
            bottomNavigationView.setSelectedItemId(R.id.donorChatMenu);
        });

        binding.donorProfileHistoryBtn.setOnClickListener(v ->{
            //firstly replace the fragment content
            replaceFragment(new DonorHistoryFragment());

            //then set the bottom navigation selected item
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.donorBottomNav);
            bottomNavigationView.setSelectedItemId(R.id.donorHistoryMenu);
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void setUpdatedDonorInformation(){
        //set the updated data
        FirebaseUtil.getCurrentDonorDetails().get().addOnCompleteListener(task->{
                if (task.isSuccessful()) {
                    UserDataModel userDataModel = task.getResult().toObject(UserDataModel.class);
                    if (userDataModel != null) {
                        binding.donorProfileNameInput.setText(userDataModel.getUserName());
                        binding.donorProfileName.setText(userDataModel.getUserName());
                        binding.donorProfileEmail.setText(userDataModel.getUserEmail());
                        binding.donorProfilePhoneInput.setText(userDataModel.getUserPhone());
                        binding.donorProfileAddressInput.setText(userDataModel.getUserAddress());
                    }
                    else{
                        Log.d("checkError","data not found");
                    }
                } else {
                    AndroidUtil.setToast(requireActivity().getApplicationContext(), "Please check your internet connection");
                }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView,fragment);
        transaction.commit();
    }
}
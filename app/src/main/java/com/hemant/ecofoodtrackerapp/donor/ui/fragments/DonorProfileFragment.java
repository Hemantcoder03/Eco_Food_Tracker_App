package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorProfileBinding;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.HistoryActivity;
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
//            NavController navController = Navigation.findNavController(requireView());
//            navController.navigate(R.id.action_profileFragment_to_chatsFragment);

//            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_chatsFragment);
            requireActivity().finish();
        });

        binding.donorProfileHistoryBtn.setOnClickListener(v ->{
            startActivity(new Intent(requireActivity(), HistoryActivity.class));
        });


        // Inflate the layout for this fragment
        return view;
    }
    public void setUpdatedDonorInformation(){
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
                    AndroidUtil.setToast(requireActivity(), "Please check your internet connection");
                }
        });
    }
}
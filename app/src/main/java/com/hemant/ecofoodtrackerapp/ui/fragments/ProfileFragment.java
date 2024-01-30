package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.FragmentProfileBinding;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
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

//        logoutBtn = view.findViewById(R.id.logoutBtn);
//
//        mAuth = FirebaseAuth.getInstance();
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                Intent i = new Intent(getActivity(), RegisterActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//            }
//        });

        setUpdatedUserInformation();

        binding.profileEditBtn.setOnClickListener(v -> {

            SharedPreferences sharedPref = requireActivity().getSharedPreferences("My_Pref",0);
            SharedPreferences.Editor editor = sharedPref.edit();
            boolean editable = sharedPref.getBoolean("editable",false);

            if(!editable){
                binding.profilePhoneInput.setFocusableInTouchMode(true);
                binding.profileNameInput.setFocusableInTouchMode(true);
                binding.profileAddressInput.setFocusableInTouchMode(true);
                binding.profileNameInput.requestFocus();
                editor.putBoolean("editable",true);
            }
            else{
                binding.profilePhoneInput.setFocusableInTouchMode(false);
                binding.profilePhoneInput.setFocusable(false);
                binding.profileNameInput.setFocusableInTouchMode(false);
                binding.profileNameInput.setFocusable(false);
                binding.profileAddressInput.setFocusableInTouchMode(false);
                binding.profileAddressInput.setFocusable(false);
                editor.putBoolean("editable",false);

                FirebaseUtil.updateCurrentUserDetails(binding.profileNameInput.getText().toString(),
                        binding.profilePhoneInput.getText().toString(),
                        binding.profileAddressInput.getText().toString());

                //get updated data
                setUpdatedUserInformation();
            }
            editor.apply();
        });

        binding.profileChatBtn.setOnClickListener(v ->{
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_chatsFragment);
            requireActivity().finish();
        });



        // Inflate the layout for this fragment
        return view;
    }

    public void setUpdatedUserInformation(){
        //set the updated data
        FirebaseUtil.getCurrentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserDataModel userDataModel = task.getResult().toObject(UserDataModel.class);
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
            }
        });
    }
}
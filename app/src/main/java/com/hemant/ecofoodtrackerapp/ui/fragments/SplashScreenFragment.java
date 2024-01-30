package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.ui.activities.MainActivity;

public class SplashScreenFragment extends Fragment {

    View view;

    public SplashScreenFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        new Handler().postDelayed(() -> {

            //current user check if already authenticated then send to main page
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {

                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            }
            else{
                Navigation.findNavController(view).navigate(R.id.action_splashScreenFragment_to_loginFragment);
            }

//            startActivity(new Intent(requireActivity(), RegisterActivity.class));
//            requireActivity().finish();

        },3000);

        // Inflate the layout for this fragment
        return view;
    }
}
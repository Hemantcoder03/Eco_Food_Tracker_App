package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hemant.ecofoodtrackerapp.R;
public class DonorForgotPasswordFragment extends Fragment {

    public DonorForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donor_forgot_password, container, false);
    }
}
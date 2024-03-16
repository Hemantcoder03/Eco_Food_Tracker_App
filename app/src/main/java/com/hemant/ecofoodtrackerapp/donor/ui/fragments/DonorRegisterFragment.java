package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorRegisterBinding;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorMainActivity;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.regex.Pattern;

public class DonorRegisterFragment extends Fragment {

    private static final int REQ_ONE_TAP = 101;
    FirebaseAuth mAuth;     //firebase auth object which can used to check authentication
    GoogleSignInClient googleClient;
    GoogleSignInOptions gso;
    String donorEmail, donorPassword, donorConfirmPassword;
    FirebaseFirestore db;
    SharedPreferences sharedPref;
    FragmentDonorRegisterBinding binding;

    public DonorRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDonorRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPref = requireActivity().getSharedPreferences("My_Pref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //google authentication
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.donorRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.donorRegisterLoadingBar.setVisibility(View.VISIBLE);
                binding.donorRegisterLoadingBar.playAnimation();
                donorEmail = String.valueOf(binding.donorRegisterEmail.getText());
                donorPassword = String.valueOf(binding.donorRegisterPassword.getText());
                donorConfirmPassword = String.valueOf(binding.donorRegisterConPassword.getText());
                if (donorEmail.isEmpty() || donorPassword.isEmpty()) {
                    hideLoadingBar();
                    AndroidUtil.setToast(requireActivity(), "Enter valid Details");
                    if (donorEmail.isEmpty())
                        binding.donorRegisterEmail.setError("Please enter email");
                    else if (donorPassword.isEmpty())
                        binding.donorRegisterPassword.setError("Please enter password");
                    else if (donorConfirmPassword.isEmpty())
                        binding.donorRegisterConPassword.setError("Please enter confirm password");
                } else if (!isValidEmail(donorEmail)) {
                    hideLoadingBar();
                    binding.donorRegisterEmail.setError("Please enter valid email\n E.g. abc@gmail.com");
                } else if (!isValidPassword(donorPassword)) {
                    hideLoadingBar();
                    binding.donorRegisterPassword.setError("Please enter valid password\n E.g. Abc@123");
                } else if (!donorPassword.equals(donorConfirmPassword)) {
                    hideLoadingBar();
                    binding.donorRegisterConPassword.setError("Password not matching");
                } else {
                    mAuth.createUserWithEmailAndPassword(donorEmail, donorPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideLoadingBar();

                                        //save to firebase
                                        //set the user as default name
                                        saveToDB("User", donorEmail);

                                        AndroidUtil.setToast(requireActivity(), "Registration Successfully");
                                        Intent i = new Intent(requireActivity(), DonorMainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        requireActivity().finish();
                                    } else {
                                        hideLoadingBar();
                                        AndroidUtil.setToast(requireActivity(), "User Already Registered");
                                    }
                                }
                            });
                }
            }
        });

        binding.donorRegisterGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.donorRegisterLoadingBar.setVisibility(View.VISIBLE);
                signUp();
            }
        });

        binding.donorRegisterAlreadyAccText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(view).navigate(R.id.action_donorRegisterFragment_to_donorLoginFragment);
//                startActivity(new Intent(requireActivity(), LoginActivity.class));
//                requireActivity();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void signUp() {
        hideLoadingBar();
        Intent intent = googleClient.getSignInIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQ_ONE_TAP);
    }

    @Override
    public void onStart() {
        super.onStart();

        //current user check if already authenticated then send to main page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            Intent i = new Intent(requireActivity(), DonorMainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            binding.donorRegisterLoadingBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception e) {
                hideLoadingBar();
                AndroidUtil.setToast(requireActivity(), "Something went wrong");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideLoadingBar();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //save to firebase
                            saveToDB(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail());
                            hideLoadingBar();
                            Intent i = new Intent(requireActivity(), DonorMainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            AndroidUtil.setToast(requireActivity(), "Registration successfully");
                            startActivity(i);
                            requireActivity().finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            hideLoadingBar();
                            AndroidUtil.setToast(requireActivity(), "Registration failed");
                        }
                    }
                });
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%^&*+=~]).{8,}");
        return pattern.matcher(password).matches();
    }

    private Boolean isValidEmail(String email) {
        //It used default email address validation
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveToDB(String name, String email) {

        sharedPref = requireActivity().getSharedPreferences("My_Pref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("donorEmail", email);
        editor.putString("donorName", name);
        editor.apply();

        FirebaseUtil.setCurrentDonorDetails(new UserDataModel(name, email, FirebaseUtil.getCurrentUserId(), "", "", "Donor", Timestamp.now(),new LocationModel(),""));
    }

    private void hideLoadingBar() {
        binding.donorRegisterLoadingBar.setVisibility(View.GONE);
        binding.donorRegisterLoadingBar.setImageDrawable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
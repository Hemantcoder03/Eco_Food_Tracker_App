package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.content.Intent;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorLoginBinding;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorMainActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

import java.util.Objects;
import java.util.regex.Pattern;

public class DonorLoginFragment extends Fragment {

    FragmentDonorLoginBinding binding;
    View view;
    private static final int REQ_ONE_TAP = 101;
    FirebaseAuth mAuth;     //firebase auth object which can used to check authentication
    GoogleSignInClient googleClient;
    GoogleSignInOptions gso;
    String donorEmail, donorPassword;

    public DonorLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDonorLoginBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        //google authentication
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.donorLoginDoNotAccText.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_donorLoginFragment_to_donorRegisterFragment);
        });

//        binding.loginForgetPassword.setOnClickListener(v -> {
//            Navigation.findNavController(view).navigate(R.id.action_donorLoginFragment_to_donorForgotPasswordFragment);
//        });

        binding.loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the progressbar visible and then show google dialog
                showLoadingBar();
                signIn();
            }
        });

        binding.donorLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingBar();
                donorEmail = String.valueOf(binding.donorLoginEmail.getText());
                donorPassword = String.valueOf(binding.donorLoginPassword.getText());
                if (donorEmail.isEmpty()) {
                    hideLoadingBar();
                    AndroidUtil.setToast(requireActivity(), "Enter valid Details");
                    binding.donorLoginEmail.setError("Please enter email");
                } else if (donorPassword.isEmpty()) {
                    hideLoadingBar();
                    AndroidUtil.setToast(requireActivity(), "Enter valid Details");
                    binding.donorLoginPassword.setError("Please enter password");
                } else if (!isValidEmail(donorEmail)) {
                    hideLoadingBar();
                    binding.donorLoginEmail.setError("Please enter valid email\nE.g. Abc@gmail.com");
                } else if (!isValidPassword(donorPassword)) {
                    hideLoadingBar();
                    binding.donorLoginPassword.setError("Please enter valid password\nE.g. Abc@123");
                } else {
                    mAuth.signInWithEmailAndPassword(donorEmail, donorPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideLoadingBar();
                                        AndroidUtil.setToast(requireActivity(), "Login Successfully");
                                        Intent i = new Intent(requireActivity(), DonorMainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        requireActivity().finish();
                                    } else {
                                        hideLoadingBar();

                                        if (Objects.requireNonNull(task.getException().getMessage()).contains("INVALID_LOGIN_CREDENTIALS")) {
                                            AndroidUtil.setToast(requireActivity(), "Invalid email or password");
                                        } else if (task.getException().getMessage().contains("USER_NOT_FOUND")) {
                                            AndroidUtil.setToast(requireActivity(), "User Not Registered");
                                        }
                                    }
                                }
                            });
                }
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void signIn() {
        hideLoadingBar();
        Intent intent = googleClient.getSignInIntent();
        startActivityForResult(intent, REQ_ONE_TAP);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        //current user check if already authenticated then send to main page
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            showLoadingBar();
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
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            AndroidUtil.setToast(requireActivity(), "Login successfully");
                            Intent i = new Intent(requireActivity(), DonorMainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            requireActivity().finish();
                        } else {
                            hideLoadingBar();
                            // If sign in fails, display a message to the user.
                            AndroidUtil.setToast(requireActivity(), "Login failed");
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

    private void showLoadingBar() {
        binding.donorLoginLoadingBar.setVisibility(View.VISIBLE);
        binding.donorLoginLoadingBar.playAnimation();
    }

    private void hideLoadingBar() {
        binding.donorLoginLoadingBar.setVisibility(View.GONE);
        binding.donorLoginLoadingBar.setImageDrawable(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
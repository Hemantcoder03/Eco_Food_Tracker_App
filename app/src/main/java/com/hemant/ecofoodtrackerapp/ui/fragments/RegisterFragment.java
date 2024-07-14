package com.hemant.ecofoodtrackerapp.ui.fragments;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.FragmentRegisterBinding;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.MainActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private static final int REQ_ONE_TAP = 101;
    FirebaseAuth mAuth;     //firebase auth object which can used to check authentication
    GoogleSignInClient googleClient;
    GoogleSignInOptions gso;
    String userEmail, userPassword, userConfirmPassword;
    FirebaseFirestore db;
    SharedPreferences sharedPref;
    FragmentRegisterBinding binding;
    View view;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        view = binding.getRoot();

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

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.registerLoadingBar.setVisibility(View.VISIBLE);
                binding.registerLoadingBar.playAnimation();
                userEmail = String.valueOf(binding.registerEmail.getText());
                userPassword = String.valueOf(binding.registerPassword.getText());
                userConfirmPassword = String.valueOf(binding.registerConfirmPassword.getText());
                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    hideLoadingBar();
                    AndroidUtil.setToast(requireActivity(), "Enter valid Details");
                    if (userEmail.isEmpty())
                        binding.registerEmail.setError("Please enter email");
                    else if (userPassword.isEmpty())
                        binding.registerPassword.setError("Please enter password");
                    else if (userConfirmPassword.isEmpty())
                        binding.registerPassword.setError("Please enter confirm password");
                } else if (!isValidEmail(userEmail)) {
                    hideLoadingBar();
                    binding.registerEmail.setError("Please enter valid email\n E.g. abc@gmail.com");
                } else if (!isValidPassword(userPassword)) {
                    hideLoadingBar();
                    binding.registerPassword.setError("Please enter valid password\n E.g. Abc@123");
                } else if (!userPassword.equals(userConfirmPassword)) {
                    hideLoadingBar();
                    binding.registerConfirmPassword.setError("Password not matching");
                } else {

                    mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideLoadingBar();

                                        //save to firebase
                                        //set the user as default name
                                        saveToDB("User", userEmail);

                                        AndroidUtil.setToast(requireActivity(), "Registration Successfully");
                                        Intent i = new Intent(requireActivity(), MainActivity.class);
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

        binding.registerGoogleBtn.setOnClickListener(v -> {
            binding.registerLoadingBar.setVisibility(View.VISIBLE);
            signUp();
        });

        binding.alreadyAccountBtn.setOnClickListener(v -> {

            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
        });

        // Inflate the layout for requireActivity() fragment
        return view;
    }

    public void signUp() {
        hideLoadingBar();
        Intent intent = googleClient.getSignInIntent();
        startActivityForResult(intent, REQ_ONE_TAP);
    }

    @Override
    public void onStart() {
        super.onStart();

        //current user check if already authenticated then send to main page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            Intent i = new Intent(requireActivity(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {

            binding.registerLoadingBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception e) {
                hideLoadingBar();
                AndroidUtil.setToast(requireActivity(), "Something went wrong " + e.getLocalizedMessage());
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
                            Intent i = new Intent(requireActivity(), MainActivity.class);
                            AndroidUtil.setToast(requireActivity(), "Registration successfully");
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

        //set up firebase notification and add subscription to topic
        setUpFirebaseNotification();

        sharedPref = requireActivity().getSharedPreferences("My_Pref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userEmail", email);
        editor.putString("userName", name);
        editor.apply();

        FirebaseUtil.setCurrentUserDetails(new UserDataModel(name, email, FirebaseUtil.getCurrentUserId(), "", "", "Receiver", Timestamp.now(), new LocationModel(), ""));
    }

    private void setUpFirebaseNotification() {

        //set the subscription for particular topic like youtube
        FirebaseMessaging.getInstance().subscribeToTopic("NewFoodAdded")
                .addOnSuccessListener(v -> {
                    AndroidUtil.setLog("checkError", "notification subscribed");
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setLog("checkError", "fail to notification subscribe");
                });
    }

    private void hideLoadingBar() {
        binding.registerLoadingBar.setVisibility(View.GONE);
        binding.registerLoadingBar.setImageDrawable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQ_ONE_TAP = 101;
    TextInputEditText regUsername, regEmail, regPassword;
    Button regBtn;
    TextView loginText;
    CircleImageView regGoogleBtn;
    FirebaseAuth mAuth;     //firebase auth object which can used to check authentication
    GoogleSignInClient googleClient;
    GoogleSignInOptions gso;
    String userName,userEmail,userPassword;
    ProgressBar regProgressBar;
    FirebaseFirestore db;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPref = getSharedPreferences("My_Pref",0);
        SharedPreferences.Editor editor = sharedPref.edit();

        regUsername = findViewById(R.id.regUsername);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regBtn = findViewById(R.id.regBtn);
        loginText = findViewById(R.id.loginText);
        regGoogleBtn = findViewById(R.id.regGoogleBtn);
        regProgressBar = findViewById(R.id.regProgressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //google authentication
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this, gso);

        regBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                regProgressBar.setVisibility(View.VISIBLE);
                userName = regUsername.getText().toString();
                userEmail = regEmail.getText().toString();
                userPassword = regPassword.getText().toString();
                if(userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()){
                    regProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Enter valid Details", Toast.LENGTH_SHORT).show();
                }
                else if(!isValidEmail(userEmail) || !isValidPassword(userPassword)){
                    regProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Please enter valid email or password", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        regProgressBar.setVisibility(View.GONE);

                                        //save to firebase
                                        saveToDB(userName, userEmail);

                                        Toast.makeText(RegisterActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        regProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "User Already Registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        regGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regProgressBar.setVisibility(View.VISIBLE);
                signUp();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    public void signUp() {
        regProgressBar.setVisibility(View.GONE);
        Intent intent = googleClient.getSignInIntent();
        startActivityForResult(intent, REQ_ONE_TAP);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //current user check if already authenticated then send to main page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            regProgressBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task .getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception e) {
                regProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            regProgressBar.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //save to firebase
                            saveToDB(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail());

                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            Toast.makeText(RegisterActivity.this, "Registration successfully", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            regProgressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%^&*+=~]).{8,}");
        return pattern.matcher(password).matches();
    }

    private Boolean isValidEmail(String email){
        //It used default email address validation
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveToDB(String name, String email){

        sharedPref = getSharedPreferences("My_Pref",0)  ;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userEmail",email);
        editor.putString("userName",name);
        editor.apply();

        FirebaseUtil.setCurrentUserDetails(new UserDataModel(name,email,FirebaseUtil.getCurrentUserId(),"","",Timestamp.now()));

//        CollectionReference ref = db.collection("Receivers");
//        ref.add(new ReceiverData(name,email,password)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(RegisterActivity.this, "failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

//    private void saveToFirebase(){
//        FirebaseUtil.getCurrentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    UserData user = task.getResult().toObject(UserData.class);
//                    if(user != null){
//
//                    }
//                }
//                else{
//
//                }
//            }
//        });
//    }
}
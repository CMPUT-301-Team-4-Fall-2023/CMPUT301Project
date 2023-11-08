package com.example.cmput301project.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput301project.Database;
import com.example.cmput301project.UserManager;
import com.example.cmput301project.activities.MainActivity;
import com.example.cmput301project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;
    private TextView alreadyHaveAccountTextView;
    private FirebaseAuth userAuth;
    private CollectionReference usersRef;
    private UserManager userManager;
    private Database db;

    private void showToast(String message){
        Toast.makeText(SignUpActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private void grabUIElements(){
        usernameField = findViewById(R.id.usernameEntry);
        emailField = findViewById(R.id.emailEntry);
        passwordField = findViewById(R.id.passwordEntry);
        passwordField.setTypeface(Typeface.DEFAULT); // To display the hint
        signUpButton = findViewById(R.id.signUpButton);
        alreadyHaveAccountTextView = findViewById(R.id.accountLoginTextView);
    }

    // TODO: This is backend code, I will move this to the db wrapper class
    public void setDisplayName(String userName){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        userManager.getLoggedInUser().updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    }
                });
    }

    //TODO: This is backend code, I will move this to the db wrapper class
    private void signUpUser(String userName, String userEmail){
        FirebaseUser potentialUser = userAuth.getCurrentUser();
        userManager.setLoggedInUser(potentialUser);
        DocumentReference userRef = usersRef.document(userName);
        userRef.get().addOnSuccessListener(doc -> {
            db.registerUser(userManager.getUserID(), userEmail, userName);
            setDisplayName(userName);
            //TODO: Initialize their own database doc that will store all their information
            // probably make the document name their UID so that they can actually write to the db
            navigateToMainActivity();
        });
    }

    private boolean checkForInvalidInputs() {
        if (usernameField.getText().toString().equals("")){
            showToast("Please enter a username");
            return true;
        }
        else if (emailField.getText().toString().equals("")){
            showToast("Please enter an email");
            return true;
        }
        else if (passwordField.getText().toString().equals("")){
            showToast("Please enter a password");
            return true;
        }
        return false;
    }

    private void navigateToLoginActivity(View view){
        finish();
    }

    private void navigateToMainActivity(){
        Intent i  = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void addListeners(){
        signUpButton.setOnClickListener(this::attemptSignUp);
        alreadyHaveAccountTextView.setOnClickListener(this::navigateToLoginActivity);
    }

    public void attemptSignUp(View v){

        if (checkForInvalidInputs()) return;

        String userName = usernameField.getText().toString();
        String userEmail = emailField.getText().toString();
        String userPassword = passwordField.getText().toString();

        //TODO: Everything from this point on is an amalgamation of front & backend,
        // will refactor -ALEX
        DocumentReference userRef = usersRef.document(userName);

        userRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()){
                showToast("An account already exists for this username");
            }
            else{
                userAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                signUpUser(userName, userEmail);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                showToast(task.getException().getLocalizedMessage());
                            }
                        });
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        userAuth = FirebaseAuth.getInstance();

        userManager = UserManager.getInstance();
        db = Database.getInstance();

        grabUIElements();
        addListeners();
    }
}

/**
 * Login page for the application where users can enter their email and password to sign in.
 * New users are redirected to the sign-up page. Upon successful login, users are directed
 * to the main activity. The class integrates Firebase authentication for user verification.
 * It provides UI elements for email, password, sign-in button, and navigation to the sign-up page.
 * User input validation is performed, and login attempts are processed, with success leading
 * to navigation to the main activity. In case of failures, appropriate error messages are displayed.
 */



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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.UserManager;
import com.example.cmput301project.activities.MainActivity;
import com.example.cmput301project.activities.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;
    private TextView createAccountTextView;
    private TextView errorTextView;
    private FirebaseAuth userAuth;
    private UserManager userManager;
    private Database db;

    private void grabUIElements() {
        emailField = findViewById(R.id.emailEntry);
        passwordField = findViewById(R.id.passwordEntry);
        passwordField.setTypeface(Typeface.DEFAULT); // To display the hint
        signInButton = findViewById(R.id.signInButton);
        createAccountTextView = findViewById(R.id.accountCreationText);
        errorTextView = findViewById(R.id.errorTextView);
        errorTextView.setVisibility(View.GONE);
    }

    private void showToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkUserLoggedOn(){
        if (userAuth.getCurrentUser() != null){
            userManager.setLoggedInUser(userAuth.getCurrentUser());
            navigateToMainPage();
        }
    }

    private boolean checkForInvalidInputs() {
        if (emailField.getText().toString().equals("")){
            errorTextView.setText(R.string.emailFieldError);
            errorTextView.setVisibility(View.VISIBLE);
            return true;
        }
        else if (passwordField.getText().toString().equals("")){
            errorTextView.setText(R.string.passwordFieldError);
            errorTextView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void navigateToSignUp(View view) {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    private void navigateToMainPage(){
        Intent i  = new Intent(LoginActivity.this, MainActivity.class);
        db.setItemCollection();
        startActivity(i);
        finish();
    }

    private void addListeners() {
        signInButton.setOnClickListener(this::attemptLogin);
        createAccountTextView.setOnClickListener(this::navigateToSignUp);
    }


    private void attemptLogin(View v){

        if (checkForInvalidInputs()) return;

        String userEmail = emailField.getText().toString();
        String userPassword = passwordField.getText().toString();

        userAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        userManager.setLoggedInUser(userAuth.getCurrentUser());
                        navigateToMainPage();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        showToast(task.getException().getLocalizedMessage());
                    }
                });
    }

    /**
     * Starts the login activity and prompts the user to log in
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Turns off dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.login);

        userAuth = FirebaseAuth.getInstance();
        userManager = UserManager.getInstance();
        db = Database.getInstance();

        checkUserLoggedOn();

        grabUIElements();
        addListeners();
    }

}

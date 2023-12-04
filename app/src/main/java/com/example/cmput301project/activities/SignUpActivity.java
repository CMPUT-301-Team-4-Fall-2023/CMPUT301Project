/**
 * Sign-up page for the application where users can create a new account by providing a username,
 * email, and password. This class integrates with Firebase authentication and Firestore for user registration.
 * Upon successful sign-up, users are directed to the main activity. User input validation is performed,
 * and the class provides functionality to navigate to the login activity. Existing usernames are checked
 * to avoid duplicates, and appropriate error messages are displayed to the user. The class also handles
 * backend code related to user registration and database interaction.
 */

package com.example.cmput301project.activities;

// Import statements

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.UserManager;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    // Membership variable declaration
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;
    private TextView alreadyHaveAccountTextView;
    private TextView errorTextView;
    private UserManager userManager;
    private Database db;
    private final Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");

    /**
     * Displays a toast message with the provided error message.
     *
     * @param message The error message to be displayed.
     */
    private void showToast(String message) {
        errorTextView.setText(message);
    }

    /**
     * Retrieves UI elements from the layout file.
     */
    private void grabUIElements() {
        usernameField = findViewById(R.id.usernameEntry);
        emailField = findViewById(R.id.emailEntry);
        passwordField = findViewById(R.id.passwordEntry);
        passwordField.setTypeface(Typeface.DEFAULT); // To display the hint
        signUpButton = findViewById(R.id.signUpButton);
        errorTextView = findViewById(R.id.errorTextView);
        alreadyHaveAccountTextView = findViewById(R.id.accountLoginTextView);
    }

    /**
     * Checks for invalid user inputs, such as empty fields.
     *
     * @return True if invalid inputs are found, false otherwise.
     */
    private boolean checkForInvalidInputs() {

        if (usernameField.getText().toString().equals("")) {
            errorTextView.setText(R.string.userNameFieldError);
            return true;
        } else if (emailField.getText().toString().equals("") || !emailPattern.matcher(emailField.getText().toString()).matches()) {
            errorTextView.setText(R.string.emailFieldError);
            return true;
        } else if (passwordField.getText().toString().equals("")) {
            errorTextView.setText(R.string.passwordFieldError);
            return true;
        }
        return false;
    }

    /**
     * Navigates to the login activity.
     *
     * @param view The View that triggered this method.
     */
    private void navigateToLoginActivity(View view) {
        finish();
    }

    /**
     * Navigates to the main activity upon successful sign-up.
     */
    private void navigateToMainActivity() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        db.setItemCollection();
        startActivity(i);
        finish();
    }

    /**
     * Adds listeners to UI elements, such as buttons and text views.
     */
    private void addListeners() {
        signUpButton.setOnClickListener(this::attemptSignUp);
        alreadyHaveAccountTextView.setOnClickListener(this::navigateToLoginActivity);
    }

    /**
     * Attempts user sign-up with provided credentials. Checks for existing usernames
     * to avoid duplicates and displays appropriate error messages.
     *
     * @param v The View that triggered this method.
     */
    private void attemptSignUp(View v) {

        if (checkForInvalidInputs()) return;

        String userName = usernameField.getText().toString();
        String userEmail = emailField.getText().toString();
        String userPassword = passwordField.getText().toString();

        db.getUsersRef().document(userName).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                showToast("An account already exists for this username");
            } else {
                userManager.getUserAuth().createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        userManager.signUpUser(userName, userEmail);
                        navigateToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        showToast(task.getException().getLocalizedMessage());
                    }
                });
            }
        });
    }

    /**
     * Starts the sign up activity and prompts the user to sign in
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        userManager = UserManager.getInstance();
        db = Database.getInstance();
        userManager.setDatabase(db);

        grabUIElements();
        addListeners();
    }
}

package com.example.cmput301project.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput301project.activities.MainActivity;
import com.example.cmput301project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;
    private TextView alreadyHaveAccountTextView;
    private FirebaseAuth userAuth;
    private CollectionReference usersRef;

    private void grabUIElements(){
        usernameField = findViewById(R.id.usernameEntry);
        emailField = findViewById(R.id.emailEntry);
        passwordField = findViewById(R.id.passwordEntry);
        passwordField.setTypeface(Typeface.DEFAULT); // To display the hint
        signUpButton = findViewById(R.id.signUpButton);
        alreadyHaveAccountTextView = findViewById(R.id.accountLoginTextView);
    }

    public void setDisplayName(FirebaseUser user){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(usernameField.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    public void attemptSignUp(View v){
        if (usernameField.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this,
                    "Please enter a username",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (emailField.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this,
                    "Please enter an email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (passwordField.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this,
                    "Please enter a password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String userName = usernameField.getText().toString();
        String userEmail = emailField.getText().toString();
        String userPassword = passwordField.getText().toString();

        DocumentReference userRef = usersRef.document(
                userName);
        userRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()){
                Toast.makeText(SignUpActivity.this,
                        "An account already exists for this username",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                userAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser potentialUser = userAuth.getCurrentUser();
                                    DocumentReference userRef = usersRef.document(
                                            userName);
                                    userRef.get().addOnSuccessListener(doc -> {
                                        if (doc.exists()){
                                            Toast.makeText(SignUpActivity.this,
                                                    "An account already exists for this username",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            HashMap<String, String> data = new HashMap<>();
                                            data.put("username", userName);
                                            data.put("email", userEmail);
                                            data.put("password", userPassword);
                                            usersRef.document(userName)
                                                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Firestore", "New User Created!");
                                                }
                                            });
                                            setDisplayName(Objects.requireNonNull(potentialUser));
                                            //TODO probably better way, this is just so this can be
                                            // merged in and used
                                            Intent i  = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    private void navigateToLoginActivity(View view){
        finish();
    }

    private void addListeners(){
        signUpButton.setOnClickListener(this::attemptSignUp);
        alreadyHaveAccountTextView.setOnClickListener(this::navigateToLoginActivity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        userAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        grabUIElements();
        addListeners();
    }
}

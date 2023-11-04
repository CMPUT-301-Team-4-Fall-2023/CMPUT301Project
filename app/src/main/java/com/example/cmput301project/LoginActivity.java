package com.example.cmput301project;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText credentialField;
    private EditText passwordField;
    private Button signInButton;
    private TextView createAccountTextView;
    private FirebaseAuth userAuth;

    private void grabUIElements(){
        credentialField = findViewById(R.id.emailEntry);
        passwordField = findViewById(R.id.passwordEntry);
        passwordField.setTypeface(Typeface.DEFAULT); // To display the hint
        signInButton = findViewById(R.id.signInButton);
        createAccountTextView = findViewById(R.id.accountCreationText);
    }

    private void checkUserLoggedOn(){
        FirebaseUser currentUser = userAuth.getCurrentUser();
        if(currentUser != null){
            //TODO: Navigate to main activity

        }
    }

    public void queryFirebase(View v){
        String userCredential = credentialField.getText().toString();
        String userPassword = passwordField.getText().toString();

        userAuth.signInWithEmailAndPassword(userCredential, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = userAuth.getCurrentUser();
                            //TODO Add navigation to main page here
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void navigateToSignUp(View view){
        Intent i  = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    private void addListeners(){
        signInButton.setOnClickListener(this::queryFirebase);
        createAccountTextView.setOnClickListener(this::navigateToSignUp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userAuth = FirebaseAuth.getInstance();

        checkUserLoggedOn();

        grabUIElements();
        addListeners();
    }

}

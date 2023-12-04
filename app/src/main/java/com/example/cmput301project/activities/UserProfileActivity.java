/**
 * UserProfileActivity.java
 * <p>
 * This activity is responsible for displaying and managing user profile information,
 * including the user's username, email, and profile picture. It provides options for the
 * user to update their profile picture, log out, and reset their password.
 * <p>
 * The activity utilizes Firebase storage for handling profile picture uploads and
 * downloads. It also interacts with the UserManager and Database singletons to manage
 * user authentication and retrieve user details.
 * <p>
 * The activity layout is defined in the 'user_profile.xml' resource file.
 */

package com.example.cmput301project.activities;

// Import statements

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The main activity responsible for displaying and managing user profile information.
 */
public class UserProfileActivity extends AppCompatActivity {
    private TextView username;
    private TextView email;
    private TextView resetPassword;
    private TextView logOut;
    private CircleImageView profilePicture;
    private ImageButton backButton;
    private UserManager userManager;
    private Database db;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            // Good lord above forgive me for what I am about to do
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), imageUri -> {
                if (imageUri != null) {
                    StorageReference iconRef = FirebaseStorage.getInstance().getReference("profilePictures/" + userManager.getUserID());
                    UploadTask uploadTask = iconRef.putFile(imageUri);

                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return iconRef.getDownloadUrl();
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                userManager.setProfilePicture(downloadUri);
                                Glide.with(getApplicationContext()).load(downloadUri).apply(new RequestOptions().placeholder(R.drawable.defaultuser).error(R.drawable.defaultuser)).into(profilePicture);
                                profilePicture.invalidate();
                            }
                        }
                    });
                }
            });

    /**
     * Initializes the activity and sets the content view.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        getSingletons();
        grabUIElements();
        injectUserDetails();
        addListeners();
    }

    /**
     * Adds listeners to UI elements, such as buttons and profile picture, for user interactions.
     */
    private void addListeners() {
        backButton.setOnClickListener(v -> finish());
        profilePicture.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
        logOut.setOnClickListener(v -> logUserOut());
        resetPassword.setOnClickListener(v -> {
            Toast.makeText(UserProfileActivity.this, "email sent!", Toast.LENGTH_SHORT).show();
            userManager.sendPasswordReset();});
    }

    /**
     * Logs the user out of the application and navigates to the login screen.
     */
    private void logUserOut() {
        userManager.signOutUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Retrieves singleton instances for UserManager and Database.
     */
    private void getSingletons() {
        userManager = UserManager.getInstance();
        db = Database.getInstance();
    }

    /**
     * Displays the user's details in the corresponding UI elements.
     */
    private void injectUserDetails() {
        username.setText(userManager.getUserName());
        email.setText(userManager.getUserEmail());
        Glide.with(getApplicationContext()).load(userManager.getUserProfilePicture()).apply(new RequestOptions().placeholder(R.drawable.defaultuser).error(R.drawable.defaultuser)).into(profilePicture);
    }

    /**
     * Initializes UI elements by finding and assigning them from the layout.
     */
    private void grabUIElements() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        profilePicture = findViewById(R.id.profile_image);
        backButton = findViewById(R.id.back_button);
        resetPassword = findViewById(R.id.resetPassword);
        logOut = findViewById(R.id.logOut);
    }
}

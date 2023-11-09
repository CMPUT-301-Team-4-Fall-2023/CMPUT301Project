package com.example.cmput301project;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

final public class UserManager {
    private static UserManager instance = null;
    private final FirebaseAuth userAuth;
    private FirebaseUser loggedInUser;
    private UserManager(){
        userAuth = FirebaseAuth.getInstance();
    }

    /**
     * Singleton for user management. Holds the authenticated firebase user and provides interface
     * for controlling all user functionality, such as logging out
     *
     * @return the instance of the user manager class
     */
    public static UserManager getInstance(){
        if (instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * @param user User to be logged in
     */
    public void setLoggedInUser(FirebaseUser user){
        this.loggedInUser = user;
    }

    /**
     * @return the logged in user
     */
    public FirebaseUser getLoggedInUser() {return loggedInUser;}

    /**
     * @return the username of the current user
     */
    public String getUserName(){
        return loggedInUser.getDisplayName();
    }

    /**
     * @return the user id of the current user
     */
    public String getUserID(){
        return loggedInUser.getUid();
    }

    /**
     * Sets the current user's username
     *
     * @param userName the string that the username will be set to
     */
    public void setDisplayName(String userName){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        loggedInUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    }
                });
    }

    public void signUpUser(String userName, String userEmail){ //TODO: Finish refactoring
        FirebaseUser potentialUser = userAuth.getCurrentUser();
        setLoggedInUser(potentialUser);
    }

}

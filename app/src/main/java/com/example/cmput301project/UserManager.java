package com.example.cmput301project;

import com.google.firebase.auth.FirebaseUser;

final public class UserManager {
    private static UserManager instance = null;
    private FirebaseUser loggedInUser;
    private UserManager(){}

    public static UserManager getInstance(){
        if (instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public void setLoggedInUser(FirebaseUser user){
        this.loggedInUser = user;
    }

    public FirebaseUser getLoggedInUser() {return loggedInUser;}

    public String getUserName(){
        return loggedInUser.getDisplayName();
    }

    public String getUserID(){
        return loggedInUser.getUid();
    }

}

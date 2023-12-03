package com.example.cmput301project;

import android.app.Application;
import android.net.Uri;

public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UserManager um = UserManager.getInstance();
        um.setUserNameTest("TEST");
        um.setUserEmailTest("test@gmail.com");
        um.setUserProfilePictureTest(Uri.parse("res/drawable/defaultuser.jpg"));
    }
}

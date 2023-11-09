package com.example.cmput301project;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Database {
    private static Database instance = null;
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;
    private final UserManager userManager;
    private Database(){
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("usernames");
        userManager = UserManager.getInstance();
    }

    /**
     * Singleton for the firestore database. Holds the database instance and provides all methods
     * for interacting with the database
     *
     * @return the instance of the user manager class
     */
    public static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    /**
     * Registers the user in the database, with the doc name being the user's Id
     *
     * @param userId string that is the user's id
     * @param userEmail string that is the user's email
     * @param userName string that is the user's username
     */
    public void registerUser(String userId, String userEmail, String userName){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("username", userName);
        usersRef.document(userId)
                .set(data).addOnSuccessListener(unused -> Log.d("Firestore", "New User Created!"));
    }

}

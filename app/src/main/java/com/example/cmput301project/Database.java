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

    public static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }
    public void registerUser(String userId, String userEmail, String userName){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("username", userName);
        usersRef.document(userId)
                .set(data).addOnSuccessListener(unused -> Log.d("Firestore", "New User Created!"));
    }

}

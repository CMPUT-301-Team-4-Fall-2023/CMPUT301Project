package com.example.cmput301project;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class FirebaseTest {

    private FirebaseFirestore db;
    private CollectionReference testRef;

    @Before
    public void initialize(){
        db = FirebaseFirestore.getInstance();
        testRef = db.collection("test");
    }
    @Test
    public void writeTest(){
        HashMap<String, String> data = new HashMap<>();
        data.put("User1", "Password");
        testRef.document("User1").set(data);
}
}

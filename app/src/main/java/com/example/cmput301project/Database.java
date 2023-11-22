/**
 * Manages interactions with the Firestore database,
 * providing methods to register users, add, edit, and delete items,
 * and listen for changes in the item collection.
 * This class follows the Singleton pattern, ensuring a single instance
 * for database operations throughout the application.
 */


package com.example.cmput301project;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.ItemAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private static Database instance = null;
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;
    private CollectionReference itemsRef;
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
        data.put("username", userName);
        data.put("email", userEmail);
        usersRef.document(userId)
                .set(data).addOnSuccessListener(unused -> Log.d("Firestore", "New User Created!"));
    }

    /**
     * Adds item to a firebase collection for authenticated user
     *
     * @param item item to add
     */
    public void addItem(Item item){
        itemsRef.document(item.getName()).set(item).addOnSuccessListener(unused -> Log.d("Firestore", String.format("Item %s Added!", item.getName())));
    }

    /**
     * Edits item in a firebase collection for authenticated user
     *
     * @param item item to edit
     */
    public void editItem(Item item){
        itemsRef.document(item.getName()).set(item).addOnSuccessListener(unused -> Log.d("Firestore", String.format("Item %s Edited!", item.getName())));
    }

    /**
     * Deletes item from a firebase collection for authenticated user
     *
     * @param item item to delete
     */
    public void deleteItem(Item item){
        itemsRef.document(item.getName()).delete().addOnSuccessListener(unused -> Log.d("Firestore", String.format("Item %s Edited!", item.getName())));
    }

    /**
     * Adds array and corresponding arrayAdapter as listeners to the changes in a firebase collection
     *
     * @param array array to update as listener
     * @param arrayAdapter arrayAdapter to change when change to data occurs
     */
    public void addArrayAsListener(ArrayList<Item> array, ArrayAdapter<Item> arrayAdapter){
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    array.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String name = doc.getId();
                        Item item = doc.toObject(Item.class);
                        item.setName(name);
                        Log.d("Firestore", String.format("Item(%s) fetched", name));
                        array.add(item);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addTotalListener(TotalListener totalListener) {
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    totalListener.setTotal(0.0);
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String name = doc.getId();
                        Item item = doc.toObject(Item.class);
                        item.setName(name);
                        totalListener.addTotal(item.getValue());
                    }
                    totalListener.update();
                    Log.d("Firestore", "total updated");
                }
            }
        });
    }

    public void setItemCollection(){
        itemsRef = db.collection("store")
                .document(userManager.getUserID())
                .collection("items");
    }

    public CollectionReference getUsersRef(){
        return usersRef;
    }
}

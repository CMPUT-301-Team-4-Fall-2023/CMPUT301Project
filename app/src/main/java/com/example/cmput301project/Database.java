/**
 * Manages interactions with the Firestore database,
 * providing methods to register users, add, edit, and delete items,
 * and listen for changes in the item collection.
 * This class follows the Singleton pattern, ensuring a single instance
 * for database operations throughout the application.
 */

package com.example.cmput301project;

// Import statements

import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.Photograph;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    // Membership variable declaration
    private static Database instance = null;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final CollectionReference usersRef;
    private StorageReference itemImageRef;
    private CollectionReference itemsRef;
    private final UserManager userManager;

    private Database() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        usersRef = db.collection("usernames");
        userManager = UserManager.getInstance();
    }

    public interface OnEditCompleteListener {
        void onEditComplete();
    }

    /**
     * Singleton for the firestore database. Holds the database instance and provides all methods
     * for interacting with the database
     *
     * @return the instance of the user manager class
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Registers the user in the database, with the doc name being the user's Id
     *
     * @param userId    string that is the user's id
     * @param userEmail string that is the user's email
     * @param userName  string that is the user's username
     */
    public void registerUser(String userId, String userEmail, String userName) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", userName);
        data.put("email", userEmail);
        usersRef.document(userId).set(data).addOnSuccessListener(unused -> Log.d("Firestore", "New User Created!"));
    }

    /**
     * Adds item to a firebase collection for authenticated user
     *
     * @param item item to add
     */
    public void addItem(Item item) {
        itemsRef.document(item.getUniqueId().toString())
                .set(item)
                .addOnSuccessListener(unused -> {
                    Log.d("Firestore", String.format("Item %s Added!", item.getName()));
                })
                .addOnFailureListener(e -> {
                    // Handle failure if necessary
                    Log.e("Firestore", "Error adding item", e);
                });
    }

    /**
     * Edits item in a firebase collection for authenticated user
     *
     * @param item item to edit
     */
    public void editItem(Item item, OnEditCompleteListener listener) {
        itemsRef.document(item.getUniqueId().toString())
                .set(item)
                .addOnSuccessListener(unused -> {
                    Log.d("Firestore", String.format("Item %s Edited!", item.getName()));
                    listener.onEditComplete();
                })
                .addOnFailureListener(e -> {
                    // Handle failure if necessary
                    Log.e("Firestore", "Error editing item", e);
                });
    }

    /**
     * Deletes item from a firebase collection for authenticated user
     *
     * @param item item to delete
     */
    public void deleteItem(Item item) {
        if (item.getPhotographs() != null) {
            for (Photograph ph : item.getPhotographs()) {
                itemImageRef.child("/" + ph.getName()).delete().addOnFailureListener(e -> Log.d("Firestore-Images", String.format("Failure when deleting photo: %s", ph.getName())));
            }
        }
        itemsRef.document(item.getUniqueId().toString()).delete().addOnSuccessListener(unused -> Log.d("Firestore", String.format("Item %s Edited!", item.getName())));
    }

    /**
     * Adds array and corresponding arrayAdapter as listeners to the changes in a firebase collection
     *
     * @param array        array to update as listener
     * @param arrayAdapter arrayAdapter to change when change to data occurs
     */
    public void addArrayAsListener(ArrayList<Item> array, ArrayAdapter<Item> arrayAdapter) {
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    array.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String name = doc.getId();
                        Item item = doc.toObject(Item.class);
                        Log.d("Firestore", String.format("Item(%s) fetched", name));
                        array.add(item);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Adds an image to the storage with the specified name and URI.
     *
     * @param name The name of the image.
     * @param uri  The URI of the image to be uploaded.
     * @return An UploadTask representing the asynchronous operation of uploading the image.
     */
    public UploadTask addImage(String name, Uri uri) {
        return itemImageRef.child("/" + name).putFile(uri);
    }

    /**
     * Retrieves the download URL for an image with the specified name.
     *
     * @param name The name of the image.
     * @return A Task containing the Uri of the image's download URL.
     */
    public Task<Uri> getImage(String name) {
        return itemImageRef.child("/" + name).getDownloadUrl();
    }

    /**
     * Retrieves the StorageReference for an image with the specified name, suitable for Glide.
     *
     * @param name The name of the image.
     * @return The StorageReference for the specified image.
     */
    public StorageReference getImageForGlide(String name) {
        return itemImageRef.child("/" + name);
    }


    /**
     * Adds a listener to monitor changes in the total value of items in the Firestore database.
     *
     * @param totalListener The TotalListener to be notified of total value changes.
     */
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
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        Item item = doc.toObject(Item.class);
                        totalListener.addTotal(item.getValue());
                    }
                    totalListener.update();
                    Log.d("Firestore", "total updated");
                }
            }
        });
    }

    /**
     * Sets the Firestore collection references for items and user data.
     */
    public void setItemCollection() {
        itemsRef = db.collection("store").document(userManager.getUserID()).collection("items");
        itemImageRef = storage.getReference().child("images");
    }

    /**
     * Retrieves the Firestore collection reference for users.
     *
     * @return The CollectionReference for the users in Firestore.
     */
    public CollectionReference getUsersRef() {
        return usersRef;
    }
}

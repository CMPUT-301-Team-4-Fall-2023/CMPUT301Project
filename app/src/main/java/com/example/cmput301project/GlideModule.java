/**
 * GlideModule is a class designed to simplify image loading in Android applications using the Glide library.
 * It extends AppGlideModule and registers a custom component to handle loading images from Firebase Storage.
 * This allows seamless integration of Glide with Firebase Storage's StorageReference objects.
 *
 * Usage:
 * To load images from Firebase Storage using Glide, include this class in your project and ensure that
 * the appropriate dependencies are added to your app's build.gradle file.
 *
 * Note: This class is annotated with @GlideModule, indicating to Glide's annotation processor that it should
 * generate necessary code during the compilation process.
 *
 * @see com.bumptech.glide.module.AppGlideModule
 * @see com.bumptech.glide.Registry
 * @see com.firebase.ui.storage.images.FirebaseImageLoader
 * @see com.google.firebase.storage.StorageReference
 */

package com.example.cmput301project;

// Import statements
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * Simple class for making loading images easier
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModule extends AppGlideModule {
    /**
     * @param context  An Application {@link android.content.Context}.
     * @param glide    The Glide singleton that is in the process of being initialized.
     * @param registry An {@link com.bumptech.glide.Registry} to use to register components.
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }
}

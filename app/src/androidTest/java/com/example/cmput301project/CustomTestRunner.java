package com.example.cmput301project;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

/**
 * This is a custom test runner that loads the test application. The test application functions the
 * same as the normal application, except the user has a few pre-loaded fields so any activity can
 * be tested.
 */
public class CustomTestRunner extends AndroidJUnitRunner {
    /**
     * @param cl        The ClassLoader with which to instantiate the object.
     * @param className The name of the class implementing the Application
     *                  object.
     * @param context   The context to initialize the application with
     * @return the test application that will be used for runners
     * @throws InstantiationException instantiation exception
     * @throws IllegalAccessException illegal class exception
     * @throws ClassNotFoundException class not found exception
     */
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // Specify the TestApplication class for testing
        return super.newApplication(cl, TestApplication.class.getName(), context);
    }
}
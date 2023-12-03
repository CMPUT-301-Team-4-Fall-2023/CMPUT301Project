package com.example.cmput301project;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

public class CustomTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // Specify the TestApplication class for testing
        return super.newApplication(cl, TestApplication.class.getName(), context);
    }
}
package com.example.cmput301project;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.lang.Thread.sleep;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cmput301project.activities.LoginActivity;
import com.example.cmput301project.activities.MainActivity;
import com.example.cmput301project.activities.SignUpActivity;
import com.example.cmput301project.itemClasses.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
@RunWith(AndroidJUnit4.class)
public class AddItemFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    // Test initialization and opening the AddItemFragment
    @Test
    public void testAddItemDialog() {
        onView(withId(R.id.add_item_button)).perform(click()); // Trigger the button to open the AddItemFragment

        // Verify the elements in the AddItemFragment are displayed
        onView(withId(R.id.name_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.description_edit_text)).check(matches(isDisplayed()));
        // Add more verifications for other fields if necessary
    }

    // Test adding a valid item through the AddItemFragment
    @Test
    public void testAddValidItem() {
        onView(withId(R.id.add_item_button)).perform(click()); // Open the AddItemFragment

        // Fill in valid details in the AddItemFragment
        onView(withId(R.id.name_edit_text)).perform(replaceText("Item Name"));
        onView(withId(R.id.description_edit_text)).perform(replaceText("Item Description"));
        // Add valid details for other fields
        // ...

        // Click the OK button in the dialog
        onView(withText("OK")).perform(click()); // Modify "OK" text if it's different

        // Verify the item is added and displayed in the MainActivity
        onView(withText("Item Name")).check(matches(isDisplayed()));
        // Add more assertions to verify other details if necessary
    }

    // Test adding an invalid item through the AddItemFragment
    @Test
    public void testAddInvalidItem() {
        onView(withId(R.id.add_item_button)).perform(click()); // Open the AddItemFragment

        // Fill in invalid details in the AddItemFragment (e.g., leave a required field empty)
        onView(withId(R.id.name_edit_text)).perform(replaceText("")); // Empty name
        // Fill in other fields with invalid details
        // ...

        // Click the OK button in the dialog
        onView(withText("OK")).perform(click()); // Modify "OK" text if it's different

        // Verify an error message or appropriate handling is displayed in the dialog
        onView(withText("Item name required")).check(matches(isDisplayed())); // Modify as per actual error message

        // Ensure the item is not added to the MainActivity
        onView(withText("")).check(doesNotExist()); // Verify the item is not added
        // Add more verifications for other fields to ensure invalid input is rejected
    }

    // Add more test methods for other functionalities or edge cases

}


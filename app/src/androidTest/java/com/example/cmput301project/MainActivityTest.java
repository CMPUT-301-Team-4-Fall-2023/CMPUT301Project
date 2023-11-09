package com.example.cmput301project;


import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
public class MainActivityTest {

    // Rule to launch MainActivity for testing
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    // Test for initialization
    @Test
    public void testInitialization() {
        // Ensure MainActivity is launched and visible
        onView(withId(R.id.item_list)).check(matches(isDisplayed())); // Replace R.id.item_list with your actual ListView ID
        onView(withId(R.id.add_item_button)).check(matches(isDisplayed())); // Replace R.id.add_item_button with your actual Add Item button ID
        // Add more checks for other UI elements as needed
    }

    // Test AddItem functionality
    @Test
    public void testAddItem() {
        // Click the add item button
        onView(withId(R.id.add_item_button)).perform(click()); // Replace R.id.add_item_button with your actual Add Item button ID

        // Add item details and perform the addition
        // Use Espresso interactions to input details and confirm adding the item

        // Check if the added item is displayed in the list view
        onView(withText("New Item")).check(matches(isDisplayed())); // Replace "New Item" with the expected item name
        // Add more verifications for added items if required
    }

    // Test Item Deletion
    @Test
    public void testItemDeletion() {
        // Select an item in the list
        onView(withId(R.id.item_list)).perform(click()); // Click on the item in the list

        // Click delete button
        onView(withId(R.id.delete_items_button)).perform(click()); // Replace R.id.delete_items_button with your actual Delete button ID

        // Verify that the selected item is removed from the list
        onView(withText("Item to delete")).check(matches(not(isDisplayed()))); // Replace "Item to delete" with the deleted item name
        // Add more verifications for deleted items if needed
    }

    // Add more test methods for other functionalities following a similar pattern

}

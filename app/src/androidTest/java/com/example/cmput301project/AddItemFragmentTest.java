package com.example.cmput301project;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.cmput301project.activities.MainActivity;
import com.example.cmput301project.R;

import org.junit.Rule;
import org.junit.Test;

public class AddItemFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddItemDialogDisplayed() {

    }

    @Test
    public void testAddValidItem() {

    }

    @Test
    public void testAddInvalidItemEmptyName() {

    }

}

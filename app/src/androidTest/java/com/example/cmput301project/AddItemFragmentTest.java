package com.example.cmput301project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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

//    @Test
//    public void testAddItemDialogDisplayed() {
//        onView(withId(R.id.add_item_button)).perform(click());
//        onView(withId(R.id.name_edit_text)).perform(ViewActions.typeText("House"));
//        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("My House"));
//        onView(withId(R.id.serial_edit_text)).perform(ViewActions.typeText("12345"));
//        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("House Model"));
//        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("House Make"));
//        onView(withId(R.id.item_day_edit_text)).perform(ViewActions.typeText("12"));
//        onView(withId(R.id.item_month_edit_text)).perform(ViewActions.typeText("06"));
//        onView(withId(R.id.item_year_edit_text)).perform(ViewActions.typeText("2023"));
//        onView(withId(R.id.input_tag_edit_text)).perform(ViewActions.typeText("My Tag"));
//        onView(withId(R.id.add_tags_button)).perform(click());
//        onView(withId(R.id.price_edit_text)).perform(ViewActions.typeText("1234.56"));
//        onView(withId(R.id.comments_edit_text)).perform(ViewActions.typeText("Some comment"));
//    }

    @Test
    public void testAddValidItem() {

    }

    @Test
    public void testAddInvalidItemEmptyName() {

    }

}

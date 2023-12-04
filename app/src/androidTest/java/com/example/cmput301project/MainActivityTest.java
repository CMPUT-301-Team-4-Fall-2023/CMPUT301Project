package com.example.cmput301project;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

import android.app.Dialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);
    @Mock
    public FirebaseUser user;

    public boolean setUp = false;

    @Test
    public void loginSetup(){
        Intents.init();
        onView(withId(R.id.emailEntry))
                .perform(ViewActions.typeText("e@e.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.passwordEntry))
                .perform(ViewActions.typeText("123321"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signInButton)).perform(click());
        try{
            // Espresso is so bad that without this sleep the test fails, even though visually
            // it still works
            Thread.sleep(2000);
        } catch (Exception e){
            e.printStackTrace();
        }
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }
    @Test
    public void testAddItem() {
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.name_edit_text)).perform(ViewActions.typeText("House"));
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("My House"));
        onView(withId(R.id.serial_edit_text)).perform(ViewActions.typeText("12345"));
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("House Model"));
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("House Make"));
        onView(withText("MM/DD/YYYY")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.input_tag_edit_text)).perform(ViewActions.typeText("My Tag"));
        onView(withId(R.id.add_tags_button)).perform(click());
        onView(withId(R.id.price_edit_text)).perform(ViewActions.typeText("1234.56"));
        onView(withId(R.id.comments_edit_text)).perform(ViewActions.typeText("Some comment"));
        onView(withText("OK")).perform(click());
        onView(withText("House")).check(matches(isDisplayed()));

    }
    @Test
    public void testFilterItem() {
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.name_edit_text)).perform(ViewActions.typeText("House1"));
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("House1"));
        onView(withId(R.id.serial_edit_text)).perform(ViewActions.typeText("12345"));
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("House Model"));
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("House Make"));
        onView(withText("MM/DD/YYYY")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.input_tag_edit_text)).perform(ViewActions.typeText("My Tag"));
        onView(withId(R.id.add_tags_button)).perform(click());
        onView(withId(R.id.price_edit_text)).perform(ViewActions.typeText("1234.56"));
        onView(withId(R.id.comments_edit_text)).perform(ViewActions.typeText("Some comment"));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.name_edit_text)).perform(ViewActions.typeText("House2"));
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("House2"));
        onView(withId(R.id.serial_edit_text)).perform(ViewActions.typeText("12345"));
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("House Model"));
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("House Make"));
        onView(withText("MM/DD/YYYY")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.input_tag_edit_text)).perform(ViewActions.typeText("My Tag"));
        onView(withId(R.id.add_tags_button)).perform(click());
        onView(withId(R.id.price_edit_text)).perform(ViewActions.typeText("1234.56"));
        onView(withId(R.id.comments_edit_text)).perform(ViewActions.typeText("Some comment"));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.filter_items_button)).perform(click());
        onView(withId(R.id.filter_keywords_edit_text)).perform(ViewActions.typeText("House2"));
        onView(withId(R.id.add_keyword_button)).perform(click());
        onView(withText("Apply")).perform(click());
        onView(withText("House2")).check(matches(isDisplayed()));


    }
//    @Test
//    public void testViewItem() {
//        onView(withId(R.id.add_item_button)).perform(click());
//        onView(withId(R.id.name_edit_text)).perform(ViewActions.typeText("House"));
//        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("My House"));
//        onView(withId(R.id.serial_edit_text)).perform(ViewActions.typeText("12345"));
//        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("House Model"));
//        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("House Make"));
//        onView(withText("MM/DD/YYYY")).perform(click());
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.input_tag_edit_text)).perform(ViewActions.typeText("My Tag"));
//        onView(withId(R.id.add_tags_button)).perform(click());
//        onView(withId(R.id.price_edit_text)).perform(ViewActions.typeText("1234.56"));
//        onView(withId(R.id.comments_edit_text)).perform(ViewActions.typeText("Some comment"));
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.view_item_button)).perform(click());
//    }
//    public void testDeleteItem() {
//        Intents.init();
//        onView(withId(R.id.emailEntry))
//                .perform(ViewActions.typeText("admin@admin.com"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.passwordEntry))
//                .perform(ViewActions.typeText("password"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.signInButton)).perform(click());
//        try{
//            // Espresso is so bad that without this sleep the test fails, even though visually
//            // it still works
//            Thread.sleep(3000);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        intended(hasComponent(MainActivity.class.getName()));
//        Intents.release();
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
//
//        //TODO: dialog button has no id, so I cannot exit the dialog properly
//        onView(withId(R.id.view_item_button)).perform(click());
//        //TODO: dialog button has no id, so I cannot click on the delete button properly
//        onView(withText("House")).check(doesNotExist());
//    }
//    public void testInvalidItem() {
//        Intents.init();
//        onView(withId(R.id.emailEntry))
//                .perform(ViewActions.typeText("admin@admin.com"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.passwordEntry))
//                .perform(ViewActions.typeText("password"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.signInButton)).perform(click());
//        try{
//            // Espresso is so bad that without this sleep the test fails, even though visually
//            // it still works
//            Thread.sleep(3000);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        intended(hasComponent(MainActivity.class.getName()));
//        Intents.release();
//        onView(withId(R.id.add_item_button)).perform(click());
//        onView(withId(R.id.name_edit_text)).perform(ViewActions.typeText("House"));
//        onView(withId(R.id.comments_edit_text)).perform(ViewActions.typeText("Some comment"));
//
//        //TODO: dialog button has no id, so I cannot exit the dialog properly
//        onView(withText("Invalid Input")).check(matches(isDisplayed()));
//    }
}
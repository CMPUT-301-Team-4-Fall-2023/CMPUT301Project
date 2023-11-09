package com.example.cmput301project;

import static org.hamcrest.CoreMatchers.not;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);
    @Mock
    public FirebaseUser user;

    @Test
    public void testNavigateToSignIn(){
        Intents.init();
        onView(withId(R.id.accountCreationText)).perform(click());
        intended(hasComponent(SignUpActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testEmptyEmailField(){
        onView(withId(R.id.signInButton)).perform(click());
        onView(withText("Please enter a valid email")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyPasswordField(){
        onView(withId(R.id.emailEntry))
                .perform(ViewActions.typeText("admin@admin.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signInButton)).perform(click());
        onView(withText("Please enter a valid password")).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessfulSignIn(){
        Intents.init();
        onView(withId(R.id.emailEntry))
                .perform(ViewActions.typeText("admin@admin.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.passwordEntry))
                .perform(ViewActions.typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signInButton)).perform(click());
        //TODO: There is an issue with android-test that stops the main activity from being
        // launched, so visually this test succeeds but fails this test case
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }

}

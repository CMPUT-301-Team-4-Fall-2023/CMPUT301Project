package com.example.cmput301project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cmput301project.activities.LoginActivity;
import com.example.cmput301project.activities.MainActivity;
import com.example.cmput301project.activities.SignUpActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule = new ActivityScenarioRule<SignUpActivity>(SignUpActivity.class);

    @Test
    public void testActivityReturns(){
        onView(withId(R.id.accountLoginTextView)).perform(click());
        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        assertSame(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }

    @Test
    public void testEmptyUsernameField(){
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withText("Please enter a valid username")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyEmailField(){
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("John"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withText("Please enter a valid email")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyPasswordField(){
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("John"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.emailEntry)).perform(ViewActions.typeText("John@email.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withText("Please enter a valid password")).check(matches(isDisplayed()));
    }
}


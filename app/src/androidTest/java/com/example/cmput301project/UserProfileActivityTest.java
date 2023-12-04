package com.example.cmput301project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertSame;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cmput301project.activities.LoginActivity;
import com.example.cmput301project.activities.UserProfileActivity;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


public class UserProfileActivityTest {
    @Rule
    public ActivityScenarioRule<UserProfileActivity> activityRule = new ActivityScenarioRule<>(UserProfileActivity.class);

    @Test
    public void testLogout(){
        Intents.init();
        onView(withId(R.id.logOut)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testUsernameSet(){
        onView(withText("TEST")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmailSet(){
        onView(withText("test@gmail.com")).check(matches(isDisplayed()));
    }

    @Test
    public void testBackAndroidUI(){
        Espresso.pressBackUnconditionally();
        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        assertSame(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }

    @Test
    public void testBackAppUI(){
        onView(withId(R.id.back_button)).perform(click());
        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        assertSame(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }
}

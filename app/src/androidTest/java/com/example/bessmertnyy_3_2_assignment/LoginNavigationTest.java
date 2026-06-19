package com.example.bessmertnyy_3_2_assignment;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginNavigationTest {

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.deleteDatabase("users.db");

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(appContext);
        dbHelper.addUser("validUser", "validPass", "Valid", "User");
    }

    @Test
    public void successfulLogin_redirectsToMainView() {
        ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.etUsername)).perform(replaceText("validUser"));
        onView(withId(R.id.etPassword)).perform(replaceText("validPass"));
        closeSoftKeyboard();

        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.tv_title)).check(matches(isDisplayed()));
    }

    @Test
    public void createNewLoginButton_redirectsToUserCreate() {
        ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.btnCreateAccount)).perform(click());

        onView(withId(R.id.etFirstName)).check(matches(isDisplayed()));
        onView(withId(R.id.etLastName)).check(matches(isDisplayed()));
    }
}

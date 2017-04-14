package com.qwerteach.wivi.qwerteachapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void signInActivityTest() {
        ViewInteraction appCompatTextView = onView(
                allOf(withText("J'ai déjà un compte"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.email_sign_in), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.email_sign_in), isDisplayed()));
        appCompatEditText2.perform(replaceText("vivi@test.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.password_sign_in), isDisplayed()));
        appCompatEditText3.perform(replaceText("12345679"), closeSoftKeyboard());

        /*ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.sign_in_button), withText("CONNEXION"), withContentDescription("CONNEXION"), isDisplayed()));
        actionMenuItemView.perform(click());*/

        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.problem_message_sign_in_textview)).check(matches(withText("Votre e-mail ou mot de passe est incorrect.\\nEsssayez à nouveau !")));

    }

}

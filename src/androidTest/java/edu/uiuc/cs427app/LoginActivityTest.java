package edu.uiuc.cs427app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Before
    public void setUp() {
        // Clear the app context to ensure fresh testing
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.deleteFile("userAccounts.dat");
    }

    @After
    public void tearDown() {
        // Clean up data after testing
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.deleteFile("userAccounts.dat");
    }


    @Test
    public void testValidLogin() throws InterruptedException {
        // Simulate an existing username scenario
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        addAccount("existingUser", "password123", context);

        // Launch the LoginActivity
        ActivityScenario.launch(LoginActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Enter valid username and password
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("existingUser"));
        Thread.sleep(2000);
        onView(withId(R.id.passwordEntry)).perform(ViewActions.typeText("password123"));

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Login button
        onView(withId(R.id.logIn)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that MainActivity is launched
        onView(withId(R.id.textViewHeader)).check(matches(ViewMatchers.isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);
    }



    @Test
    public void testInvalidLogin() throws InterruptedException {
        // Launch the LoginActivity
        ActivityScenario.launch(LoginActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Enter valid username and invalid password
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("testUser"));
        Thread.sleep(2000);
        onView(withId(R.id.passwordEntry)).perform(ViewActions.typeText("wrongPassword"));

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Login button
        onView(withId(R.id.logIn)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that the invalid login dialog appears
        onView(withText("Invalid username or password"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);
    }

    @Test
    public void testEmptyFieldsLogin() throws InterruptedException {
        // Launch the LoginActivity
        ActivityScenario.launch(LoginActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Login button without entering any data
        onView(withId(R.id.logIn)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that the empty fields dialog appears
        onView(withText("Please ensure you have entered both a username and a password"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);
    }

    /**
     * Helper method to add a user account programmatically.
     *
     * @param username The username of the account.
     * @param password The password of the account.
     * @param context  The application context.
     */
    private void addAccount(String username, String password, Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = new Account(username, "basic");
        accountManager.addAccountExplicitly(account, password, null);
    }
}

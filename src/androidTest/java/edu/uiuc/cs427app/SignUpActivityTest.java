package edu.uiuc.cs427app;

import android.content.Context;
import android.accounts.Account;
import android.accounts.AccountManager;
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
public class SignUpActivityTest {

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
    public void testValidSignUp() throws InterruptedException {
        // Launch the SignUpActivity
        ActivityScenario.launch(SignUpActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Enter valid username, password, and confirm password
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("sunny123"), ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.passwordEntry)).perform(ViewActions.typeText("newPassword"), ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.confirmPasswordEntry)).perform(ViewActions.typeText("newPassword"), ViewActions.closeSoftKeyboard());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Sign Up button
        onView(withId(R.id.signUp)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that MainActivity is launched
        onView(withId(R.id.textViewHeader)).check(matches(ViewMatchers.isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);
    }


    @Test
    public void testEmptyFieldsSignUp() throws InterruptedException {
        // Launch the SignUpActivity
        ActivityScenario.launch(SignUpActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Sign Up button without entering any data
        onView(withId(R.id.signUp)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that the empty fields dialog appears
        onView(withText("Please ensure you have filled in all fields."))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);
    }

    @Test
    public void testMismatchedPasswordsSignUp() throws InterruptedException {
        // Launch the SignUpActivity
        ActivityScenario.launch(SignUpActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Enter valid username and mismatched password/confirm password
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("newUser"));
        Thread.sleep(2000);
        onView(withId(R.id.passwordEntry)).perform(ViewActions.typeText("password1"));
        Thread.sleep(2000);
        onView(withId(R.id.confirmPasswordEntry)).perform(ViewActions.typeText("password2"));
        Thread.sleep(2000);
        ViewActions.closeSoftKeyboard();

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Sign Up button
        onView(withId(R.id.signUp)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that the mismatched passwords dialog appears
        onView(withText("Your password and confirm password do not match."))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);
    }

    @Test
    public void testExistingUsernameSignUp() throws InterruptedException {
        // Simulate an existing username scenario
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        addAccount("existingUser", "password123", context);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Launch the SignUpActivity
        ActivityScenario.launch(SignUpActivity.class);

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Enter the existing username
        onView(withId(R.id.usernameEntry)).perform(ViewActions.typeText("existingUser"));
        Thread.sleep(2000);
        onView(withId(R.id.passwordEntry)).perform(ViewActions.typeText("password123"));
        Thread.sleep(2000);
        onView(withId(R.id.confirmPasswordEntry)).perform(ViewActions.typeText("password123"));
        Thread.sleep(2000);
        ViewActions.closeSoftKeyboard();

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Click the Sign Up button
        onView(withId(R.id.signUp)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion: Verify that the username already exists dialog appears
        onView(withText("An account with this username already exists."))
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

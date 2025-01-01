package edu.uiuc.cs427app;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uiuc.cs427app.data.model.Coordinates;
import edu.uiuc.cs427app.data.model.Feature;

/**
 * Instrumented test for the app, using Espresso.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    // Rule to launch the MainActivity for testing
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // Test to verify the app context package name
    @Test
    public void useAppContext() {
        // Get the app context
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Verify the package name is as expected
        assertEquals("edu.uiuc.cs427app", appContext.getPackageName());
    }

    // Setup method to clean up files before each test
    @Before
    public void setUp() {
        // Get the app context
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Delete any existing data file to ensure a clean test environment
        appContext.deleteFile("userCities.dat");
    }

    // Teardown method to clean up files after each test
    @After
    public void tearDown() {
        // Get the app context
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Delete the data file to ensure no residual data remains
        appContext.deleteFile("userCities.dat");
    }

    // Helper method to stub data with predefined cities
    private void stubChicagoAndSeattle() {
        // Add Chicago and Seattle to the user's city list
        activityRule.getScenario().onActivity(activity -> {
            Feature chicago = new Feature("Chicago", "city", new Coordinates(-87.623177, 41.881832));
            activity.addCityForUser(chicago);

            Feature seattle = new Feature("Seattle", "city", new Coordinates(122.3328, 47.6061));
            activity.addCityForUser(seattle);

            // Display the added cities
            activity.displayUserCities();
        });
    }

    // Test to add a new city to the app
    @Test
    public void testAddNewCity() throws InterruptedException {
        // Simulate clicking the Add Location button
        onView(withId(R.id.buttonAddLocation)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate clicking on the search bar
        onView(withContentDescription("Search")).perform(click());

        // Enter a search query for "Chicago"
        String query = "Chicago";
        onView(withId(R.id.searchBar)).inRoot(isDialog()).perform(ViewActions.typeText(query));

        // Allow UI operations to settle
        Thread.sleep(5000);

        // Select the first search result
        onData(instanceOf(Feature.class)).atPosition(0).perform(click());

        // Allow UI operations to settle
        Thread.sleep(1000);

        // Simulate clicking the ADD button
        onView(withText("ADD")).perform(ViewActions.click());

        // Allow UI operations to settle
        Thread.sleep(3000);

        // Assertion: Verify that "Chicago" is displayed
        onView(withText(containsString("Chicago"))).check(matches(isDisplayed()));
    }

    // Test to remove an existing city
    @Test
    public void testRemoveExistingCity() throws InterruptedException {
        // Add stub data for Chicago and Seattle
        stubChicagoAndSeattle();

        // Verify "Chicago" is displayed
        onView(withText(containsString("Chicago"))).check(matches(isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate clicking the Delete button for "Chicago"
        onView(allOf(
                withText("Delete"),
                hasSibling(withText("Chicago")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Confirm deletion in the dialog
        onView(withText("YES")).inRoot(isDialog()).perform(click());

        // Allow UI operations to settle
        Thread.sleep(3000);

        // Assertion: Verify that "Chicago" is no longer displayed
        onView(withText(containsString("Chicago"))).check(doesNotExist());

        // Allow UI operations to settle
        Thread.sleep(500);
    }

    // Test to log off the user
    @Test
    public void testUserLogOff() throws InterruptedException {
        // Initialize Intents for intent validation
        Intents.init();

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate clicking the Logout button
        onView(withId(R.id.buttonLogout)).perform(click());

        // Assertion: Verify that the LoginActivity is launched
        intended(hasComponent(LoginActivity.class.getName()));

        // Allow UI operations to settle
        Thread.sleep(3000);

        // Release Intents to clean up
        Intents.release();
    }

    // Test the Weather Insight feature
    @Test
    public void testWeatherInsightFeature() throws InterruptedException {
        // Add stub data for Chicago and Seattle
        stubChicagoAndSeattle();

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate clicking the Weather button for "Chicago"
        onView(allOf(
                withText("Weather"),
                hasSibling(withText("Chicago")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(3000);

        // Simulate clicking the Weather Insights button
        onView(withId(R.id.weatherInsightsButton)).perform(click());

        // Simulate clicking a question button
        Thread.sleep(5000);
        onView(withId(R.id.questionButton1)).perform(click());

        // Allow UI operations to settle
        Thread.sleep(5000);

        // Assertion: Verify that the response text is displayed
        onView(withId(R.id.responseText))
                .check(matches(isDisplayed()));
    }

    // Test the Weather feature for two cities (Seattle and Chicago)
    @Test
    public void testWeatherFeatureForTwoCities() throws InterruptedException {
        // Add stub data for Chicago and Seattle
        stubChicagoAndSeattle();

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate clicking the Weather button for "Seattle"
        onView(allOf(
                withText("Weather"),
                hasSibling(withText("Seattle")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Assertion City 1: Verify that the welcome text displays "Seattle"
        onView(withId(R.id.welcomeText)).check(matches(withText("Seattle")));
        // Assertion City 1: Verify that there is a weather description
        onView(withId(R.id.weatherDescription_text)).check(matches(isDisplayed()));
        // Assertion City 1: Verify that there is a weather icon
        onView(withId(R.id.weatherIcon)).check(matches(isDisplayed()));
        // Assertion City 1: Verify that there is a temperature
        onView(withId(R.id.temperature_text)).check(matches(isDisplayed()));
        // Assertion City 1: Verify that there is a humidity percentage
        onView(withId(R.id.humidity_text)).check(matches(isDisplayed()));
        // Assertion City 1: Verify that there is a wind displayed
        onView(withId(R.id.wind_text)).check(matches(isDisplayed()));

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate pressing the back button
        Espresso.pressBack();

        // Allow UI operations to settle
        Thread.sleep(1000);

        // Simulate clicking the Weather button for "Chicago"
        onView(allOf(
                withText("Weather"),
                hasSibling(withText("Chicago")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(3000);

        // Assertion City 2: Verify that the welcome text displays "Chicago"
        onView(withId(R.id.welcomeText)).check(matches(withText("Chicago")));
        // Assertion City 2: Verify that there is a weather description
        onView(withId(R.id.weatherDescription_text)).check(matches(isDisplayed()));
        // Assertion City 2: Verify that there is a weather icon
        onView(withId(R.id.weatherIcon)).check(matches(isDisplayed()));
        // Assertion City 2: Verify that there is a temperature
        onView(withId(R.id.temperature_text)).check(matches(isDisplayed()));
        // Assertion City 2: Verify that there is a humidity percentage
        onView(withId(R.id.humidity_text)).check(matches(isDisplayed()));
        // Assertion City 2: Verify that there is a wind displayed
        onView(withId(R.id.wind_text)).check(matches(isDisplayed()));
    }

    // Test the Location feature for two cities (Seattle and Chicago)
    @Test
    public void testLocationFeatureForTwoCities() throws InterruptedException {
        // Add stub data for Chicago and Seattle
        stubChicagoAndSeattle();

        // Allow UI operations to settle
        Thread.sleep(1000);

        // Simulate clicking the Map button for "Seattle"
        onView(allOf(
                withText("Map"),
                hasSibling(withText("Seattle")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(5000);

        // Assertion City 1: Verify that the city name and coordinates for "Seattle" are displayed
        onView(withId(R.id.cityNameTextView)).check(matches(withText("Seattle")));
        onView(withId(R.id.latLongTextView)).check(matches(withText("Latitude: 47.6061, Longitude: 122.3328")));

        // Assertion: City 1 Verify that the map WebView URL contains the coordinates for "Seattle"
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("47.6061")));
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("122.3328")));

        // Simulate pressing the back button
        Espresso.pressBack();

        // Allow UI operations to settle
        Thread.sleep(3000);

        // Simulate clicking the Map button for "Chicago"
        onView(allOf(
                withText("Map"),
                hasSibling(withText("Chicago")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(5000);

        // Assertion City 2: Verify that the city name and coordinates for "Chicago" are displayed
        onView(withId(R.id.cityNameTextView)).check(matches(withText("Chicago")));
        onView(withId(R.id.latLongTextView)).check(matches(withText("Latitude: 41.881832, Longitude: -87.623177")));

        // Assertion City 2: Verify that the map WebView URL contains the coordinates for "Chicago"
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("41.881832")));
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("-87.623177")));
    }

    // Test mocking the location for a city
    @Test
    public void testMockLocation() throws InterruptedException {
        // Add an ActivityMonitor for MapActivity
        Instrumentation.ActivityMonitor monitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(MapActivity.class.getName(), null, false);

        // Add stub data for Chicago
        stubChicagoAndSeattle();

        // Allow UI operations to settle
        Thread.sleep(2000);

        // Simulate clicking the Map button for "Chicago"
        onView(allOf(
                withText("Map"),
                hasSibling(withText("Chicago")),
                isDisplayed()
        )).perform(click());

        // Allow UI operations to settle
        Thread.sleep(8000);

        // Wait for MapActivity to be launched
        MapActivity mapActivity = (MapActivity) InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        // Assertion: Verify that the MapActivity was launched
        assertNotNull("MapActivity was not launched", mapActivity);

        // Verify initial city details are Chicago
        onView(withId(R.id.cityNameTextView)).check(matches(withText("Chicago")));
        onView(withId(R.id.latLongTextView)).check(matches(withText("Latitude: 41.881832, Longitude: -87.623177")));
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("41.881832")));
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("-87.623177")));

        // Mock the city name and coordinates within MapActivity to Champaign
        mapActivity.runOnUiThread(() -> {
            mapActivity.configureWithCity("Champaign", 40.1164, -88.2434);
        });

        // Allow UI operations to settle
        Thread.sleep(8000);

        // Assertion: Verify the mocked city name and coordinates are displayed
        onView(withId(R.id.cityNameTextView)).check(matches(withText("Champaign")));
        onView(withId(R.id.latLongTextView)).check(matches(withText("Latitude: 40.1164, Longitude: -88.2434")));
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("40.1164")));
        onWebView(withId(R.id.mapWebView)).check(webMatches(getCurrentUrl(), containsString("-88.2434")));
    }
}

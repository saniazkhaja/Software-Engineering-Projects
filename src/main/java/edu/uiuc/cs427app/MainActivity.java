package edu.uiuc.cs427app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
//
//// Class that sets up the main page after logging in
//// This includes a list of locations,adding/removing cities, logging out, setting and more
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private LinearLayout locationLayout; // Layout to display the list of cities
//    private HashMap<String, ArrayList<String>> userCities; //Store per-user city list in hash map
//    private String signedInUsername; // Username of the signed-in user
//
//    // Sets up the page including the user's location list, chosen theme and relevant buttons
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Set up the title with the user's name if passed in the Intent
//        Intent intent = getIntent();
//        signedInUsername = intent.getStringExtra("username");
//        if (signedInUsername != null) {
//            setTitle("Team 52 - " + signedInUsername);
//        }
//
//        applyTheme(); // Apply user-selected theme
//
//        setContentView(R.layout.activity_main);
//
//        // Initialize the layout container for dynamic location buttons
//        locationLayout = findViewById(R.id.locationLayout);
//
//        // Set up the "Add Location", "Logout" and "Setting" buttons
//        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);
//        Button buttonLogout = findViewById(R.id.buttonLogout);
//        Button buttonSettings = findViewById(R.id.buttonSettings);
//
//        // Set click listeners for the buttons
//        buttonLogout.setOnClickListener(this);
//        buttonAddLocation.setOnClickListener(this);
//        buttonSettings.setOnClickListener(this);
//
//        // Initialize the userCities HashMap
//        userCities = new HashMap<>();
//
//        // Current implementation: store in local file
//        // Read user cities from file
//        readUserCitiesFromFile();
//
//        // Display the cities for the current user
//        displayUserCities();
//    }
//
//    // Handle button click events for adding location, logging out and settings
//    @Override
//    public void onClick(View view) {
//        // Show dialog to add a new location
//        if (view.getId() == R.id.buttonAddLocation) {
//            showAddLocationDialog();
//        }
//
//        // Navigate back to the LoginActivity
//        if (view.getId() == R.id.buttonLogout) {
//            Intent intent;
//            intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }
//
//        // Navigate to the SettingsUIActivity, passing the username
//        if (view.getId() == R.id.buttonSettings) {
//            Intent intent;
//            intent = new Intent(this, SettingsUIActivity.class);
//            intent.putExtra("username", signedInUsername);
//            startActivity(intent);
//        }
//    }
//
//    // Used when the user wants to add a location (clicks add location button) and gets location through a input dialog
//    private void showAddLocationDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Add New Location");
//
//        final EditText input = new EditText(this);
//        input.setHint("Enter city name");
//        input.setHintTextColor(Color.GRAY);
//        input.setTextColor(Color.GRAY);
//        builder.setView(input);
//
//        builder.setPositiveButton("Add", (dialog, which) -> {
//            String cityNameInput = input.getText().toString().trim();
//            if (!cityNameInput.isEmpty()) {
//                String cityName = capitalizeCityName(cityNameInput);
//                ArrayList<String> cities = userCities.get(signedInUsername);
//                if (cities == null) {  // can't do .contains if does not exist
//                    addCityForUser(cityName);  // Add city to user's list
//                    addLocationButton(cityName); // Add city button to UI
//                } else if (!cities.contains(cityName)) {
//                    addCityForUser(cityName);  // Add city to user's list
//                    addLocationButton(cityName); // Add city button to UI
//                } else {
//                    showToastWithCustomColor(cityName + " is already added");
//                }
//            } else {
//                showToastWithCustomColor("City name cannot be empty");
//            }
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//        builder.show();
//    }
//
//    // Helper method to show toast with custom text color
//    private void showToastWithCustomColor(String message) {
//        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
//        View toastView = toast.getView();
//
//        // Check if the view is a TextView or contains a TextView
//        if (toastView instanceof TextView) {
//            ((TextView) toastView).setTextColor(Color.GRAY); // Set to light gray for readability
//        } else if (toastView instanceof LinearLayout) {
//            TextView toastMessage = (TextView) ((LinearLayout) toastView).getChildAt(0);
//            toastMessage.setTextColor(Color.GRAY);
//        }
//
//        toast.show();
//    }
//
//
//    // Sets up each location inputted (each user location in the list) with a details and delete button
//    private void addLocationButton(String cityName) {
//        // Create a horizontal LinearLayout for each city entry
//        LinearLayout cityLayout = new LinearLayout(this);
//        cityLayout.setOrientation(LinearLayout.HORIZONTAL);
//        cityLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        cityLayout.setPadding(8, 8, 8, 8);
//        cityLayout.setGravity(Gravity.CENTER_VERTICAL);
//
//        // Create a TextView for the city name, styled to be larger, bold, and left-aligned
//        TextView cityTextView = new TextView(this);
//        cityTextView.setText(cityName);
//        // layout style
//        cityTextView.setLayoutParams(new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 to push buttons to the right
//        cityTextView.setTextSize(18); // Increase font size
//        cityTextView.setTypeface(null, android.graphics.Typeface.BOLD); // Bold text
//        cityTextView.setPadding(16, 0, 0, 0);
//
//        // Create the "Details" button using createButton method
//        Button showDetailsButton = createButton(
//                "Details",
//                Color.WHITE,
//                Color.BLUE,
//                v -> navigateToDetails(cityName) // Navigate to details page for this city
//        );
//
//        // Create the "Delete" button using createButton method
//        Button deleteButton = createButton(
//                "Delete",
//                Color.WHITE,
//                Color.RED,
//                v -> showDeleteConfirmationDialog(cityName, cityLayout) // Show confirmation before deleting
//        );
//
//        // Add the TextView and buttons to the cityLayout
//        cityLayout.addView(cityTextView);
//        cityLayout.addView(showDetailsButton);
//        cityLayout.addView(deleteButton);
//
//        // Add the city layout to the locationLayout container
//        locationLayout.addView(cityLayout);
//    }
//
//    // Properly formats each city name by making sure that only first letter is capitalized
//    private String capitalizeCityName(String cityName) { // Make sure the first letter is upper case, the rest lowercase
//        if (cityName.length() == 0) {
//            return cityName;
//        }
//        return cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
//    }
//
//    // Adds each unique city that the user added to a hashmap
//    private void addCityForUser(String cityName) { // Add it to the hashmap
//        ArrayList<String> cities = userCities.get(signedInUsername);
//        if (cities == null) {
//            cities = new ArrayList<>();
//        }
//        // Add the city if it's not already in the list
//        if (!cities.contains(cityName)) {
//            cities.add(cityName);
//            userCities.put(signedInUsername, cities);
//            // Write the updated data to the file
//            writeUserCitiesToFile();
//        }
//    }
//
//    // Used to show a confirmation dialog before removing a city
//    private void showDeleteConfirmationDialog(String cityName, LinearLayout cityLayout) {
//        // alerting user which city they are deleting and asking for confirmation
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Remove City")
//                .setMessage("Are you sure you want to remove " + cityName + " from your locations?")
//                // checks to see if confirmed yes so can go ahead and delete location from user list
//                .setPositiveButton("Yes", (dialogInterface, which) -> {
//                    // Remove the city from the user's list
//                    removeCityForUser(cityName);
//                    // Remove the city layout from the UI
//                    locationLayout.removeView(cityLayout);
//                    // Show toast with custom color
//                    showToastWithCustomColor(cityName + " removed successfully");
//                })
//                // user decides to not delete city
//                .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss())
//                .create();
//
//        // Show the dialog first, then customize the message color
//        dialog.show();
//
//        // Find the dialog message TextView and set its color to gray
//        TextView messageView = dialog.findViewById(android.R.id.message);
//        if (messageView != null) {
//            messageView.setTextColor(Color.GRAY);
//        }
//    }
//
//    // Removes city from the user's list in the hashmap
//    private void removeCityForUser(String cityName) {
//        ArrayList<String> cities = userCities.get(signedInUsername);
//        if (cities != null) {
//            cities.remove(cityName);
//            userCities.put(signedInUsername, cities);
//            // Write the updated data to the file
//            writeUserCitiesToFile();
//        }
//    }
//
//    // Creates a button with specified text, text color, background color, and click listener
//    private Button createButton(String text, int textColor, int backgroundColor, View.OnClickListener listener) {
//        Button button = new Button(this);
//        button.setText(text);
//        button.setTextColor(textColor);
//        button.setBackgroundColor(backgroundColor);
//        button.setPadding(16, 8, 16, 8); // Padding inside the button
//
//        // Set margins for the button
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(16, 0, 0, 0);
//        button.setLayoutParams(params);
//
//        button.setOnClickListener(listener);
//        return button;
//    }
//
//    // Navigates to the DetailsActivity for the selected city
//    private void navigateToDetails(String cityName) {
//        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
//        intent.putExtra("city", cityName);
//        intent.putExtra("username", signedInUsername);
//        startActivity(intent);
//    }
//
//    // Reads user cities from a local file
//    private void readUserCitiesFromFile() {
//        try {
//            FileInputStream fis = openFileInput("userCities.dat");
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            userCities = (HashMap<String, ArrayList<String>>) ois.readObject();
//            ois.close();
//            fis.close();
//        } catch (FileNotFoundException e) {
//            // File not found, initialize empty HashMap
//            userCities = new HashMap<>();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//            userCities = new HashMap<>();
//        }
//    }
//
//    // Writes user cities to a local file to persist data
//    private void writeUserCitiesToFile() { //write to local file to store city list
//        try {
//            FileOutputStream fos = openFileOutput("userCities.dat", Context.MODE_PRIVATE);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(userCities);
//            oos.close();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Fetch cities for the user and display them in the UI
//    private void displayUserCities() { // Fetch city for a user and display it
//        // Clear any existing views
//        locationLayout.removeAllViews();
//
//        // Get the city list for the current user
//        ArrayList<String> cities = userCities.get(signedInUsername);
//        if (cities != null) {
//            for (String cityName : cities) {
//                addLocationButton(cityName);
//            }
//        }
//    }
//
//    // Method to apply user theme based on selection
//    protected void applyTheme() {
//        //retrieve sharedPreferences
//        SharedPreferences preferences = getSharedPreferences("UserPreferences",
//                MODE_PRIVATE);
//
//        // Retrieve shared preferences to get the user-selected theme
//        String theme = preferences.getString(signedInUsername, "Light");
//
//        // Determine the user's selected theme and apply it
//        switch(theme) {
//            case "Dark":
//                setTheme(R.style.Theme_MyFirstApp_Dark);
//                break;
//            case "HighContrast":
//                setTheme(R.style.Theme_MyFirstApp_HighContrast);
//                break;
//            case "UIUC":
//                setTheme(R.style.Theme_MyFirstApp_UIUC);
//                break;
//            default:
//                setTheme(R.style.Theme_MyFirstApp);
//                break;
//        }
//    }
//}
//
//

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uiuc.cs427app.data.model.Feature;
import edu.uiuc.cs427app.data.model.GeocodingAPIResponse;

// Class that sets up the main page after logging in
// This includes a list of locations,adding/removing cities, logging out, setting and more
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout locationLayout;
    private HashMap<String, ArrayList<Feature>> userCities; //Store per-user city list in hash map
    private String signedInUsername;
    private Feature selectedCity = null; // Variable to store the selected city temporarily

    // Sets up the page including the user's location list, chosen theme and relevant buttons
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the title with the user's name if passed in the Intent
        Intent intent = getIntent();
        signedInUsername = intent.getStringExtra("username");
        if (signedInUsername != null) {
            setTitle("Team 52 - " + signedInUsername);
        }

        applyTheme(); // Apply user-selected theme
        setContentView(R.layout.activity_main);

        // Initialize the layout container for dynamic location buttons
        locationLayout = findViewById(R.id.locationLayout);

        // Set up the "Add Location", "Logout" and "Setting" buttons
        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonSettings = findViewById(R.id.buttonSettings);

        // Set click listeners for the buttons
        buttonLogout.setOnClickListener(this);
        buttonAddLocation.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);

        // Initialize the userCities HashMap
        userCities = new HashMap<>();

        // Current implementation: store in local file
        // Read user cities from file
        readUserCitiesFromFile();

        // Display the cities for the current user
        displayUserCities();
    }

    private List<Feature> allCities;
    private ArrayAdapter<Feature> cityAdapter;

    // Initialize available city names
    private void initializeCities() {
        allCities = new ArrayList<>();
    }

    // Handle button click events for adding location, logging out and settings
    @Override
    public void onClick(View view) {
        // Show dialog to add a new location
        if (view.getId() == R.id.buttonAddLocation) {
            showAddLocationDialog();
        }

        // Navigate back to the LoginActivity
        if (view.getId() == R.id.buttonLogout) {
            Intent intent;
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Navigate to the SettingsUIActivity, passing the username
        if (view.getId() == R.id.buttonSettings) {
            Intent intent;
            intent = new Intent(this, SettingsUIActivity.class);
            intent.putExtra("username", signedInUsername);
            startActivity(intent);
        }
    }

    // Dialog viewer to add locations
    private void showAddLocationDialog() {
        // Create an AlertDialog for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Location");

        // Inflate a layout containing the SearchView
        View dialogView = getLayoutInflater().inflate(R.layout.search_view_city, null);
        builder.setView(dialogView);

        // Initialize the sample cities list
        initializeCities();

        // Find the SearchView and ListView in the inflated layout
        SearchView searchView = dialogView.findViewById(R.id.searchBar);
        ListView cityListView = dialogView.findViewById(R.id.cityListView);

        // Apply color to SearchView
        searchView.setBackgroundColor(ContextCompat.getColor(this, R.color.search_bar_background_light));

        // extract all cities
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allCities);
        cityListView.setAdapter(cityAdapter);
        // Hide all city names in search view
        cityListView.setVisibility(View.GONE);

        // Set up SearchView listener to filter the list
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Called when the user submits a query in the SearchView.
             * @param query the text entered by the user in the SearchView.
             * @return true if the event is handled, false otherwise.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }

            /**
             * Called when the query text in the SearchView is changed by the user.
             * @param newText the updated text entered by the user in the SearchView.
             * @return true if the event is handled, false otherwise.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                searchGeocodingAPI(newText);
                // Show ListView if there is input, otherwise hide it
                if (newText.trim().isEmpty()) {
                    cityListView.setVisibility(View.GONE);
                } else {
                    cityListView.setVisibility(View.VISIBLE);
                    //cityAdapter.getFilter().filter(newText);
                }
                return false;
            }

        });
        // select the city form search view
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCity = cityAdapter.getItem(position); // Store the selected city
            if (selectedCity != null) {
                Toast.makeText(this, "Selected City: " + selectedCity, Toast.LENGTH_SHORT).show();
            }
            // Hide the ListView after selection
            cityListView.setVisibility(View.GONE);
            searchView.setQuery(selectedCity.getProperties().getFullAddress(), false); // Display selected city in SearchView
        });

        // Set up the dialog buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            if (selectedCity == null) {
                Toast.makeText(this, "Please select a city first", Toast.LENGTH_SHORT).show();
            } else if (!allCities.contains(selectedCity)) {
                // Check if selectedCity exists in allCities
                Toast.makeText(this, "City does not exist", Toast.LENGTH_SHORT).show();
            } else {
                // Refresh the user cities data to check the latest list
                readUserCitiesFromFile();
                ArrayList<Feature> cities = userCities.get(signedInUsername);

                // Check if city already exists
                if (cities != null && cities.contains(selectedCity)) {
                    Toast.makeText(this, "City already exists", Toast.LENGTH_SHORT).show();
                } else {
                    addCityForUser(selectedCity);
                    addLocationButton(selectedCity);
                    Toast.makeText(this, selectedCity + " added to your list", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // exit from dialogue box
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        // Show the dialog
        builder.show();
    }

    // creating space/view for added cities
    private void addLocationButton(Feature cityName) {
        // Create a horizontal LinearLayout for each city entry
        LinearLayout cityLayout = new LinearLayout(this);
        cityLayout.setOrientation(LinearLayout.HORIZONTAL);
        cityLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cityLayout.setPadding(8, 8, 8, 8);
        cityLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Create a TextView for the city name, styled to be larger, bold, and left-aligned
        TextView cityTextView = new TextView(this);
        cityTextView.setText(cityName.toString());
        cityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 to push buttons to the right
        cityTextView.setTextSize(18); // Increase font size
        cityTextView.setTypeface(null, android.graphics.Typeface.BOLD); // Bold text
        cityTextView.setPadding(16, 0, 0, 0);

        // Create the "Weather" button using createButton method
        Button showDetailsButton = createButton(
                "Weather",
                Color.WHITE,
                Color.BLUE,
                v -> navigateToDetails(cityName)
        );

        // Create the "Delete" button using createButton method
        Button deleteButton = createButton(
                "Delete",
                Color.WHITE,
                Color.RED,
                v -> showDeleteConfirmationDialog(cityName, cityLayout)
        );
        // Create the "Map" button
        Button mapButton = createButton(
                "Map",
                Color.WHITE,
                Color.GREEN,
                v -> navigateToMap(cityName)
        );

        // Add the Map button to the cityLayout
        // Add the TextView and buttons to the cityLayout
        cityLayout.addView(cityTextView);
        cityLayout.addView(mapButton);
        cityLayout.addView(showDetailsButton);
        cityLayout.addView(deleteButton);

        // Add the city layout to the locationLayout container
        locationLayout.addView(cityLayout);
    }

    // capitalize city names
    private String capitalizeCityName(String cityName) { // Make sure the first letter is upper case, the rest lowercase
        if (cityName.length() == 0) {
            return cityName;
        }
        return cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
    }

    // update user selected city list
    void addCityForUser(Feature cityName) { // Add it to the hashmap
        ArrayList<Feature> cities = userCities.get(signedInUsername);
        if (cities == null) {
            cities = new ArrayList<>();
        }
        // Add the city if it's not already in the list
        if (!cities.contains(cityName)) {
            cities.add(cityName);
            userCities.put(signedInUsername, cities);
            // Write the updated data to the file
            writeUserCitiesToFile();
        }
    }

    // Code for removal of city from layout list
    private void showDeleteConfirmationDialog(Feature cityName, LinearLayout cityLayout) {
        // Alert for when deleting a city and based on user option will update UI
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Remove City")
                .setMessage("Are you sure you want to remove " + cityName + " from your locations?")
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    // Remove the city from the user's list
                    removeCityForUser(cityName);
                    // Remove the city layout from the UI
                    locationLayout.removeView(cityLayout);
                    Toast.makeText(this, cityName + " removed successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss())
                .show();

        // Change the color of the message text to gray
        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTextColor(Color.GRAY); // Set to gray
        }
    }

    // remove the city from user list
    private void removeCityForUser(Feature cityName) {
        ArrayList<Feature> cities = userCities.get(signedInUsername);
        if (cities != null) {
            cities.remove(cityName);
            userCities.put(signedInUsername, cities);
            // Write the updated data to the file
            writeUserCitiesToFile();
        }
    }

    // template code for buttons to create buttons and set the UI of it
    private Button createButton(String text, int textColor, int backgroundColor, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(textColor);
        button.setBackgroundColor(backgroundColor);
        button.setPadding(16, 8, 16, 8); // Padding inside the button

        // Set margins for the button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 0, 0);
        button.setLayoutParams(params);

        button.setOnClickListener(listener);
        return button;
    }

    // Used to navigate from Main page to details (weather info) with relevant details sent as intents
    private void navigateToDetails(Feature cityName) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("city", cityName.toString());
        intent.putExtra("latitude", cityName.getProperties().getCoordinates().getLatitude());
        intent.putExtra("longitude", cityName.getProperties().getCoordinates().getLongitude());
        intent.putExtra("username", signedInUsername);
        startActivity(intent);
    }

    // read user selected cities from hashmap
    private void readUserCitiesFromFile() {
        try {
            FileInputStream fis = openFileInput("userCities.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            userCities = (HashMap<String, ArrayList<Feature>>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            // File not found, initialize empty HashMap
            userCities = new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            userCities = new HashMap<>();
        }
    }

    // write new user selected cities to hashmap file
    private void writeUserCitiesToFile() { //write to local file to store city list
        try {
            FileOutputStream fos = openFileOutput("userCities.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userCities);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // show user selected cities in specified layout
    void displayUserCities() { // Fetch city for a user and display it
        // Clear any existing views
        locationLayout.removeAllViews();

        // Get the city list for the current user
        ArrayList<Feature> cities = userCities.get(signedInUsername);
        if (cities != null) {
            for (Feature cityName : cities) {
                addLocationButton(cityName);
            }
        }
    }

    // Method to apply user theme based on selection
    protected void applyTheme() {
        //retrieve sharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences",
                MODE_PRIVATE);

        // Retrieve shared preferences to get the user-selected theme
        String theme = preferences.getString(signedInUsername, "Light");

        // Determine the user's selected theme and apply it
        switch(theme) {
            case "Dark":
                setTheme(R.style.Theme_MyFirstApp_Dark);
                break;
            case "HighContrast":
                setTheme(R.style.Theme_MyFirstApp_HighContrast);
                break;
            case "UIUC":
                setTheme(R.style.Theme_MyFirstApp_UIUC);
                break;
            default:
                setTheme(R.style.Theme_MyFirstApp);
                break;
        }
    }

    // Method to search for geocoding information using the Mapbox API
    // Used when user searches for a location and it shows up
    void searchGeocodingAPI(String cityName) {
        // Start a new thread to perform network operations
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do network action in this function
                System.out.println(cityName);
                HttpURLConnection urlConnection = null;
                try {
                    // Construct the Mapbox geocoding API URL with the city name and access token
                    URL url = new URL("https://api.mapbox.com/search/geocode/v6/forward?q=" + cityName + "&access_token=pk.eyJ1Ijoicm9zZW4xNCIsImEiOiJjbTM2OHowZnowMndjMmxvY24zMTZlZ3YyIn0.bgQVYNeuPI72YNXzjac_ng&types=place");
                    System.out.println(url);
                    // Open a connection to the API endpoint
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    // Get the input stream from the connection to read the API response
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    // Parse the JSON response using Gson into a GeocodingAPIResponse object
                    Reader reader = new InputStreamReader(in, "UTF-8");
                    GeocodingAPIResponse result  = new Gson().fromJson(reader, GeocodingAPIResponse.class);

                    // Update the UI on the main thread with the fetched data
                    runOnUiThread(new Runnable() {
                        @Override
                        // Update cities in serach box output
                        public void run() {
                            allCities = result.getFeatures();
                            cityAdapter.clear();
                            cityAdapter.addAll(result.getFeatures());
                            cityAdapter.notifyDataSetChanged();
                        }
                    });


                } catch(Exception e) {
                    System.out.println(e);
                } finally {
                    urlConnection.disconnect();

                }
            }
        }).start();
    }

    // Method to navigate to the MapActivity with selected city details
    private void navigateToMap(Feature cityName) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("city", cityName.toString());
        intent.putExtra("latitude", cityName.getProperties().getCoordinates().getLatitude());
        intent.putExtra("longitude", cityName.getProperties().getCoordinates().getLongitude());
        SharedPreferences preferences = getSharedPreferences("UserPreferences",
                MODE_PRIVATE);
        intent.putExtra("theme", preferences.getString(signedInUsername, "Light"));
        //intent.putExtra("city", cityName.getProperties().getFullAddress());
        startActivity(intent);
    }
}
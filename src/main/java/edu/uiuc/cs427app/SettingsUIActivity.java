package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;

/**
 * This class allows users to select and apply a theme from the settings menu within the app.
 * The class connects with the layout file activity_settingsui and associates each radio button
 * with a specific theme. Themes include Light, Dark, High Contrast and UIUC themes.
 */
public class SettingsUIActivity extends AppCompatActivity {

    private String username;
    private SharedPreferences preferences;

    // Sets up the Settings page with radio buttons to select Theme and submit button while showing current selection
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize shared preferences and get the username
        preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        username = getIntent().getStringExtra("username");

        // Apply the theme based on saved preference before setting content view
        applySavedTheme();

        // Set the title with the username, if available
        if (username != null) {
            setTitle("Team 52 - " + username);
        }

        // Initialize the UI
        initializeUI();
    }

    // Applies the theme that is saved in the user's preferencesSet
    private void applySavedTheme() {
        String theme = preferences.getString(username, "Light"); // Default theme is Light

        // Switch between themes based on the saved preference
        switch (theme) {
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

    // Initializes UI elements, configures the theme selection radio buttons and listens and applies theme changes.
    private void initializeUI() {
        setContentView(R.layout.activity_settingsui);

        // Initialize UI elements
        RadioGroup themeGroup = findViewById(R.id.radioGroupTheme);
        RadioButton lightTheme = findViewById(R.id.radioButtonLight);
        RadioButton darkTheme = findViewById(R.id.radioButtonDark);
        RadioButton highContrastTheme = findViewById(R.id.radioButtonHighContrast);
        RadioButton uiucTheme = findViewById(R.id.radioButtonUofI);
        Button submitUI = findViewById(R.id.buttonSubmitUI);

        // Get the saved theme preference to set the initial selection
        String theme = preferences.getString(username, "Light");

        // Used to adjust text color for radio buttons based on theme
        RadioButton radioButtonDark = findViewById(R.id.radioButtonDark);
        RadioButton radioButtonLight = findViewById(R.id.radioButtonLight);
        RadioButton radioButtonHighContrast = findViewById(R.id.radioButtonHighContrast);
        RadioButton radioButtonUofI = findViewById(R.id.radioButtonUofI);

        // Get and apply saved theme preference
        switch (theme) {
            case "Dark":
                radioButtonDark.setTextColor(Color.WHITE);
                radioButtonLight.setTextColor(Color.WHITE);
                radioButtonHighContrast.setTextColor(Color.WHITE);
                radioButtonUofI.setTextColor(Color.WHITE);
                darkTheme.setChecked(true);
                break;
            case "HighContrast":
                highContrastTheme.setChecked(true);
                break;
            case "UIUC":
                radioButtonDark.setTextColor(Color.WHITE);
                radioButtonLight.setTextColor(Color.WHITE);
                radioButtonHighContrast.setTextColor(Color.WHITE);
                radioButtonUofI.setTextColor(Color.WHITE);
                uiucTheme.setChecked(true);
                break;
            default:
                lightTheme.setChecked(true);
                break;
        }

        // Listen for radio button changes and apply the theme immediately
        themeGroup.setOnCheckedChangeListener((group, checkedID) -> {
            String selectedTheme = "Light";
            // Determine the selected theme based on checked radio button ID
            if (checkedID == darkTheme.getId()) {
                selectedTheme = "Dark";
            } else if (checkedID == highContrastTheme.getId()) {
                selectedTheme = "HighContrast";
            } else if (checkedID == uiucTheme.getId()) {
                selectedTheme = "UIUC";
            }

            // Save the selected theme in preferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(username, selectedTheme);
            editor.apply();

            // Recreate the activity to apply the theme change across all UI elements
            recreate();
        });

        // Configure button to return to MainActivity
        submitUI.setOnClickListener(v -> {
            Intent intent_toMainActivity = new Intent(this, MainActivity.class);
            intent_toMainActivity.putExtra("username", username); // Pass username to MainActivity
            startActivity(intent_toMainActivity); // Start MainActivity
            finish(); // Close SettingsUIActivity
        });
    }
}


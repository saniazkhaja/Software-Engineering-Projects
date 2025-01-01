package edu.uiuc.cs427app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Class that allows a user to sign up
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // Stores the currently selected theme, set to the light mode theme by default
    private String theme = "Light";

    // Sets up the sign up page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view for the activity
        setContentView(R.layout.activity_signup);

        // Adjust layout padding based on system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Let user select from the available themes
        Spinner themeSpinner = findViewById(R.id.themeSelection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.themes, android.R.layout.simple_spinner_item);
        themeSpinner.setAdapter(adapter);

        // Set up the listener for theme selection
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Gets selected theme
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theme = parent.getItemAtPosition(position).toString(); // Update selected theme
            }

            // Sets to default theme, when no manual selection
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                theme = "Light"; // Default theme if none selected
            }
        });

        // Register listeners for when the go back to login or sign up buttons are pressed
        Button goBackToLoginButton = findViewById(R.id.goBackToLogin);
        Button signUpButton = findViewById(R.id.signUp);

        goBackToLoginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    /**
     * Retrieves the current username entered by the user.
     * @return The username as a String.
     */
    String currentUsername() {
        TextView usernameTextField = (TextView) findViewById(R.id.usernameEntry);
        return usernameTextField.getText().toString();
    }

    /**
     * Retrieves the current password entered by the user.
     * @return The password as a String.
     */
    String currentPassword() {
        TextView passwordTextField = (TextView) findViewById(R.id.passwordEntry);
        return passwordTextField.getText().toString();
    }

    /**
     * Retrieves the confirmation password entered by the user.
     * @return The confirmation password as a String.
     */
    String currentConfirmPassword() {
        TextView passwordTextField = (TextView) findViewById(R.id.confirmPasswordEntry);
        return passwordTextField.getText().toString();
    }

    // Handle button click events for log in button and sign up button
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.goBackToLogin) {
            finish();
        } else if (view.getId() == R.id.signUp) {
            signUpAccount();
        }
    }

    /**
     * Creates a new user account if input is valid and moves to the main activity.
     */
    private void signUpAccount() {
        // Retrieve the username and password for the sign-up process
        String username = currentUsername();
        String password = currentPassword();

        // Show an alert if the user did not fill in all 3 fields
        if (currentUsername().isEmpty() || currentPassword().isEmpty() || currentConfirmPassword().isEmpty()) {
            // alerts user that some fields are still empty
            new AlertDialog.Builder(this).setTitle("Please ensure you have filled in all fields.")
                    .setMessage("Please try again.")
                    // this is the alert
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        // checks for clicks
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
            return; // Exit the method if fields are empty
        }

        // Make sure that this username is not already being used by another user
        if (accountAlreadyExists(currentUsername())) {
            // alerts user that the username is already taken
            new AlertDialog.Builder(this).setTitle("An account with this username already exists.")
                    .setMessage("Please try again.")
                    // this is the alert
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        // checks for clicks
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
            return;
        }

        // Make sure that the password field and confirm password fields match
        if (!currentPassword().equals(currentConfirmPassword())) {
            // alerts user that their passwords do not match
            new AlertDialog.Builder(this).setTitle("Your password and confirm password do not match.")
                    .setMessage("Please try again.")
                    // this is the alert
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        // checks for clicks
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
            return;
        }

        // Create an account for the user in Android AccountManager
        AccountManager accountManager = AccountManager.get(this);
        Account account = new Account(username, "basic");
        accountManager.addAccountExplicitly(account, password, null); // Add account

        setUserTheme(account); // Set the selected theme for the use
        moveToMainActivity(); // Navigate to the main activity after successful sign-up
    }

    /**
     * Checks if an account with the provided username already exists.
     * @param username The username to check.
     * @return True if the account exists, false otherwise.
     */
    private boolean accountAlreadyExists(String username) {
        // If we see an existing account with this username, return true, otherwise, return false.
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("basic");
        // Goes through all accounts
        for (Account account : accounts) {
            if (account.name.equals(username)) {
                return true; // Return true if the account is found
            }
        }
        return false;  // Return false if no matching account is found
    }

    /**
     * Retrieves the theme preference for a specific account.
     * @param account The account for which to retrieve the theme.
     * @return The theme as a String.
     */
    private String getUserTheme(Account account) {
        AccountManager accountManager = AccountManager.get(this);
        return accountManager.getUserData(account, "theme");
    }

    /**
     * Sets the theme preference for the newly created account.
     * @param account The account for which to set the theme.
     */
    private void setUserTheme(Account account) {
//        AccountManager accountManager = AccountManager.get(this);
//        accountManager.setUserData(account, "theme", theme);

        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(currentUsername(), theme); // Using username as part of key
        editor.apply();  // Commit changes
    }

    /**
     * Navigates to the main activity, passing the current username.
     */
    void moveToMainActivity() {
        Intent moveToMainIntent = new Intent(this, MainActivity.class);
        moveToMainIntent.putExtra("username", currentUsername());
        startActivity(moveToMainIntent); // Start the main activity
    }
}
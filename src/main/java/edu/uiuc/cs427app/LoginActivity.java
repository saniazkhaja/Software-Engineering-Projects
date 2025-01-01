package edu.uiuc.cs427app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Class for the login page which allows users to login
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Sets up the login page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializes the activity and set up the user interface
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Apply window insets to ensure the main layout is correctly padded
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Setup listeners so we know when the log in or sign up button are tapped
        Button logInButton = findViewById(R.id.logIn);
        Button signUpButton = findViewById(R.id.signUp);

        // Set click listeners for the buttons
        logInButton.setOnClickListener(this);
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

    // Get user clicks and act on it
    @Override
    public void onClick(View view) {
        // Handle button click events
        if (view.getId() == R.id.logIn) {
            System.out.println("login " + currentUsername() + " " + currentPassword());
            // Validate user credentials
            validateUserAccount();
        } else if (view.getId() == R.id.signUp) {
            // Navigate to the sign-up activity
            moveToSignUpActivity();

        }
    }

    /**
     * Navigates to the sign-up activity, passing the current username.
     */
    void moveToSignUpActivity() {
        Intent moveToMainIntent = new Intent(this, SignUpActivity.class);
        moveToMainIntent.putExtra("username", currentUsername());
        startActivity(moveToMainIntent);
    }

    /**
     * Validates the user account by checking entered credentials.
     * Shows a dialog if fields are empty or credentials are invalid.
     */
    void validateUserAccount() {
        // Check if username and password fields are empty
        if (currentUsername().isEmpty() || currentPassword().isEmpty()) {
            // Show alert dialog if either field is empty
            new AlertDialog.Builder(this).setTitle("Please ensure you have entered both a username and a password")
                    .setMessage("Please try again.")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
            return;
        }

        // Go through every Account saved and if we have a matching username and password, log the user in
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("basic");

        // Check for a matching account
        for (Account account : accounts) {
            if (account.name.equals(currentUsername())) {
                // Validate the password for the matched account
                if (accountManager.getPassword(account).equals(currentPassword())) {
                    // If credentials are valid, move to the main activity
                    moveToMainActivity();
                    return;
                }
            }
        }

        // If we do not find a valid account, we show the invalid prompt.
        showInvalidPasswordPrompt();
    }

    /**
     * Displays an alert dialog when the username or password is invalid.
     */
    void showInvalidPasswordPrompt() {
        // to display a dialog for invalid credentials
        new AlertDialog.Builder(this).setTitle("Invalid username or password")
                .setMessage("Please try again.")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    // for onclicks
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    /**
     * Navigates to the main activity, passing the current username.
     */
    void moveToMainActivity() {
        Intent moveToMainIntent = new Intent(this, MainActivity.class);
        moveToMainIntent.putExtra("username", currentUsername());
        startActivity(moveToMainIntent);
    }
}
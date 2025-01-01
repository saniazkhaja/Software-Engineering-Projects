package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

// activity to display and use the Gemeni API to get weather insights based on User Query
public class WeatherInsightsActivity extends AppCompatActivity {

    // UI elements
    private LinearLayout questionsContainer;
    private TextView responseText;
    private EditText customQuestionInput;
    private Button questionButton1;
    private Button questionButton2;
    private Button submitCustomQuestionButton;

    // API key for Gemini API  service
    private String APIKEY = "AIzaSyBu6_gjl3E2pR-70LiZE0en6txHWs6mjSY";

    // Define the Executor for async tasks and mainHandler for UI updates
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Variables to store weather data
    private String cityName;
    private double temperature;
    private int humidity;
    private double windSpeed;

    // Username of the currently signed-in user
    private String signedInUsername;

    // Sets up the weather insights page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the title with the user's name if passed in the Intent
        Intent intent = getIntent();
        signedInUsername = intent.getStringExtra("username");
        if (signedInUsername != null) {
            setTitle("Team 52 - " + signedInUsername);
        }
        // Apply the selected theme based on user preferences
        applyTheme();
        setContentView(R.layout.activity_weather_insights);

        // // Initialize the UI elements
        TextView headerText = findViewById(R.id.headerText);
        responseText = findViewById(R.id.responseText);
        questionsContainer = findViewById(R.id.questionsContainer);
        customQuestionInput = findViewById(R.id.customQuestionInput);
        questionButton1 = findViewById(R.id.questionButton1);
        questionButton2 = findViewById(R.id.questionButton2);
        submitCustomQuestionButton = findViewById(R.id.submitCustomQuestionButton);

        // Retrieve weather data passed via Intent
        cityName = getIntent().getStringExtra("cityName");
        temperature = getIntent().getDoubleExtra("temperature", 0.0);
        humidity = getIntent().getIntExtra("humidity", 0);
        windSpeed = getIntent().getDoubleExtra("windSpeed", 0.0);

        // Set header text with the city name
        headerText.setText(cityName + " Weather Insights");

        // Generate initial weather questions using the generative model
        generateWeatherQuestion();

        // Set up click listeners for question buttons
        questionButton1.setOnClickListener(v -> queryWeatherResponse(questionButton1.getText().toString()));
        questionButton2.setOnClickListener(v -> queryWeatherResponse(questionButton2.getText().toString()));

        // Set up click listener for the submit button for a custom question
        submitCustomQuestionButton.setOnClickListener(v -> {
            String customQuestion = customQuestionInput.getText().toString();
            if (!customQuestion.isEmpty()) {
                queryWeatherResponse(customQuestion);  // Query response for the custom question
            }
        });
    }

    /**
     * Generates weather-related questions based on the current weather conditions.
     * Utilizes the Generative AI model to generate two potential user questions.
     */
    private void generateWeatherQuestion() {
        // Set up the GenerativeModel for "gemini-1.5-flash"
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", APIKEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Define the prompt describing current weather conditions
        String prompt = String.format("Today's weather is temperature of %.1f degrees Fahrenheit, humidity of %d%% with wind speed of %.1f mph. " +
                        "Generate two question that users might ask about this weather. Seperate the questions as Q1. and Q2.",
                temperature, humidity, windSpeed);
        // Prepare content with the prompt text

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Asynchronously handle response or failure
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    /**
                     * Method invoked when the content generation is successful.
                     * Parses the response text, extracts generated questions, and updates the UI.
                     *
                     * @param result The response from the generative AI model containing the generated content.
                     */
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        try {
                            // Get the text response and split by "Q1." and "Q2." labels
                            String resultText = result.getText();
                            String question1 = "No question generated";
                            String question2 = "No question generated";

                            // Parse response text to find and separate Q1 and Q2
                            if (resultText.contains("Q1.") && resultText.contains("Q2.")) {
                                int q1Index = resultText.indexOf("Q1.") + 3;
                                int q2Index = resultText.indexOf("Q2.");

                                question1 = resultText.substring(q1Index, q2Index).trim();
                                question2 = resultText.substring(q2Index + 3).trim();
                            }

                            // Update UI with the generated questions
                            final String finalQuestion1 = question1;
                            final String finalQuestion2 = question2;
                            mainHandler.post(() -> {
                                // fill the dynamic values of buttons with questions
                                questionButton1.setText(finalQuestion1);
                                questionButton2.setText(finalQuestion2);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            mainHandler.post(() -> {
                                // fill the values of button with errors in case of failure
                                questionButton1.setText("Error processing response.");
                                questionButton2.setText("Error processing response.");
                            });
                        }
                    }

                    /**
                     * Callback method invoked when the content generation fails.
                     * Displays an error message to the user.
                     *
                     * @param t The throwable object representing the error.
                     */
                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        // Display error message on the main thread if generation fails
                        mainHandler.post(() -> questionButton1.setText("Failed to generate question."));
                    }
                },
                executor);
    }

    /**
     * Sends a weather-related query to the model and displays the response
     * @param question the question to be answered by the model
     */
    private void queryWeatherResponse(String question) {
        Log.d("### question: ", question);

        // Initialize the GenerativeModel for generating responses
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", APIKEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Formulate a prompt with current weather details and the user's question
        String prompt = String.format("Today's weather is %.1f degrees Fahrenheit, %d%% humidity, and wind speed of %.1f mph. Question: %s",
                temperature, humidity, windSpeed, question);

        // Prepare content with the prompt for the model
        Content content = new Content.Builder().addText(prompt).build();

        // Execute the model's content generation with a callback to handle the asynchronous response
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Add a callback to process the result or display an error
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    /**
                     * Method invoked when the content generation is successful.
                     * Displays the generated response on the UI.
                     *
                     * @param result The response from the generative AI model containing the generated content.
                     */
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        // Retrieve the model's response and display it on the UI
                        String answer = result.getText();
                        mainHandler.post(() -> {
                            responseText.setText(answer);
                            responseText.setVisibility(View.VISIBLE); // Make the responseText visible
                        });
                    }

                    /**
                     * Callback method invoked when the content generation fails.
                     * Displays an error message to the user on the UI.
                     *
                     * @param t The throwable object representing the error.
                     */
                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        // Display an error message if the response fails
                        mainHandler.post(() -> {
                            responseText.setText("Failed to retrieve response.");
                            responseText.setVisibility(View.VISIBLE); // Make the responseText visible on failure as well
                        });
                    }
                },
                executor);
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
}
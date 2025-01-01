package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uiuc.cs427app.data.model.Feature;
import edu.uiuc.cs427app.data.model.GeocodingAPIResponse;
import edu.uiuc.cs427app.data.model.Weather;
import edu.uiuc.cs427app.WeatherInsightsActivity;
import edu.uiuc.cs427app.data.model.WeatherResponse;

// Class for a particular city's info
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    // Username of the currently signed-in user
    private String signedInUsername;

    // variables for each TextView to setText with weather conditions
    private TextView welcomeTextView;
    private TextView temperatureTextView;
    private TextView weatherConditionsTextView;
    private TextView windTextView;
    private TextView humidityTextView;


    // variables to hold: timezone for clocking and date
    private int timezone;
    private TextView dateTimeView;
    private Handler handler = new Handler();

    private double temp;
    private int humidity;
    private double wind;

    // Used to set up the city details page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the activity and set up the user interface
        super.onCreate(savedInstanceState);
        // Set up the title with the user's name if passed in the Intent
        Intent intent = getIntent();
        signedInUsername = intent.getStringExtra("username");
        if (signedInUsername != null) {
            setTitle("Team 52 - " + signedInUsername);
        }
        // Apply the selected theme based on user preferences
        applyTheme();
        // Set the content view to the details layout
        setContentView(R.layout.activity_details);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        // Retrieve the city name passed through the Intent
        String cityName = getIntent().getStringExtra("city").toString();

        // Initializing the GUI elements
        welcomeTextView = findViewById(R.id.welcomeText);
        temperatureTextView = findViewById(R.id.temperature_text);
        weatherConditionsTextView = findViewById(R.id.weatherDescription_text);
        humidityTextView = findViewById(R.id.humidity_text);
        windTextView = findViewById(R.id.wind_text);
        dateTimeView = findViewById(R.id.dateTime_text);

        // Setting text for elements
        welcomeTextView.setText(cityName);
        //cityInfoMessage.setText(cityWeatherInfo);

        // Set up the button to view the city map and handle click events
        // Button buttonMap = findViewById(R.id.mapButton);
        // buttonMap.setOnClickListener(this);

        // Get the weather information from a Service that connects to a weather server and show the results
        Button buttonInsights = findViewById(R.id.weatherInsightsButton);
        buttonInsights.setOnClickListener(this);

        double latitude = getIntent().getDoubleExtra("latitude", 1.0);
        double longitude = getIntent().getDoubleExtra("longitude", 1.0);
        queryWeatherAPI(latitude, longitude);
        //https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}

        // create a function with handler to update clock!
        updateDateTime();
        // use the handler to run the updateTime every second for a live clock (24 hour)
        handler.postDelayed(new Runnable() {
            // call back to updateDateTime to update seconds
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000);
            }
        }, 1000);

    }

    // Used for onclick to get to weather insights
    @Override
    public void onClick(View view) {
        // Goes to weather insights when button is clicked and sends relevant intents
        if (view.getId() == R.id.weatherInsightsButton) {
            Intent intent = new Intent(DetailsActivity.this, WeatherInsightsActivity.class);
            // Add weather data to the intent
            intent.putExtra("cityName", getIntent().getStringExtra("city"));
            intent.putExtra("temperature", temp);
            intent.putExtra("humidity", humidity);
            intent.putExtra("windSpeed", wind);
            intent.putExtra("username", signedInUsername);
            startActivity(intent);
        }
    }

    // method to apply the User's selected theme to this city details page
    protected void applyTheme() {
        // retrieve sharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences",
                MODE_PRIVATE);

        // Get the signed-in username from the Intent
        signedInUsername = getIntent().getStringExtra("username");

        // Determine the user's selected theme and apply it
        String theme = preferences.getString(signedInUsername, "Light");
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

    // Used to query the weather from the Weather API (OpenWeather Map)
    void queryWeatherAPI(double latitude, double longitude) {
        // Start a new thread to perform the network operation to fetch weather data
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do network action in this function
                HttpURLConnection urlConnection = null; // HTTP connection object for the API request
                try {
                    // Construct the OpenWeatherMap API URL with the provided latitude and longitude
                    URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=3ecf46093fcf28b901af8bcbb6eeaae5&units=imperial");
                    System.out.println(url);
                    // Open a connection to the URL
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    // Get the input stream of the response from the API
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    // Create a reader to parse the JSON response
                    Reader reader = new InputStreamReader(in, "UTF-8");

                    // Use Gson to map the JSON response to a WeatherResponse object
                    WeatherResponse result  = new Gson().fromJson(reader, WeatherResponse.class);

                    // Switch back to the UI thread to update UI elements
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Pull out the important weather information
                            Date currentTime = Calendar.getInstance().getTime();
                            temp = result.getMain().getTemp();
                            humidity = result.getMain().getHumidity();
                            wind = result.getWind().getSpeed();
                            timezone = result.getTimezone();

                            // Loop through weather conditions (if there are multiple)
                            for (Weather w : result.getWeather()) {
//                                text += "\nIcon id: " + w.getIcon();
                                weatherConditionsTextView.setText(w.getDescription());
                            }

                            // Get the icon code for the weather (e.g., "10d")
                            String iconCode = result.getWeather().get(0).getIcon(); // Icon code (e.g., "10d")
                            // Construct the icon URL
                            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                            // Load the weather icon into the ImageView
                            ImageView weatherIcon = findViewById(R.id.weatherIcon);
                            Glide.with(DetailsActivity.this)
                                    .load(iconUrl)
                                    .into(weatherIcon); // Use Glide to load the image

                            // Variables to format strings then set TextViews
                            String tempStr = (int) temp + "°F";
                            String humidStr = humidity + "%";
                            String windStr = wind + " mph";

                            // Update the UI with formatted weather data
                            temperatureTextView.setText(tempStr);
                            humidityTextView.setText(humidStr);
                            windTextView.setText(windStr);

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

    /**
     * This method updates the local time and date to display.
     * This method includes counting seconds and a timezone shift to show local time of city
     */
    private void updateDateTime() {
        //get current time and apply timezone
        long localTime = System.currentTimeMillis() + (timezone * 1000L);

        //create a date based off current time
        Date date = new Date(localTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // update textview with the latest time
        String formattedDateTime = dateFormat.format(date);
        dateTimeView.setText(formattedDateTime);
    }
}


package edu.uiuc.cs427app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to display an interactive map of the selected city.
 */
public class MapActivity extends AppCompatActivity {

    private WebView webView;
    private String cityName;
    private String mapUrl;
    private TextView cityNameTextView;
    private TextView latLongTextView;
    private ExecutorService executor;
    private Handler handler;

    // Sets up the Map page with relevant info
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply theme
        String theme = getIntent().getStringExtra("theme");
        System.out.println(theme);
        if (theme != null)
            applyTheme(theme);

        setContentView(R.layout.activity_map);

        // Initialize Executor and Handler for asynchronous operations
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Retrieve city name from Intent
        cityName = getIntent().getStringExtra("city");
        double latitude = getIntent().getDoubleExtra("latitude", 1.0);
        double longitude = getIntent().getDoubleExtra("longitude", 1.0);

        if (cityName == null || cityName.isEmpty()) {
            Toast.makeText(this, "City name is missing.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if city name is not provided
            return;
        }

        // Set the Activity title to the city name
        configureWithCity(cityName, latitude, longitude);

    }

    public void configureWithCity(String cityName, double latitude, double longitude) {
        setTitle(cityName);

        // Initialize UI components
        cityNameTextView = findViewById(R.id.cityNameTextView);
        latLongTextView = findViewById(R.id.latLongTextView);
        webView = findViewById(R.id.mapWebView);

        // Display the city name
        cityNameTextView.setText(cityName);
        latLongTextView.setText("Latitude: " + latitude + ", Longitude: " + longitude);
        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript for interactive maps

        // Ensure links are opened within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Fetch coordinates for the city
        //fetchCoordinates(cityName);
        mapUrl = getURL(latitude, longitude);
        String html = "<html><body style=\"margin:0; padding:0;\">" +
                "<iframe width=\"100%\" height=\"100%\" frameborder=\"0\" style=\"border:0\" src=\"" +
                mapUrl + "\" allowfullscreen></iframe>" +
                "</body></html>";
        webView.loadData(html, "text/html", "utf-8");
    }

    /**
     * Generates the Google Maps embedding URL based on latitude and longitude.
     *
     * @param latitude  The latitude of the city.
     * @param longitude The longitude of the city.
     * @return The formatted Google Maps embedding URL.
     */
    private String getURL(double latitude, double longitude) {
        return "https://maps.google.com/maps?q=" + latitude + "," + longitude + "&t=&z=15&ie=UTF8&iwloc=&output=embed";
    }

    /**
     * Fetches the latitude and longitude of the given city using the Google Geocoding API.
     *
     * @param cityName The name of the city to geocode.
     */
    private void fetchCoordinates(String cityName) {
        executor.execute(() -> {
            double[] coordinates = null;
            String apiKey = "AIzaSyBDYrxmtzJ4NwkHMeAWE5H6Y7ILtlAAr68";
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    cityName.replace(" ", "+") + "&key=" + apiKey;

            try {
                // Create URL and open connection
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Read response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                // Parse JSON response
                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject location = results.getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    coordinates = new double[]{lat, lng};
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            double[] finalCoordinates = coordinates;
            // Post the UI update action to the handler to ensure it runs on the main (UI) thread
            handler.post(() -> {
                if (finalCoordinates != null && finalCoordinates[0] != 0.0 && finalCoordinates[1] != 0.0) {
                    // Update UI with coordinates
                    latLongTextView.setText("Latitude: " + finalCoordinates[0] + ", Longitude: " + finalCoordinates[1]);

                    // Generate and load the map URL
                    mapUrl = getURL(finalCoordinates[0], finalCoordinates[1]);
                    webView.loadUrl(mapUrl);
                } else {
                    // Handle error case
                    Toast.makeText(MapActivity.this, "Unable to fetch coordinates for " + cityName, Toast.LENGTH_LONG).show();
                    latLongTextView.setText("Coordinates not available.");
                }
            });
        });
    }

    // Call the parent class's onDestroy method to handle any required cleanup and shutdown
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Shutdown the executor to prevent memory leaks
    }

    // Used to see which theme to apply to page based on user preference
    private void applyTheme(String theme) {
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

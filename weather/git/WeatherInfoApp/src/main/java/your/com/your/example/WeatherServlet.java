package com.your.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class WeatherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String API_KEY = "a1f86c04ecc3d27f1f5f6118c0b80ce6";
    private static final String LOG_FILE_NAME = System.getProperty("user.home") + "/Documents/weather-log.txt";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String city = req.getParameter("city");
        String rawWeather = null;

        // Ensure the log file exists
        ensureLogFile();

        if (city != null && !city.trim().isEmpty()) {
            rawWeather = fetchWeather(city.trim());

            if (rawWeather != null && !rawWeather.startsWith("Error")) {
                try {
                    JSONObject weatherJSON = new JSONObject(rawWeather);
                    String cityName = weatherJSON.optString("name", city);
                    double temp = weatherJSON.getJSONObject("main").getDouble("temp");
                    String condition = weatherJSON.getJSONArray("weather").getJSONObject(0).getString("description");
                    int humidity = weatherJSON.getJSONObject("main").getInt("humidity");

                    // Set attributes for JSP
                    req.setAttribute("cityName", cityName);
                    req.setAttribute("temp", temp);
                    req.setAttribute("condition", condition);
                    req.setAttribute("humidity", humidity);

                    // Write to log
                    logWeatherToFile(cityName, temp, condition, humidity);

                    System.out.println("✔ Weather logged successfully for: " + cityName);

                } catch (Exception e) {
                    e.printStackTrace();
                    req.setAttribute("weatherError", "❌ Error parsing weather data");
                }
            } else {
                req.setAttribute("weatherError", rawWeather); // Error from API
            }
        } else {
            req.setAttribute("weatherError", "⚠️ Please enter a valid city name.");
        }

        // Forward to JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
        dispatcher.forward(req, resp);
    }

    private String fetchWeather(String city) {
        StringBuilder result = new StringBuilder();
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&units=metric&appid=" + API_KEY;

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            System.out.println("🌐 Weather API status: " + status);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching weather: " + e.getMessage();
        }

        return result.toString();
    }

    // ✅ Ensure the log file exists or create it
    private void ensureLogFile() {
        File logFile = new File(LOG_FILE_NAME);
        if (!logFile.exists()) {
            try {
                boolean created = logFile.createNewFile();
                System.out.println(created ? "📄 Log file created." : "⚠️ Failed to create log file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ✅ Log weather details into file
    private void logWeatherToFile(String city, double temp, String condition, int humidity) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_NAME, true))) {
            writer.write(LocalDateTime.now() + " - City: " + city + ", Temp: " + temp + "°C, Condition: " + condition + ", Humidity: " + humidity + "%");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("❌ Could not write to log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

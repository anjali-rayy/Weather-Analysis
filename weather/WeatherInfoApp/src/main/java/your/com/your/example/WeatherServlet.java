package com.your.example; // ✅ Replace this with your actual package if different

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

public class WeatherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ✅ Replace this with your actual (valid) API key
    private static final String API_KEY = "a1f86c04ecc3d27f1f5f6118c0b80ce6";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String city = req.getParameter("city");
        String rawWeather = null;

        if (city != null && !city.trim().isEmpty()) {
            rawWeather = fetchWeather(city.trim());

            if (rawWeather != null && !rawWeather.startsWith("Error")) {
                try {
                    JSONObject weatherJSON = new JSONObject(rawWeather);

                    String cityName = weatherJSON.optString("name", city);
                    double temp = weatherJSON.getJSONObject("main").getDouble("temp");
                    String condition = weatherJSON
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");
                    int humidity = weatherJSON.getJSONObject("main").getInt("humidity");

                    // ✅ Set attributes for JSP
                    req.setAttribute("cityName", cityName);
                    req.setAttribute("temp", temp);
                    req.setAttribute("condition", condition);
                    req.setAttribute("humidity", humidity);

                    System.out.println("✔ Weather fetched successfully for city: " + cityName);
                } catch (Exception e) {
                    e.printStackTrace();
                    req.setAttribute("weatherError", "❌ Error parsing weather data");
                }
            } else {
                req.setAttribute("weatherError", rawWeather); // e.g.: invalid API key or 404
            }
        } else {
            req.setAttribute("weatherError", "⚠️ Please enter a valid city name.");
        }

        // ✅ Forward to JSP
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
            System.out.println("🔗 API Request URL: " + apiUrl);
            System.out.println("🌐 OpenWeatherMap HTTP Status: " + status);

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
}

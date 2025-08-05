<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Weather Info </title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <h1>Weather Info</h1>
    <form action="weather" method="get">
        <input type="text" name="city" placeholder="Enter City Name" required />
        <input type="submit" value="Get Weather" />
    </form>

    <%
        String weatherError = (String) request.getAttribute("weatherError");
        Object city = request.getAttribute("cityName");
        Object temp = request.getAttribute("temp");
        Object condition = request.getAttribute("condition");
        Object humidity = request.getAttribute("humidity");

        // Debugging line – prints attribute values (can remove in production)
        // out.println("weatherError=" + weatherError + ", city=" + city + ", temp=" + temp + ", condition=" + condition + ", humidity=" + humidity);

        if (weatherError != null) {
    %>
        <div class="weather-container">
            <div class="weather-detail">❌ <%= weatherError %></div>
        </div>
    <%
        } else if (city != null && temp != null && condition != null && humidity != null) {
    %>
        <div class="weather-container">
            <div class="weather-detail city">📍 City: <%= city %></div>
            <div class="weather-detail temp" data-temp="<%= ((Double) temp).intValue() %>">
                🌡 Temperature: <%= temp %> °C
            </div>
            <div class="weather-detail cond">🌤 Condition: <%= condition %></div>
            <div class="weather-detail humidity">💧 Humidity: <%= humidity %>%</div>
        </div>
    <%
        }
    %>
</body>
</html>

package com.example.testapi;

import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestApiAirport {
    public static void main(String[] args) {
        try {
            String API_KEY = "3dabb7ef-783d-4518-adc5-9285f232ba17";
            String COUNTRY_CODE = "VN";
            String url = "https://airlabs.co/api/v9/airports"
                    + "?api_key=" + API_KEY
                    + "&country_code=" + COUNTRY_CODE;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // =========================
            // Parse JSON
            // =========================
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray countries = root.getAsJsonArray("response");

            for (JsonElement element : countries) {
                JsonObject country = element.getAsJsonObject();

                System.out.println(
                        new GsonBuilder()
                                .setPrettyPrinting()
                                .create()
                                .toJson(country)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

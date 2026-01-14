package com.example.airlabproject.service;

import com.example.airlabproject.dto.AirlineDTO;
import com.example.airlabproject.entity.Airline;
import com.example.airlabproject.entity.FlightSchedule;
import com.example.airlabproject.repository.AirlineRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AirlineService {

    @Autowired
    private AirlineRepository airlineRepository;

    @Value("${api-key-airlabs}")
    private String apiKey;

    private final String API_URL = "https://airlabs.co/api/v9/airlines";

    public int fetchAllAirlines(){
        String url = API_URL + "?api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray responseArray = root.has("response") && root.get("response").isJsonArray()
                    ? root.getAsJsonArray("response") : null;

            int count = 0;

            if (responseArray != null) {
                for (JsonElement el : responseArray) {
                    JsonObject node = el.getAsJsonObject();

                    String iataCode = getString(node, "iata_code");
                    String icaoCode = getString(node, "icao_code");
                    String name = getString(node, "name");

                    if (iataCode == null || iataCode.isBlank() || icaoCode == null || icaoCode.isBlank())
                        continue;

                    airlineRepository.upsertAirline(iataCode, icaoCode, name);
                    count++;
                }
            }

            return count;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<AirlineDTO> getAll() {
        return airlineRepository.findAll().stream().map(
                a ->  new AirlineDTO(a.getIataCode(), a.getIcaoCode(), a.getName())
        ).collect(Collectors.toList());
    }

    public Optional<Airline> findByIataCode(String airlineIata) {
        return airlineRepository.findByIataCode(airlineIata);
    }

    public boolean isDBEmpty()
    {
        return airlineRepository.count() == 0;
    }

    @Transactional
    public void AirlinesLoadDB()
    {
        if  (isDBEmpty())   fetchAllAirlines();
    }

    private String getString(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
}
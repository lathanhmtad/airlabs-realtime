package com.example.airlabproject.service;

import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.dto.FlightScheduleDTO;
import com.example.airlabproject.entity.FlightSchedule;
import com.example.airlabproject.repository.FlightScheduleRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightScheduleService {

    @Autowired
    private FlightScheduleRepository flightRepository;

    @Value("${api-key-airlabs}")
    private String apiKey;

    private final String API_URL = "https://airlabs.co/api/v9/schedules";

    @Transactional
    public List<FlightSchedule> getFlights(String airportCode) {
        // 1. QUY TẮC CACHE: Chỉ lấy dữ liệu trong DB nếu nó được tạo trong vòng 30 phút qua
        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(30);

        List<FlightSchedule> cachedData = flightRepository.findByDepIataAndCreatedAtAfter(airportCode, timeThreshold);

        if (!cachedData.isEmpty()) {
            System.out.println("--> Lấy dữ liệu từ DATABASE (Cache)");
            return cachedData;
        }

        // 2. Nếu không có cache hợp lệ -> Gọi API
        System.out.println("--> Gọi AIRLABS API mới");
        return fetchFromApiAndSave(airportCode);
    }

    public List<FlightScheduleDTO> getAll() {
        return flightRepository.findAll().stream()
            .map(f -> new FlightScheduleDTO(
                f.getAirlineIata(), f.getFlightIata(), f.getDepIata(), f.getArrIata(), f.getStatus(), f.getDepTime(), f.getArrTime(), f.getDepTimeUtc(), f.getArrTimeUtc()
            ))
            .collect(Collectors.toList());
    }

    public List<FlightScheduleDTO> getFlightsByAirportCode(String airportCode) {
        return flightRepository.findByDepIata(airportCode).stream()
            .map(f -> new FlightScheduleDTO(
                f.getAirlineIata(), f.getFlightIata(), f.getDepIata(), f.getArrIata(), f.getStatus(), f.getDepTime(), f.getArrTime(), f.getDepTimeUtc(), f.getArrTimeUtc()
            ))
            .collect(Collectors.toList());
    }

    private List<FlightSchedule> fetchFromApiAndSave(String airportCode) {
        String url = API_URL + "?dep_iata=" + airportCode + "&api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray responseArray = root.has("response") && root.get("response").isJsonArray() ? root.getAsJsonArray("response") : null;

            List<FlightSchedule> flightList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            if (responseArray != null) {
                for (JsonElement el : responseArray) {
                    JsonObject node = el.getAsJsonObject();

                    FlightSchedule f = new FlightSchedule();

                    // Strings
                    f.setAirlineIata(getString(node, "airline_iata"));
                    f.setFlightIata(getString(node, "flight_iata"));
                    f.setDepIata(getString(node, "dep_iata"));
                    f.setArrIata(getString(node, "arr_iata"));
                    f.setStatus(getString(node, "status"));

                    // Times (local & UTC)
                    f.setDepTime(parseTime(getString(node, "dep_time"), formatter));
                    f.setDepTimeUtc(parseTime(getString(node, "dep_time_utc"), formatter));
                    f.setArrTime(parseTime(getString(node, "arr_time"), formatter));
                    f.setArrTimeUtc(parseTime(getString(node, "arr_time_utc"), formatter));

                    flightList.add(f);
                }
            }

            // Refresh cache for this departure airport
            flightRepository.deleteByDepIata(airportCode);
            return flightRepository.saveAll(flightList);

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : null;
    }

    private LocalDateTime parseTime(String value, DateTimeFormatter fmt) {
        try {
            if (value == null || value.isEmpty()) return null;
            return LocalDateTime.parse(value, fmt);
        } catch (Exception ignore) {
            return null;
        }
    }
}
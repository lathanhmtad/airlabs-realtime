package com.example.airlabproject.service;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.entity.Airport;
import com.example.airlabproject.entity.Country;
import com.example.airlabproject.repository.AirportRepository;
import com.example.airlabproject.repository.CountryRepository;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportService {
    private final CountryRepository countryRepository;
    private final AirportRepository airportRepository;

    @Value("${api-key-airlabs}")
    private String airlabsApiKey;

    public AirportService(CountryRepository countryRepository, AirportRepository airportRepository) {
        this.countryRepository = countryRepository;
        this.airportRepository = airportRepository;
    }

    public List<AirportDTO> getAll() {
        return airportRepository.findAll()
                .stream()
                .map(c -> new AirportDTO(c.getIataCode(), c.getName(), c.getIcaoCode(), c.getLat(), c.getLng(), c.getParentCountry() != null ? c.getParentCountry().getCode() : null))
                .collect(Collectors.toList());
    }

    public List<AirportDTO> getByCountryCode(String countryCode) {
        return airportRepository.findAllByParentCountry_Code(countryCode)
            .stream()
            .map(c -> new AirportDTO(
                c.getIataCode(),
                c.getName(),
                c.getIcaoCode(),
                c.getLat(),
                c.getLng(),
                c.getParentCountry() != null ? c.getParentCountry().getCode() : null
            ))
            .collect(Collectors.toList());
    }

    public int saveByCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) return 0;

        Country countryRef = countryRepository.findById(countryCode).orElse(null);
        if (countryRef == null) return 0; // require existing country (FK constraint)
        int saved = 0;
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new GsonBuilder().create();

        String url = "https://airlabs.co/api/v9/airports?api_key=" + airlabsApiKey + "&country_code=" + countryCode;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray airports = root.getAsJsonArray("response");
            if (airports == null) return 0;

            List<Airport> batch = new ArrayList<>();
            for (JsonElement element : airports) {
                JsonObject obj = element.getAsJsonObject();

                String iata = obj.has("iata_code") && !obj.get("iata_code").isJsonNull() ? obj.get("iata_code").getAsString() : null;
                String name = obj.has("name") && !obj.get("name").isJsonNull() ? obj.get("name").getAsString() : null;
                String icao = obj.has("icao_code") && !obj.get("icao_code").isJsonNull() ? obj.get("icao_code").getAsString() : null;

                java.math.BigDecimal lat = null;
                java.math.BigDecimal lng = null;
                if (obj.has("lat") && !obj.get("lat").isJsonNull()) {
                    try { lat = java.math.BigDecimal.valueOf(obj.get("lat").getAsDouble()); } catch (Exception ignored) {}
                }
                if (obj.has("lng") && !obj.get("lng").isJsonNull()) {
                    try { lng = java.math.BigDecimal.valueOf(obj.get("lng").getAsDouble()); } catch (Exception ignored) {}
                }

                if (iata == null || name == null) continue;

                Airport ap = new Airport();
                ap.setIataCode(iata);
                ap.setName(name);
                ap.setIcaoCode(icao);
                ap.setLat(lat);
                ap.setLng(lng);

                if (countryCode != null) {
                    Country parent = countryRepository.findById(countryCode).orElse(null);
                    ap.setParentCountry(parent);
                }

                if (ap.getIcaoCode() == null || ap.getIataCode() == null) continue;
                batch.add(ap);
            }

            if (!batch.isEmpty()) {
                airportRepository.saveAll(batch);
                saved = batch.size();
            }
        } catch (Exception e) {
            // ignore and return what we have
        }

        return saved;
    }
    

}

package com.example.airlabproject.service;

import com.example.airlabproject.dto.CityDTO;
import com.example.airlabproject.entity.City;
import com.example.airlabproject.entity.Country;
import com.example.airlabproject.repository.CityRepository;
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
public class CityService {
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @Value("${api-key-airlabs}")
    private String airlabsApiKey;

    public CityService(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    public List<CityDTO> getAll() {
        return cityRepository.findAll().stream()
                .map(c -> new CityDTO(
                        c.getCityCode(),
                        c.getName(),
                        c.getLat(),
                        c.getLng(),
                        c.getParentCountry() != null ? c.getParentCountry().getCode() : null,
                        c.getType()
                ))
                .collect(Collectors.toList());
    }

    public List<CityDTO> getByCountryCode(String countryCode) {
        return cityRepository.findAllByParentCountry_Code(countryCode).stream()
                .map(c -> new CityDTO(
                        c.getCityCode(),
                        c.getName(),
                        c.getLat(),
                        c.getLng(),
                        c.getParentCountry() != null ? c.getParentCountry().getCode() : null,
                        c.getType()
                ))
                .collect(Collectors.toList());
    }

    public int saveAllVietnamCityFromAirlabs() {
        // Only import cities for Vietnam (VN)
        Country vn = countryRepository.findById("VN").orElse(null);
        if (vn == null) return 0;

        int saved = 0;
        HttpClient client = HttpClient.newHttpClient();

        String url = "https://airlabs.co/api/v9/cities?api_key=" + airlabsApiKey + "&country_code=VN";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray cities = root.getAsJsonArray("response");
            if (cities == null) return 0;

            List<City> batch = new ArrayList<>();
            for (JsonElement element : cities) {
                JsonObject obj = element.getAsJsonObject();

                String cityCode = obj.has("city_code") && !obj.get("city_code").isJsonNull() ? obj.get("city_code").getAsString() : null;
                String name = obj.has("name") && !obj.get("name").isJsonNull() ? obj.get("name").getAsString() : null;
                String type = obj.has("type") && !obj.get("type").isJsonNull() ? obj.get("type").getAsString() : null;

                java.math.BigDecimal lat = null;
                java.math.BigDecimal lng = null;
                if (obj.has("lat") && !obj.get("lat").isJsonNull()) {
                    try { lat = java.math.BigDecimal.valueOf(obj.get("lat").getAsDouble()); } catch (Exception ignored) {}
                }
                if (obj.has("lng") && !obj.get("lng").isJsonNull()) {
                    try { lng = java.math.BigDecimal.valueOf(obj.get("lng").getAsDouble()); } catch (Exception ignored) {}
                }

                if (cityCode == null || name == null) continue;

                City city = new City();
                city.setCityCode(cityCode);
                city.setName(name);
                city.setType(type);
                city.setLat(lat);
                city.setLng(lng);
                city.setParentCountry(vn);
                batch.add(city);
            }

            if (!batch.isEmpty()) {
                cityRepository.saveAll(batch);
                saved += batch.size();
            }
        } catch (Exception e) {
            // Skip on error
        }
        return saved;
    }
}

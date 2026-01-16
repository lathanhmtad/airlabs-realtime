package com.example.airlabproject.service;

import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.entity.Country;
import com.example.airlabproject.repository.ContinentRepository;
import com.example.airlabproject.repository.CountryRepository;
import com.example.airlabproject.util.CountryCache;
import com.google.gson.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;

    public CountryService(CountryRepository countryRepository, ContinentRepository continentRepository) {
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(CountryService.class);

    // HttpClient nên được tái sử dụng thay vì tạo mới mỗi lần (tốt cho hiệu năng)
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${api-key-airlabs}")
    private String airlabsApiKey;

    public List<CountryDTO> getByContinentId(String continentId) {
        if (continentId == null || continentId.isBlank()) return null;

        Continent continent = continentRepository.findById(continentId).orElse(null);
        if (continent == null) {
            continentRepository.save(new Continent(continentId));
        }
        //Get from cache
        List<CountryDTO> cacheCountryDTOs = CountryCache.getAll();
        if (!cacheCountryDTOs.isEmpty()) {
            return cacheCountryDTOs;
        }

        //Get from DB
        List<Country> countries = countryRepository.findAllByContinent_Id(continentId);
        if (countries.isEmpty()) {
            //Get from APi
            countries = fetchAndSaveCountriesByContinent(continentId);
        }
        cacheCountryDTOs = countries
                            .stream()
                            .map(c -> new CountryDTO(c.getCode(), c.getCode3(), c.getName(), c.getContinent() != null ? c.getContinent().getId() : null))
                            .collect(Collectors.toList());
        CountryCache.addAll(cacheCountryDTOs);
        return cacheCountryDTOs;
    }

    private List<Country> fetchAndSaveCountriesByContinent(String continentId) {
        String url = String.format("https://airlabs.co/api/v9/countries?api_key=%s&continent=%s", airlabsApiKey, continentId);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 1. Kiểm tra HTTP Status
            if (response.statusCode() != 200) {
                log.error("API Error: Status Code {} - Body: {}", response.statusCode(), response.body());
                return null;
            }

            // 2. Parse JSON
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            // Kiểm tra xem key "response" có tồn tại và là mảng không
            if (!root.has("response") || !root.get("response").isJsonArray()) {
                log.warn("API response does not contain valid data for continent: {}", continentId);
                return null;
            }

            JsonArray dataArray = root.getAsJsonArray("response");
            List<Country> countries = new ArrayList<>();

            // 3. Loop và Map dữ liệu
            for (JsonElement element : dataArray) {
                JsonObject obj = element.getAsJsonObject();

                String code = getSafeString(obj, "code");
                String code3 = getSafeString(obj, "code3");
                String name = getSafeString(obj, "name");

                countries.add(new Country(code, code3, name, new Continent("AS")));
            }

            // 4. Lưu vào DB
            if (!countries.isEmpty()) {
                List<Country> savedCountries = countryRepository.saveAll(countries);
                log.info("Saved {} countries for continent {}", savedCountries.size(), continentId);
            }
            return countries;

        } catch (Exception e) {
            log.error("Error processing continentId: " + continentId, e);
        }
        return null;
    }

    private String getSafeString(JsonObject obj, String memberName) {
        if (obj.has(memberName) && !obj.get(memberName).isJsonNull()) {
            return obj.get(memberName).getAsString();
        }
        return null;
    }
}
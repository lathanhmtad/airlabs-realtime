package com.example.airlabproject.service;

import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.entity.Country;
import com.example.airlabproject.repository.ContinentRepository;
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
public class CountryService {

    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;

    @Value("${api-key-airlabs}")
    private String airlabsApiKey;

    public CountryService(CountryRepository countryRepository, ContinentRepository continentRepository) {
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
    }

    public List<CountryDTO> getAll() {
        return countryRepository.findAll()
                .stream()
                .map(c -> new CountryDTO(c.getCode(), c.getCode3(), c.getName(), c.getContinent() != null ? c.getContinent().getId() : null))
                .collect(Collectors.toList());
    }

    public List<CountryDTO> getByContinentId(String continentId) {
        if (continentId == null || continentId.isBlank()) return getAll();
        return countryRepository.findAllByContinent_Id(continentId)
                .stream()
                .map(c -> new CountryDTO(c.getCode(), c.getCode3(), c.getName(), c.getContinent() != null ? c.getContinent().getId() : null))
                .collect(Collectors.toList());
    }

    public int saveAllFromAirlabs() {
        List<Continent> continents = continentRepository.findAll();
        if (continents.isEmpty()) return 0;

        int saved = 0;
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new GsonBuilder().create();

        for (Continent continent : continents) {
            String url = "https://airlabs.co/api/v9/countries?api_key=" + airlabsApiKey + "&continent=" + continent.getId();
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray countries = root.getAsJsonArray("response");
                if (countries == null) continue;

                List<Country> batch = new ArrayList<>();
                for (JsonElement element : countries) {
                    JsonObject obj = element.getAsJsonObject();
                    String code = obj.has("code") && !obj.get("code").isJsonNull() ? obj.get("code").getAsString() : null;
                    String code3 = obj.has("code3") && !obj.get("code3").isJsonNull() ? obj.get("code3").getAsString() : null;
                    String name = obj.has("name") && !obj.get("name").isJsonNull() ? obj.get("name").getAsString() : null;
                    if (code == null || name == null) continue;
                    batch.add(new Country(code, code3, name, continent));
                }
                if (!batch.isEmpty()) {
                    countryRepository.saveAll(batch);
                    saved += batch.size();
                }
            } catch (Exception e) {
                // Skip this continent on error
            }
        }
        return saved;
    }
}

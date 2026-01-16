package com.example.airlabproject.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.airlabproject.dto.CountryDTO;

public class CountryCache {

    private static final Set<CountryDTO> countryCache =
            ConcurrentHashMap.newKeySet();

    public static void addCountry(CountryDTO country) {
        countryCache.add(country);
    }

    public static void addAll(Collection<CountryDTO> countries) {
        countryCache.addAll(countries);
    }

    public static List<CountryDTO> getAll() {
        return new ArrayList<>(countryCache);
    }

    public static void clear() {
        countryCache.clear();
    }

    public static boolean isEmpty() {
        return countryCache.isEmpty();
    }
}

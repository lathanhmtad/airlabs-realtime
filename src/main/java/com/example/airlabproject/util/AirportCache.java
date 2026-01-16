package com.example.airlabproject.util;

import com.example.airlabproject.dto.AirportDTO;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AirportCache {

    private static final int MAX_SIZE = 10;

    private static final Map<String, List<AirportDTO>> airportCache =
            new ConcurrentHashMap<>();

    private static final Queue<String> order =
            new ConcurrentLinkedQueue<>();

    public static List<AirportDTO> get(String countryCode) {
        List<AirportDTO> airports = airportCache.get(countryCode);
        if (airports != null) {
            refresh(countryCode);
        }
        return airports;
    }

    public static void add(String countryCode, List<AirportDTO> airports) {
        if (countryCode == null || airports == null) return;

        airportCache.put(countryCode, List.copyOf(airports));
        addInternal(countryCode);
    }

    private static void addInternal(String countryCode) {
        synchronized (AirportCache.class) {
            if (!order.contains(countryCode)) {
                order.add(countryCode);
            }

            while (order.size() > MAX_SIZE) {
                String oldest = order.poll();
                if (oldest != null) {
                    airportCache.remove(oldest);
                }
            }
        }
    }

    private static void refresh(String countryCode) {
        synchronized (AirportCache.class) {
            order.remove(countryCode);
            order.add(countryCode);
        }
    }
}

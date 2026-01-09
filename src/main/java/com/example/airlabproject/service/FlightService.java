package com.example.airlabproject.service;

import com.example.airlabproject.entity.FlightSchedule;
import com.example.airlabproject.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightServiceImpl {

    @Autowired
    private FlightRepository flightRepository;

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

    private List<FlightSchedule> fetchFromApiAndSave(String airportCode) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + "?dep_iata=" + airportCode + "&api_key=" + apiKey;

        try {
            // Gọi API và parse JSON thủ công để linh hoạt
            String response = restTemplate.getForObject(url, String.class);
            tools.jackson.databind.ObjectMapper mapper = new ObjectMapper();
            tools.jackson.databind.JsonNode root = mapper.readTree(response);
            JsonNode responseArray = root.path("response");

            List<FlightSchedule> flightList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            if (responseArray.isArray()) {
                for (JsonNode node : responseArray) {
                    FlightSchedule f = new FlightSchedule();
                    f.setFlightIata(node.path("flight_iata").asText());
                    f.setDepIata(node.path("dep_iata").asText());
                    f.setArrIata(node.path("arr_iata").asText());
                    f.setStatus(node.path("status").asText());

                    // Parse thời gian (cần xử lý try-catch nếu format sai, ở đây làm đơn giản)
                    try {
                        String depTimeStr = node.path("dep_time").asText();
                        String arrTimeStr = node.path("arr_time").asText();
                        if(!depTimeStr.isEmpty()) f.setDepTime(LocalDateTime.parse(depTimeStr, formatter));
                        if(!arrTimeStr.isEmpty()) f.setArrTime(LocalDateTime.parse(arrTimeStr, formatter));
                    } catch (Exception e) {
                        // Bỏ qua lỗi parse date
                    }

                    flightList.add(f);
                }
            }

            // 3. Xóa cache cũ và Lưu mới vào DB
            flightRepository.deleteByDepIata(airportCode);
            return flightRepository.saveAll(flightList);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về rỗng nếu lỗi
        }
    }
}
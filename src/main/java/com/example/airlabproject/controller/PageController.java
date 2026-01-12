package com.example.airlabproject.controller;

import com.example.airlabproject.entity.FlightSchedule;
import com.example.airlabproject.service.FlightScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class PageController {

    private FlightScheduleService flightService;

    // Dữ liệu giả lập mapping City -> Airports (Thực tế có thể lưu trong DB)
    private Map<String, Map<String, String>> getVietnamAirports() {
        Map<String, Map<String, String>> data = new HashMap<>();

        // Hà Nội
        Map<String, String> hanoi = new HashMap<>();
        hanoi.put("HAN", "Sân bay Nội Bài (HAN)");
        data.put("Hanoi", hanoi);

        // Đà Nẵng
        Map<String, String> danang = new HashMap<>();
        danang.put("DAD", "Sân bay Đà Nẵng (DAD)");
        data.put("Da Nang", danang);

        // TP. HCM
        Map<String, String> hcm = new HashMap<>();
        hcm.put("SGN", "Sân bay Tân Sơn Nhất (SGN)");
        data.put("Ho Chi Minh City", hcm);

        // Nha Trang
        Map<String, String> nhatrang = new HashMap<>();
        nhatrang.put("CXR", "Sân bay Cam Ranh (CXR)");
        data.put("Nha Trang", nhatrang);

        return data;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("cities", getVietnamAirports().keySet());
        model.addAttribute("airportsMap", getVietnamAirports()); // Truyền map xuống để JS xử lý
        return "index";
    }

    @PostMapping("/search")
    public String searchFlights(@RequestParam("airportCode") String airportCode, Model model) {
        List<FlightSchedule> flights = flightService.getFlights(airportCode);

        // Load lại dữ liệu dropdown để không bị mất khi reload trang
        model.addAttribute("cities", getVietnamAirports().keySet());
        model.addAttribute("airportsMap", getVietnamAirports());

        model.addAttribute("selectedAirport", airportCode);
        model.addAttribute("flights", flights);

        return "index";
    }
}
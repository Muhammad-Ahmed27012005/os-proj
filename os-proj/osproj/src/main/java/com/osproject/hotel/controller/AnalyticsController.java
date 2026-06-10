package com.osproject.hotel.controller;

import com.osproject.hotel.model.Booking;
import com.osproject.hotel.model.Room;
import com.osproject.hotel.service.BookingService;
import com.osproject.hotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public String analyticsPage(Model model) {
        List<Room> allRooms = roomService.getAllRooms();
        List<Booking> allBookings = bookingService.getAllBookings();

        model.addAttribute("totalRooms", allRooms.size());
        model.addAttribute("availableRooms", allRooms.stream().filter(Room::isAvailable).count());
        model.addAttribute("totalBookings", allBookings.size());
        model.addAttribute("confirmedBookings",
                allBookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count());

        BigDecimal totalRevenue = allBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalRevenue", totalRevenue);

        long occupiedToday = allBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus())
                        && !b.getCheckInDate().isAfter(LocalDate.now())
                        && !b.getCheckOutDate().isBefore(LocalDate.now()))
                .count();
        double occupancyRate = allRooms.isEmpty() ? 0 : (occupiedToday * 100.0 / allRooms.size());
        model.addAttribute("occupancyRate", String.format("%.1f%%", occupancyRate));

        model.addAttribute("recentBookings", bookingService.getRecentBookings(5));
        return "analytics";
    }
}
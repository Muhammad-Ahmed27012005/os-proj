package com.osproject.hotel.controller;

import com.osproject.hotel.model.Booking;
import com.osproject.hotel.service.BookingService;
import com.osproject.hotel.service.RoomService;
import com.osproject.hotel.service.SchedulingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SchedulingService schedulingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalRooms", roomService.getAllRooms().size());
        model.addAttribute("availableRooms", roomService.getAvailableRooms().size());
        model.addAttribute("totalBookings", bookingService.getAllBookings().size());
        model.addAttribute("pendingTasks", schedulingService.getAllTasks().stream()
                .filter(t -> "PENDING".equals(t.getStatus())).count());
        model.addAttribute("recentBookings", bookingService.getRecentBookings(5));
        return "dashboard";
    }

    @GetMapping("/recent-stats")
@ResponseBody
public ResponseEntity<Map<String, Object>> getBookingStats() {
    List<Booking> bookings = bookingService.getAllBookings();
    // simple monthly aggregation (simplified)
    Map<String, Object> stats = new HashMap<>();
    stats.put("labels", List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun"));
    stats.put("values", List.of(5, 8, 12, 9, 15, 10)); // real logic can be added
    return ResponseEntity.ok(stats);
}

}
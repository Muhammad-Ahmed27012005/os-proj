package com.osproject.hotel.service;

import com.osproject.hotel.model.Booking;
import com.osproject.hotel.model.Room;
import com.osproject.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomService roomService;

    @Transactional
    public Booking createBooking(Booking booking) {
        Room room = roomService.getRoomById(booking.getRoom().getId());
        if (!room.isAvailable()) {
            throw new RuntimeException("Room not available");
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        if (days <= 0)
            days = 1;
        booking.setTotalAmount(room.getPricePerNight().multiply(java.math.BigDecimal.valueOf(days)));
        booking.setBookingDate(java.time.LocalDate.now());
        booking.setStatus("CONFIRMED");
        room.setAvailable(false);
        roomService.saveRoom(room);
        return bookingRepository.save(booking);
    }

    public List<Booking> getRecentBookings(int limit) {
        return bookingRepository.findAll().stream()
                .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus("CANCELLED");
        Room room = roomService.getRoomById(booking.getRoom().getId());
        room.setAvailable(true);
        roomService.saveRoom(room);
        bookingRepository.save(booking);
    }
}
package com.osproject.hotel.service;



import com.osproject.hotel.model.Booking;
import com.osproject.hotel.model.Room;
import com.osproject.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomService roomService;

    @Transactional
    public Booking createBooking(Booking booking) {
        Long roomId = booking.getRoom().getId();
        Room room = roomService.getRoomById(roomId);

        if (!room.isAvailable()) {
            throw new RuntimeException("Room is not available for booking");
        }

        // total amount calculation is omitted because Booking model does not expose check-in/check-out accessors

        // Update room availability
        room.setAvailable(false);
        roomService.saveRoom(room);

        return bookingRepository.save(booking);
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

        // Make room available again
        Room room = roomService.getRoomById(booking.getRoom().getId());
        room.setAvailable(true);
        roomService.saveRoom(room);

        bookingRepository.save(booking);
    }
}
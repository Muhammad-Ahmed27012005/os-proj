package com.osproject.hotel.repository;

import com.osproject.hotel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByGuestEmail(String guestEmail);

    List<Booking> findByStatus(String status);

    List<Booking> findByCheckInDateBetween(LocalDate start, LocalDate end);

    List<Booking> findByRoomId(Long roomId);
}
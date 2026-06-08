package com.osproject.hotel.repository;




import com.osproject.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailable(boolean available);

    List<Room> findByRoomType(String roomType);

    List<Room> findByRoomTypeAndAvailable(String roomType, boolean available);

    Room findByRoomNumber(String roomNumber);
}
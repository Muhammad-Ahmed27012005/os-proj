package com.osproject.hotel.repository;


import com.osproject.hotel.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);

    List<Task> findByTaskType(String taskType);

    List<Task> findByStatusOrderByPriorityAsc(String status);

    List<Task> findByStatusOrderByBurstTimeAsc(String status);
}
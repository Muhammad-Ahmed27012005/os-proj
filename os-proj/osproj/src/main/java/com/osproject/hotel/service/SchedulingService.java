package com.osproject.hotel.service;



import com.osproject.hotel.model.Task;
import com.osproject.hotel.repository.TaskRepository;
import com.osproject.hotel.scheduler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SchedulingService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FCFSScheduler fcfsScheduler;

    @Autowired
    private PriorityScheduler priorityScheduler;

    @Autowired
    private RoundRobinScheduler roundRobinScheduler;

    @Autowired
    private SJFScheduler sjfScheduler;

    public Map<String, Object> scheduleWithAlgorithm(String algorithm, List<Task> tasks, int timeQuantum) {
        List<Task> scheduledTasks;
        Map<String, Double> metrics;

        switch (algorithm.toUpperCase()) {
            case "FCFS":
                scheduledTasks = fcfsScheduler.schedule(tasks);
                metrics = fcfsScheduler.calculateMetrics(scheduledTasks);
                break;
            case "PRIORITY":
                scheduledTasks = priorityScheduler.schedule(tasks);
                metrics = priorityScheduler.calculateMetrics(scheduledTasks);
                break;
            case "ROUNDROBIN":
                scheduledTasks = roundRobinScheduler.schedule(tasks, timeQuantum);
                metrics = roundRobinScheduler.calculateMetrics(scheduledTasks);
                break;
            case "SJF":
                scheduledTasks = sjfScheduler.schedule(tasks);
                metrics = sjfScheduler.calculateMetrics(scheduledTasks);
                break;
            case "SJF_PREEMPTIVE":
                scheduledTasks = sjfScheduler.schedulePreemptive(tasks);
                metrics = sjfScheduler.calculateMetrics(scheduledTasks);
                break;
            default:
                throw new IllegalArgumentException("Unknown scheduling algorithm: " + algorithm);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("scheduledTasks", scheduledTasks);
        result.put("metrics", metrics);
        result.put("algorithm", algorithm);

        return result;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
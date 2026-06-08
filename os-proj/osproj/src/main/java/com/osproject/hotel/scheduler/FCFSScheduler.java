package com.osproject.hotel.scheduler;



import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class FCFSScheduler {

    public List<Task> schedule(List<Task> tasks) {
        // Sort by arrival time
        tasks.sort(Comparator.comparingInt(Task::getArrivalTime));

        int currentTime = 0;
        List<Task> scheduledTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (currentTime < task.getArrivalTime()) {
                currentTime = task.getArrivalTime();
            }

            task.setWaitingTime(currentTime - task.getArrivalTime());
            currentTime += task.getBurstTime();
            task.setTurnaroundTime(task.getWaitingTime() + task.getBurstTime());
            task.setStatus("COMPLETED");
            scheduledTasks.add(task);
        }

        return scheduledTasks;
    }

    public Map<String, Double> calculateMetrics(List<Task> tasks) {
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (Task task : tasks) {
            totalWaitingTime += task.getWaitingTime();
            totalTurnaroundTime += task.getTurnaroundTime();
        }

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("avgWaitingTime", totalWaitingTime / tasks.size());
        metrics.put("avgTurnaroundTime", totalTurnaroundTime / tasks.size());

        return metrics;
    }
}
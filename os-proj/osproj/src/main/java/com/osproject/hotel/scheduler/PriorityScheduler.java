package com.osproject.hotel.scheduler;

import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class PriorityScheduler {

    public List<Task> schedule(List<Task> tasks) {
        List<Task> mutableTasks = new ArrayList<>(tasks);
        mutableTasks.sort(Comparator.comparingInt((Task t) -> t.getArrivalTime())
                .thenComparingInt(Task::getPriority));
        int currentTime = 0;
        List<Task> scheduledTasks = new ArrayList<>();
        while (!mutableTasks.isEmpty()) {
            Task selected = null;
            for (Task task : mutableTasks) {
                if (task.getArrivalTime() <= currentTime) {
                    if (selected == null || task.getPriority() < selected.getPriority()) {
                        selected = task;
                    }
                }
            }
            if (selected == null) {
                currentTime = mutableTasks.get(0).getArrivalTime();
                continue;
            }
            selected.setWaitingTime(currentTime - selected.getArrivalTime());
            currentTime += selected.getBurstTime();
            selected.setTurnaroundTime(selected.getWaitingTime() + selected.getBurstTime());
            selected.setStatus("COMPLETED");
            scheduledTasks.add(selected);
            mutableTasks.remove(selected);
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
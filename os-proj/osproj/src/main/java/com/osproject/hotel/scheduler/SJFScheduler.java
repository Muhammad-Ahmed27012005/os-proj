package com.osproject.hotel.scheduler;

import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class SJFScheduler {

    public List<Task> schedule(List<Task> tasks) {
        List<Task> scheduledTasks = new ArrayList<>();
        List<Task> pendingTasks = new ArrayList<>(tasks); // mutable copy
        int currentTime = 0;

        while (!pendingTasks.isEmpty()) {
            Task shortestTask = null;
            int minBurstTime = Integer.MAX_VALUE;
            for (Task task : pendingTasks) {
                if (task.getArrivalTime() <= currentTime && task.getBurstTime() < minBurstTime) {
                    shortestTask = task;
                    minBurstTime = task.getBurstTime();
                }
            }
            if (shortestTask == null) {
                currentTime = pendingTasks.stream().mapToInt(Task::getArrivalTime).min().getAsInt();
                continue;
            }
            shortestTask.setWaitingTime(currentTime - shortestTask.getArrivalTime());
            currentTime += shortestTask.getBurstTime();
            shortestTask.setTurnaroundTime(shortestTask.getWaitingTime() + shortestTask.getBurstTime());
            shortestTask.setStatus("COMPLETED");
            scheduledTasks.add(shortestTask);
            pendingTasks.remove(shortestTask);
        }
        return scheduledTasks;
    }

    public List<Task> schedulePreemptive(List<Task> tasks) {
        List<Task> mutableTasks = new ArrayList<>(tasks);
        Map<Long, Integer> remainingTime = new HashMap<>();
        for (Task task : mutableTasks) {
            remainingTime.put(task.getId(), task.getBurstTime());
        }
        int currentTime = 0;
        int completedTasks = 0;
        int n = mutableTasks.size();
        List<Task> scheduledTasks = new ArrayList<>();

        while (completedTasks < n) {
            int minRemainingTime = Integer.MAX_VALUE;
            Task shortestTask = null;
            for (Task task : mutableTasks) {
                if (task.getArrivalTime() <= currentTime && remainingTime.get(task.getId()) > 0
                        && remainingTime.get(task.getId()) < minRemainingTime) {
                    minRemainingTime = remainingTime.get(task.getId());
                    shortestTask = task;
                }
            }
            if (shortestTask == null) {
                currentTime++;
                continue;
            }
            remainingTime.put(shortestTask.getId(), remainingTime.get(shortestTask.getId()) - 1);
            currentTime++;
            if (remainingTime.get(shortestTask.getId()) == 0) {
                completedTasks++;
                shortestTask.setTurnaroundTime(currentTime - shortestTask.getArrivalTime());
                shortestTask.setWaitingTime(shortestTask.getTurnaroundTime() - shortestTask.getBurstTime());
                shortestTask.setStatus("COMPLETED");
                scheduledTasks.add(shortestTask);
            }
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
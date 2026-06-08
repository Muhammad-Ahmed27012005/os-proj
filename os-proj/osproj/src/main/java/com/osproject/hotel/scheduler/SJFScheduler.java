package com.osproject.hotel.scheduler;


import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class SJFScheduler {

    public List<Task> schedule(List<Task> tasks) {
        List<Task> scheduledTasks = new ArrayList<>();
        List<Task> pendingTasks = new ArrayList<>(tasks);

        int currentTime = 0;

        while (!pendingTasks.isEmpty()) {
            Task shortestTask = null;
            int minBurstTime = Integer.MAX_VALUE;

            // Find the task with shortest burst time among arrived tasks
            for (Task task : pendingTasks) {
                if (task.getArrivalTime() <= currentTime && task.getBurstTime() < minBurstTime) {
                    shortestTask = task;
                    minBurstTime = task.getBurstTime();
                }
            }

            if (shortestTask == null) {
                // If no task has arrived, move time to next arrival
                currentTime = pendingTasks.stream()
                        .mapToInt(Task::getArrivalTime)
                        .min()
                        .getAsInt();
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

    // Preemptive SJF (SRTF - Shortest Remaining Time First)
    public List<Task> schedulePreemptive(List<Task> tasks) {
        List<Task> scheduledTasks = new ArrayList<>();
        Map<Long, Integer> remainingTime = new HashMap<>();

        for (Task task : tasks) {
            remainingTime.put(task.getId(), task.getBurstTime());
        }

        int currentTime = 0;
        int completedTasks = 0;
        int n = tasks.size();

        while (completedTasks < n) {
            int minRemainingTime = Integer.MAX_VALUE;
            Task shortestTask = null;

            // Find task with shortest remaining time at current time
            for (Task task : tasks) {
                if (task.getArrivalTime() <= currentTime &&
                        remainingTime.get(task.getId()) > 0 &&
                        remainingTime.get(task.getId()) < minRemainingTime) {
                    minRemainingTime = remainingTime.get(task.getId());
                    shortestTask = task;
                }
            }

            if (shortestTask == null) {
                currentTime++;
                continue;
            }

            // Execute for 1 time unit
            remainingTime.put(shortestTask.getId(),
                    remainingTime.get(shortestTask.getId()) - 1);
            currentTime++;

            // Check if task completed
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
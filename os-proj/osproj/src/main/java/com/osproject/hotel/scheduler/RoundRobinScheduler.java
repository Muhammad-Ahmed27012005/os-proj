package com.osproject.hotel.scheduler;


import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RoundRobinScheduler {

    public List<Task> schedule(List<Task> tasks, int timeQuantum) {
        List<Task> scheduledTasks = new ArrayList<>();
        Queue<Task> readyQueue = new LinkedList<>();

        // Initialize remaining burst times
        Map<Long, Integer> remainingTime = new HashMap<>();
        for (Task task : tasks) {
            remainingTime.put(task.getId(), task.getBurstTime());
        }

        // Sort by arrival time
        tasks.sort(Comparator.comparingInt(Task::getArrivalTime));

        int currentTime = 0;
        int index = 0;
        Map<Long, Integer> completionTime = new HashMap<>();
        Map<Long, Integer> startTime = new HashMap<>();

        while (index < tasks.size() || !readyQueue.isEmpty()) {
            // Add arriving tasks to ready queue
            while (index < tasks.size() && tasks.get(index).getArrivalTime() <= currentTime) {
                readyQueue.offer(tasks.get(index));
                startTime.put(tasks.get(index).getId(), currentTime);
                index++;
            }

            if (readyQueue.isEmpty()) {
                currentTime = tasks.get(index).getArrivalTime();
                continue;
            }

            Task currentTask = readyQueue.poll();
            int execTime = Math.min(timeQuantum, remainingTime.get(currentTask.getId()));

            currentTime += execTime;
            remainingTime.put(currentTask.getId(),
                    remainingTime.get(currentTask.getId()) - execTime);

            // Add newly arrived tasks during execution
            while (index < tasks.size() && tasks.get(index).getArrivalTime() <= currentTime) {
                readyQueue.offer(tasks.get(index));
                startTime.put(tasks.get(index).getId(), currentTime);
                index++;
            }

            if (remainingTime.get(currentTask.getId()) > 0) {
                readyQueue.offer(currentTask);
            } else {
                completionTime.put(currentTask.getId(), currentTime);
                currentTask.setTurnaroundTime(currentTime - currentTask.getArrivalTime());
                currentTask.setWaitingTime(currentTask.getTurnaroundTime() - currentTask.getBurstTime());
                currentTask.setStatus("COMPLETED");
                scheduledTasks.add(currentTask);
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
package com.osproject.hotel.scheduler;

import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RoundRobinScheduler {

    public List<Task> schedule(List<Task> tasks, int timeQuantum) {
        List<Task> mutableTasks = new ArrayList<>(tasks);
        List<Task> scheduledTasks = new ArrayList<>();
        Queue<Task> readyQueue = new LinkedList<>();

        Map<Long, Integer> remainingTime = new HashMap<>();
        for (Task task : mutableTasks) {
            remainingTime.put(task.getId(), task.getBurstTime());
        }

        mutableTasks.sort(Comparator.comparingInt(Task::getArrivalTime));

        int currentTime = 0;
        int index = 0;
        Map<Long, Integer> completionTime = new HashMap<>();

        while (index < mutableTasks.size() || !readyQueue.isEmpty()) {
            while (index < mutableTasks.size() && mutableTasks.get(index).getArrivalTime() <= currentTime) {
                readyQueue.offer(mutableTasks.get(index));
                index++;
            }
            if (readyQueue.isEmpty()) {
                currentTime = mutableTasks.get(index).getArrivalTime();
                continue;
            }
            Task currentTask = readyQueue.poll();
            int execTime = Math.min(timeQuantum, remainingTime.get(currentTask.getId()));
            currentTime += execTime;
            remainingTime.put(currentTask.getId(), remainingTime.get(currentTask.getId()) - execTime);
            while (index < mutableTasks.size() && mutableTasks.get(index).getArrivalTime() <= currentTime) {
                readyQueue.offer(mutableTasks.get(index));
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
package com.osproject.hotel.scheduler;

import com.osproject.hotel.model.Task;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class PriorityScheduler {

    public List<Task> schedule(List<Task> tasks) {
        List<Task> mutableTasks = new ArrayList<>(tasks);
        // sort by priority, then arrival time
        mutableTasks.sort((t1, t2) -> {
            if (t1.getPriority() != t2.getPriority())
                return Integer.compare(t1.getPriority(), t2.getPriority());
            return Integer.compare(t1.getArrivalTime(), t2.getArrivalTime());
        });

        int currentTime = 0;
        List<Task> scheduledTasks = new ArrayList<>();
        List<Task> pendingTasks = new ArrayList<>(mutableTasks);

        while (!pendingTasks.isEmpty()) {
            Task selectedTask = null;
            for (Task task : pendingTasks) {
                if (task.getArrivalTime() <= currentTime) {
                    if (selectedTask == null || task.getPriority() < selectedTask.getPriority()) {
                        selectedTask = task;
                    }
                }
            }
            if (selectedTask == null) {
                currentTime++;
                continue;
            }
            selectedTask.setWaitingTime(currentTime - selectedTask.getArrivalTime());
            currentTime += selectedTask.getBurstTime();
            selectedTask.setTurnaroundTime(selectedTask.getWaitingTime() + selectedTask.getBurstTime());
            selectedTask.setStatus("COMPLETED");
            scheduledTasks.add(selectedTask);
            pendingTasks.remove(selectedTask);
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
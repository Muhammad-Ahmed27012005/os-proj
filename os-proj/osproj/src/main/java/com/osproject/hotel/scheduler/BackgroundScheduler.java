package com.osproject.hotel.scheduler;

import com.osproject.hotel.model.Task;
import com.osproject.hotel.repository.TaskRepository;
import com.osproject.hotel.service.SchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BackgroundScheduler {

    private static final Logger log = LoggerFactory.getLogger(BackgroundScheduler.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SchedulingService schedulingService;

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void evaluateSchedulingAlgorithms() {
        List<Task> tasks = taskRepository.findAll();
        if (tasks.isEmpty()) {
            log.info("⏳ No tasks – skipping background scheduling.");
            return;
        }

        String bestAlgo = "";
        double bestAvgWait = Double.MAX_VALUE;

        String[] algorithms = { "FCFS", "SJF", "PRIORITY", "ROUNDROBIN" };
        for (String algo : algorithms) {
            try {
                Map<String, Object> result = schedulingService.scheduleWithAlgorithm(
                        algo, tasks, (algo.equals("ROUNDROBIN") ? 2 : 0));
                Map<String, Double> metrics = (Map<String, Double>) result.get("metrics");
                double avgWait = metrics.get("avgWaitingTime");
                if (avgWait < bestAvgWait) {
                    bestAvgWait = avgWait;
                    bestAlgo = algo;
                }
            } catch (Exception e) {
                log.error("Error running algorithm {}: {}", algo, e.getMessage());
            }
        }

        log.info("🏆 Background scheduling best algorithm: {} (avg waiting: {})", bestAlgo, bestAvgWait);
    }
}
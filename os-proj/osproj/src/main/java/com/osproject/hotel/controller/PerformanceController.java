package com.osproject.hotel.controller;

import com.osproject.hotel.model.Task;
import com.osproject.hotel.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private SchedulingService schedulingService;

    @GetMapping
    public String performancePage(Model model) {
        // Sample tasks for demonstration (or fetch from DB if needed)
        // Inside PerformanceController.java, replace List.of(...) with new ArrayList<>()
List<Task> sampleTasks = new ArrayList<>();
sampleTasks.add(createTask("Housekeeping R101", 5, 2, 0, "HOUSEKEEPING"));
sampleTasks.add(createTask("Maintenance R202", 3, 1, 1, "MAINTENANCE"));
sampleTasks.add(createTask("Room Service R303", 2, 3, 2, "ROOM_SERVICE"));
sampleTasks.add(createTask("Concierge R404", 4, 1, 3, "CONCIERGE"));
sampleTasks.add(createTask("Laundry R505", 6, 2, 4, "OTHER"));

        // Run all algorithms
        Map<String, Object> fcfs = schedulingService.scheduleWithAlgorithm("FCFS", sampleTasks, 0);
        Map<String, Object> sjf = schedulingService.scheduleWithAlgorithm("SJF", sampleTasks, 0);
        Map<String, Object> priority = schedulingService.scheduleWithAlgorithm("PRIORITY", sampleTasks, 0);
        Map<String, Object> roundrobin = schedulingService.scheduleWithAlgorithm("ROUNDROBIN", sampleTasks, 2);

        model.addAttribute("fcfs", fcfs.get("metrics"));
        model.addAttribute("sjf", sjf.get("metrics"));
        model.addAttribute("priority", priority.get("metrics"));
        model.addAttribute("roundrobin", roundrobin.get("metrics"));

        return "performance";
    }

    private Task createTask(String name, int burst, int priority, int arrival, String type) {
        Task t = new Task();
        t.setTaskName(name);
        t.setBurstTime(burst);
        t.setPriority(priority);
        t.setArrivalTime(arrival);
        t.setTaskType(type);
        t.setStatus("PENDING");
        return t;
    }
}
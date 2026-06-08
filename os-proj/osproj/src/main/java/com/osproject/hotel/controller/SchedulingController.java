package com.osproject.hotel.controller;



import com.osproject.hotel.model.Task;
import com.osproject.hotel.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private SchedulingService schedulingService;

    @GetMapping
    public String schedulingPage(Model model) {
        model.addAttribute("tasks", schedulingService.getAllTasks());
        model.addAttribute("task", new Task());
        return "scheduling";
    }

    @PostMapping("/task/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveTask(@RequestBody Task task) {
        Map<String, Object> response = new HashMap<>();
        try {
            Task savedTask = schedulingService.saveTask(task);
            response.put("success", true);
            response.put("task", savedTask);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule/{algorithm}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> scheduleTasks(
            @PathVariable String algorithm,
            @RequestParam(defaultValue = "2") int timeQuantum) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = schedulingService.getAllTasks();
            Map<String, Object> result = schedulingService.scheduleWithAlgorithm(algorithm, tasks, timeQuantum);
            response.put("success", true);
            response.put("data", result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/compare")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> compareAlgorithms() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = schedulingService.getAllTasks();
            Map<String, Object> comparison = new HashMap<>();

            // Run all algorithms
            comparison.put("FCFS", schedulingService.scheduleWithAlgorithm("FCFS", tasks, 0).get("metrics"));
            comparison.put("SJF", schedulingService.scheduleWithAlgorithm("SJF", tasks, 0).get("metrics"));
            comparison.put("Priority", schedulingService.scheduleWithAlgorithm("PRIORITY", tasks, 0).get("metrics"));
            comparison.put("RoundRobin",
                    schedulingService.scheduleWithAlgorithm("ROUNDROBIN", tasks, 2).get("metrics"));

            response.put("success", true);
            response.put("data", comparison);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
package com.kaiburr.task1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // GET all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // GET task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET tasks by name
    @GetMapping("/search")
    public ResponseEntity<List<Task>> getTasksByName(@RequestParam String name) {
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(t -> t.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tasks);
    }

    // PUT create or update a task
    @PutMapping
    public ResponseEntity<Task> createOrUpdateTask(@RequestBody Task task) {
        if (task.getCommand().contains("rm") || task.getCommand().contains("del")) {
            return ResponseEntity.badRequest().build();
        }
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    // DELETE a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // PUT execute task
    @PutMapping("/{id}/execute")
    public ResponseEntity<Task> executeTask(@PathVariable String id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Task task = optionalTask.get();
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Windows
                processBuilder.command("cmd.exe", "/c", task.getCommand());
            } else {
                // Linux / Mac
                processBuilder.command("/bin/bash", "-c", task.getCommand());
            }

            System.out.println("Executing command: " + String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errors = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errors.append(line).append("\n");
            }

            process.waitFor();
            String resultOutput = output.toString().trim();
            if (!errors.toString().isEmpty()) {
                resultOutput += "\nErrors:\n" + errors.toString().trim();
            }
            execution.setOutput(resultOutput);

        } catch (Exception e) {
            execution.setOutput("Error executing command: " + e.getMessage());
        }

        execution.setEndTime(new Date());
        task.getTaskExecutions().add(execution);
        taskRepository.save(task);

        return ResponseEntity.ok(task);
    }
}

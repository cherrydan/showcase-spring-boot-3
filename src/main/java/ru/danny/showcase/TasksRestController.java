package ru.danny.showcase;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/tasks")
public class TasksRestController {

    private final TaskRepository taskRepository;

    private final MessageSource messageSource;

    public TasksRestController(TaskRepository taskRepository, MessageSource messageSource) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;

    }

    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(this.taskRepository.findAll());

    }

    @PostMapping
    public ResponseEntity<?> handleCreateNewTask(@RequestBody NewTaskPayload payload,
                                                    UriComponentsBuilder uriComponentsBuilder,
                                                 Locale locale) {

        var message = this.messageSource.getMessage("tasks.create.details.errors.not_set",
                new Objects[0], locale);
        if (payload.details() == null || payload.details().isBlank()) {

            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).
                    body(new ErrorsPresentation(List.of(message))); //placeholder

        } else {
            var task = new Task(payload.details());
            this.taskRepository.save(task);
            return ResponseEntity.created(uriComponentsBuilder.path("/api/taks/{taskId}")
                    .build(Map.of("taskId", task.id()))).contentType(MediaType.APPLICATION_JSON).body(task);

        }
    }

}

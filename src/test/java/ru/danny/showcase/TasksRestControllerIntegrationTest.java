package ru.danny.showcase;
// ----------------------------------------------
// Класс интеграционных тестов Spring Boot Test
// ----------------------------------------------

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemTaskRepository taskRepository;

    @AfterEach //после каждого теста надо очистить данные в репо
    void clearRepo() {
        this.taskRepository.getTasks().clear();
    }

    @Test
    @DisplayName("GET api/tasks/ возвращает валидный HTTP ответ с кодом 200 и списком задач")
    void handleGetAllTaskTest_ReturnsValidResponseEntityTest() throws Exception {
        // given
        var requestBuilder = get("/api/tasks");
        this.taskRepository.getTasks().addAll(List.of(
                new Task(UUID.fromString("c52187e6-3788-11ee-9d98-0b0f39a49472"),
                "Первая задача", false),
                new Task(UUID.fromString("152f6b54-3789-11ee-a13a-b7f28337b9de"),
                        "Вторая задача", true)
        ));

        // when
        this.mockMvc.perform(requestBuilder).
                // then
                andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                {
                                "id": "c52187e6-3788-11ee-9d98-0b0f39a49472",
                                "details": "Первая задача",
                                "completed": false
                                },
                                {
                                "id": "152f6b54-3789-11ee-a13a-b7f28337b9de",
                                "details": "Вторая задача",
                                "completed": true
                                }
]
"""));

    }

    @Test
    @DisplayName("POST api/tasks принимает валидные данные, возвращает валидный HTTP ответ")
    public void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntityTest() throws Exception {
        // given
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details": "Третья задача"
                        }
                        """);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "details": "Третья задача"
                                }
                                """),
                        jsonPath("$.id").exists()
                );
        final var task = this.taskRepository.getTasks().get(0);
        assertEquals(1, this.taskRepository.getTasks().size());
        assertEquals("Третья задача", task.details());
        assertFalse(task.completed());
        assertNotNull(task.id());
    }

    @Test
    @DisplayName("POST api/tasks принимает НЕВАЛИДНЫЕ данные, возвращает валидный HTTP ответ")
    public void handleCreateNewTask_PayloadIsValid_ReturnsInvalidResponseEntityTest() throws Exception {
        // given
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details": null
                        }
                        """);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "errors": ["Task details must be set!"]
                                }
                                """, true)
                );
        assertTrue(this.taskRepository.getTasks().isEmpty());

    }
}
package ru.danny.showcase;
// ----------------------------------------------
// Класс интеграционных тестов Spring Boot Test
// ----------------------------------------------

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/sql/tasks_rest_controller/test_data.sql")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("GET api/tasks/ возвращает валидный HTTP ответ с кодом 200 и списком задач")
    void handleGetAllTaskTest_ReturnsValidResponseEntityTest() throws Exception {
        // given
        var requestBuilder = get("/api/tasks");

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


    }
}
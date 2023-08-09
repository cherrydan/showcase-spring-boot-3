package ru.danny.showcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    TasksRestController controller;

    @Test
    @DisplayName("GET api/tasks/ возвращает валидный HTTP ответ с кодом 200 и списком задач")
    void handleGetAllTaskTest_ReturnsValidResponseEntityTest() {
        //given Дано
        var tasks = List.of(new Task(UUID.randomUUID(), "Сделать инглиш", true),
                new Task(UUID.randomUUID(),"Поменять горшок Кейки", false));
        doReturn(tasks).when(this.taskRepository).findAll();

        //when
        var responseEntity = this.controller.handleGetAllTasks();

        //then
        assertNotNull(responseEntity);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        assertEquals(tasks, responseEntity.getBody());

    }

    @Test

    public void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntityTest() {
        // given
        var details = "Помыть посуду";

        // when
        var responseEntity = this.controller.handleCreateNewTask(
                new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"),
                Locale.US
        );
        // then
        assertNotNull(responseEntity);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        if(responseEntity.getBody()  instanceof Task task) {
            assertNotNull(task.id());
            assertEquals(details, task.details());
            assertFalse(task.completed());
            assertEquals(URI.create("http://localhost:8080/api/tasks/" + task.id()),
                    responseEntity.getHeaders().getLocation());
            verify(this.taskRepository).save(task);
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }
        verifyNoMoreInteractions(this.taskRepository);
    }



}
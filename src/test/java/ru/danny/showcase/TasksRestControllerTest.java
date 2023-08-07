package ru.danny.showcase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest {

    @Mock
    TaskRepository taskRepository;

    //@Mock
    //MessageSource messageSource;

    @InjectMocks
    TasksRestController controller;

    @Test
    void handleGetAllTaskTest_ReturnsValidResponseEntity() {
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



}
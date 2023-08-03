package ru.danny.showcase;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemTaskRepository implements TaskRepository {

    private final List<Task> tasks = new LinkedList<>() {{
        this.add(new Task("Сделать инглиш"));
        this.add(new Task("Поменять горшок Кейки"));

    }};
    @Override
    public List<Task> findAll() {
        return this.tasks;
    }
}

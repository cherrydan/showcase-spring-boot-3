package ru.danny.showcase;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JDBCOperationsTaskRepository implements  TaskRepository, RowMapper<Task> {

    private final JdbcOperations jdbcOperations;

    public JDBCOperationsTaskRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Task> findAll() {
        return this.jdbcOperations.query("SELECT * FROM t_task",this );
    }

    @Override
    public void save(Task task) {
        this.jdbcOperations.update("INSERT INTO t_task (id, c_details, c_completed) VALUES (?, ?, ?)",
                new Object[] { task.id(), task.details(), task.completed()});

    }

    @Override
    @Deprecated
    public Optional<Task> findById(UUID id) {
        return this.jdbcOperations.query("SELECT * FROM t_task WHERE id=?",
                new Object[]{id}, this).stream().findFirst();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Task(rs.getObject("id", UUID.class), rs.getString("c_details"),
                rs.getBoolean("c_completed"));
    }
}

package com.TaskManager.Repository;

import com.TaskManager.Model.AssignmentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AssignmentTaskRepository extends JpaRepository<AssignmentTask,Long> {

    @Transactional
    void deleteByTaskId(Long taskId);
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);

    @Transactional
    void deleteByUserIdAndTaskId(Long userId, Long taskId);
}

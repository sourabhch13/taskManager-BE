package com.TaskManager.Model;

import jakarta.persistence.*;

@Entity
@Table
public class AssignmentTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignedId;

    @JoinColumn(name = "taskId",referencedColumnName = "taskId",nullable = false)
    private Long taskId;

    @JoinColumn(name = "userId",referencedColumnName = "Id",nullable = false)
    private Long userId;

    public AssignmentTask() {
    }

    public AssignmentTask(Long taskId, Long userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Long getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(Long assignedId) {
        this.assignedId = assignedId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

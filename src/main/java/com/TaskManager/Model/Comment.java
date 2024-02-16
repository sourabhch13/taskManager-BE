package com.TaskManager.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(name = "commentatorName", nullable = false)
    private String commentatorName;
    @Column(name = "taskId", nullable = false)
    private Long taskId;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "message", nullable = false)
    private String message;

    public Comment() {
    }

    public Comment(Long commentId, String commentatorName, Long taskId, LocalDateTime createdAt, String message) {
        this.commentId = commentId;
        this.commentatorName = commentatorName;
        this.taskId = taskId;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentatorName() {
        return commentatorName;
    }

    public void setCommentatorName(String commentatorName) {
        this.commentatorName = commentatorName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

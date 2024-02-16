package com.TaskManager.Model;

import jakarta.persistence.*;

@Entity
@Table
public class BoardTaskTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardTaskTableId;

    @JoinColumn(name = "boardId", referencedColumnName = "boardId",nullable = false)
    private Long boardId;

    @JoinColumn(name = "taskId", referencedColumnName = "taskId",nullable = false)
    private Long taskId;

    public BoardTaskTable() {
    }

    public BoardTaskTable(Long boardId, Long taskId) {
        this.boardId = boardId;
        this.taskId = taskId;
    }

    public BoardTaskTable(Long boardTaskTableId, Long boardId, Long taskId) {
        this.boardTaskTableId = boardTaskTableId;
        this.boardId = boardId;
        this.taskId = taskId;
    }

    public Long getBoardTaskTableId() {
        return boardTaskTableId;
    }

    public void setBoardTaskTableId(Long boardTaskTableId) {
        this.boardTaskTableId = boardTaskTableId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}

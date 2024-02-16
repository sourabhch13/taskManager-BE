package com.TaskManager.Model;

import jakarta.persistence.*;

@Entity
@Table
public class BoardAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardAssignmentId;

    @JoinColumn(name = "boardId", referencedColumnName = "boardId")
    private Long boardId;
    @JoinColumn(name = "userId", referencedColumnName = "Id")
    private Long userId;

    public BoardAssignment() {
    }

    public BoardAssignment(Long boardId, Long userId) {
        this.boardId = boardId;
        this.userId = userId;
    }

    public BoardAssignment(Long boardAssignmentId, Long boardId, Long userId) {
        this.boardAssignmentId = boardAssignmentId;
        this.boardId = boardId;
        this.userId = userId;
    }

    public Long getBoardAssignmentId() {
        return boardAssignmentId;
    }

    public void setBoardAssignmentId(Long boardAssignmentId) {
        this.boardAssignmentId = boardAssignmentId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

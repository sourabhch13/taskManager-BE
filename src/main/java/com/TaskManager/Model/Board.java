package com.TaskManager.Model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;
    @Column(name = "title",nullable = false)
    private String title;
    @Column(name = "description",nullable = false)
    private String description;
    @Column(name = "createdAt")
    private LocalDate createdAt;
    @JoinColumn(name = "ownerId",referencedColumnName = "id")
    private Long ownerId;

    public Board() {
    }

    public Board(Long boardId, String title, String description, LocalDate createdAt, Long ownerId) {
        this.boardId = boardId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}

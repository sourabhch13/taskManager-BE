package com.TaskManager.Repository;

import com.TaskManager.Model.BoardAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardAssignmentRepository extends JpaRepository<BoardAssignment, Long> {
    List<BoardAssignment> findByUserId(Long userId);

    @Transactional
    void deleteByBoardId(Long boardId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    @Query("SELECT ba.userId FROM BoardAssignment ba WHERE ba.boardId = :boardId")
    List<Long> findUserIdByBoardId(Long boardId);

    @Query("SELECT ba.boardId FROM BoardAssignment ba WHERE ba.userId = :userId")
    List<Long> findBoardIdByUserId(Long userId);
;
}


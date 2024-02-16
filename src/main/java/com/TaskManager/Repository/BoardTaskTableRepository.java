package com.TaskManager.Repository;

import com.TaskManager.Model.BoardTaskTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BoardTaskTableRepository extends JpaRepository<BoardTaskTable,Long> {
//    boolean existByBoardIdAndTaskId(Long boardId, Long taskId);

    @Query("SELECT bt.boardId FROM BoardTaskTable bt WHERE bt.taskId = :taskId")
    Long findBoardIdByTaskId(@Param("taskId") Long taskId);

    @Transactional
    void deleteByTaskId(Long taskId);

    @Transactional
    void deleteByBoardId(Long boardId);

    @Query("SELECT bt.taskId FROM BoardTaskTable bt WHERE bt.boardId = :boardId")
    List<Long> findTaskIdByBoardId(@Param("boardId") Long boardId);
}

package com.TaskManager.Services;

import com.TaskManager.Model.Board;
import com.TaskManager.Model.Comment;
import com.TaskManager.Model.Task;
import com.TaskManager.Model.User;
import com.TaskManager.Repository.*;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardAssignmentRepository boardAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardTaskTableRepository boardTaskTableRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private TaskRepository taskRepository;
    public ApiResponse<Comment> makeComment(Comment comment, String token) {
        Long taskId = comment.getTaskId();
        Optional<Task> task = taskRepository.findById(taskId);
        if(!task.isPresent()) return new ApiResponse<>(400,"Failed","Task not found");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","User not found");
        Long boardId = boardTaskTableRepository.findBoardIdByTaskId(taskId);
        Optional<Board> board = boardRepository.findById(boardId);
        if(!board.isPresent()) return new ApiResponse<>(400,"Failed","No board found for this task");
        if(!boardAssignmentRepository.existsByBoardIdAndUserId(boardId,userId)){
            return new ApiResponse<>(400,"Failed","Join the board to comment");
        }
        if(comment.getMessage().isEmpty()) return new ApiResponse<>(400,"Failed","Message cannot be empty");
        comment.setCommentatorName(user.get().getName());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return new ApiResponse<>(200,"Success","Commented");
    }

    public ApiResponse<List<Comment>> seeComments(Long taskId, String token){
        Optional<Task> task = taskRepository.findById(taskId);
        if(!task.isPresent()) return new ApiResponse<>(400,"Failed","Task not found");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","User not found");
        Long boardId = boardTaskTableRepository.findBoardIdByTaskId(taskId);
        Optional<Board> board = boardRepository.findById(boardId);
        if(!board.isPresent()) return new ApiResponse<>(400,"Failed","No board found for this task");
        if(!boardAssignmentRepository.existsByBoardIdAndUserId(boardId,userId)){
            return new ApiResponse<>(400,"Failed","Join the board to see comment");
        }
        List<Comment> list = commentRepository.findAllByTaskId(taskId);
        return new ApiResponse<>(200,"Success","List of comment for a task", list);
    }
}

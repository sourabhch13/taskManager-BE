package com.TaskManager.Services;

import com.TaskManager.Model.Board;
import com.TaskManager.Model.Comment;
import com.TaskManager.Model.Task;
import com.TaskManager.Model.User;
import com.TaskManager.Repository.*;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.DESIGNATION;
import com.TaskManager.UtilityClasses.ROLE;
import com.TaskManager.UtilityClasses.STATUS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardTaskTableRepository boardTaskTableRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardAssignmentRepository boardAssignmentRepository;

    @Mock
    private CommentRepository commentRepository;


    @Test
    void makeCommentTaskInvalid() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Comment> res = commentService.makeComment(comment, "xyz");
        assertEquals("Task not found", res.getMessage());
    }

    @Test
    void makeCommentUserInvalid() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Comment> res = commentService.makeComment(comment, "xyz");
        assertEquals("User not found", res.getMessage());
    }

    @Test
    void makeCommentWithoutBoard() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Comment> res = commentService.makeComment(comment, "xyz");
        assertEquals("No board found for this task", res.getMessage());
    }

    @Test
    void makeCommentRequireToJoinBoard() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l, 1l)).thenReturn(false);
        ApiResponse<Comment> res = commentService.makeComment(comment, "xyz");
        assertEquals("Join the board to comment", res.getMessage());
    }

    @Test
    void makeCommentWithEmptyMessage() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l, 1l)).thenReturn(true);
        ApiResponse<Comment> res = commentService.makeComment(comment, "xyz");
        assertEquals("Message cannot be empty", res.getMessage());
    }

    @Test
    void makeComment() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l, 1l)).thenReturn(true);
        ApiResponse<Comment> res = commentService.makeComment(comment, "xyz");
        assertEquals("Commented", res.getMessage());
    }

    @Test
    void seeCommentsInvalidTask() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<List<Comment>> res = commentService.seeComments(1l, "xyz");
        assertEquals("Task not found", res.getMessage());
    }

    @Test
    void seeCommentsInvalidUser() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<List<Comment>> res = commentService.seeComments(1l, "xyz");
        assertEquals("User not found", res.getMessage());
    }

    @Test
    void seeCommentsWithoutBoard() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<List<Comment>> res = commentService.seeComments(1l, "xyz");
        assertEquals("No board found for this task", res.getMessage());
    }

    @Test
    void seeCommentsRequireToJoinBoardToSeeComments() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l, 1l)).thenReturn(false);
        ApiResponse<List<Comment>> res = commentService.seeComments(1l, "xyz");
        assertEquals("Join the board to see comment", res.getMessage());
    }

    @Test
    void seeComments() {
        User user = new User(1l, "user", "user@gmail.com", "12345", ROLE.USER, DESIGNATION.INTERN);
        Task task = new Task(1l, "title", "description", LocalDate.now(), 4, STATUS.INPROGRESS, 1l, 1l);
        Comment comment = new Comment(1l, "user", 1l, LocalDateTime.now(), "Commented on task");
        Board board = new Board(1l, "title", "description", LocalDate.now(), 1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardTaskTableRepository.findBoardIdByTaskId(1l)).thenReturn(1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l, 1l)).thenReturn(true);
        ApiResponse<List<Comment>> res = commentService.seeComments(1l, "xyz");
        assertEquals("List of comment for a task", res.getMessage());
    }
}
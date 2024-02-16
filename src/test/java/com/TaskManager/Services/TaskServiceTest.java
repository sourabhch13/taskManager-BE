package com.TaskManager.Services;

import com.TaskManager.Model.AssignmentTask;
import com.TaskManager.Model.Board;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardTaskTableRepository boardTaskTableRepository;

    @Mock
    private BoardAssignmentRepository boardAssignmentRepository;

    @Mock
    private AssignmentTaskRepository assignmentTaskRepository;


    @Test
    void getAllTaskWhenBoardNotAssigned() {
        Board board = new Board(1l,"title","description", LocalDate.now(),1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,1l)).thenReturn(false);
        ApiResponse<List<Task>> res = taskService.getAllTask(1l,"xyz");
        assertEquals("This board is not assigned to you",res.getMessage());
    }

    @Test
    void getAllTaskWhenNoBoard() {
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<List<Task>> res = taskService.getAllTask(1l,"xyz");
        assertEquals("Board id not found",res.getMessage());
    }


    @Test
    void getAllTask() {
        Board board = new Board(1l,"title","description", LocalDate.now(),1l);
        List<Long> list = new ArrayList<>();
        list.add(2l);
        list.add(3l);
        Task task = new Task(2l,"title2","description",LocalDate.now(),4, STATUS.INPROGRESS,1l,1l);
        Task task2 = new Task(3l,"title3","description",LocalDate.now(),4, STATUS.INPROGRESS,1l,1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,1l)).thenReturn(true);
        Mockito.when(boardTaskTableRepository.findTaskIdByBoardId(1l)).thenReturn(list);
        Mockito.when(taskRepository.findById(2l)).thenReturn(Optional.of(task));
        Mockito.when(taskRepository.findById(3l)).thenReturn(Optional.of(task2));
        ApiResponse<List<Task>> res = taskService.getAllTask(1l,"xyz");
        assertEquals("List of all task for this board",res.getMessage());
    }

    @Test
    void createTaskWithoutUser() {
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("User not Found",res.getMessage());
    }

    @Test
    void createTaskWithoutBoard() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("Board not found",res.getMessage());
    }

    @Test
    void createTaskWithEmptyTitle() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Task task = new Task(1l,"","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("title cannot be empty",res.getMessage());
    }

    @Test
    void createTaskWithEmptyDescription() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Task task = new Task(1l,"title","",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("description cannot be empty",res.getMessage());
    }

    @Test
    void createTaskDeadlineInvalid() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Task task = new Task(1l,"title","description",null,3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("deadline cannot be empty",res.getMessage());
    }

    @Test
    void createTaskPriorityNotValid() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Task task = new Task(1l,"title","description",LocalDate.now(),8,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("Priority invalid",res.getMessage());
    }

    @Test
    void createTaskNotValidStatus() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Task task = new Task();
        task.setPriority(2);
        task.setDeadline(LocalDate.now());
        task.setTitle("title");
        task.setBoardId(1l);
        task.setUserId(1l);
        task.setDescription("description");
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("Status invalid",res.getMessage());
    }

    @Test
    void createTask() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Task> res = taskService.createTask(task,"xyz");
        assertEquals("Task created successfully",res.getMessage());
    }

    @Test
    void deleteTaskEmptyUser() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.deleteTask(1l,"xyz");
        assertEquals("User not found",res.getMessage());
    }

    @Test
    void deleteTaskEmptyTask() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.deleteTask(1l,"xyz");
        assertEquals("Task not found",res.getMessage());
    }

    @Test
    void deleteTaskNotValid() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,2l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        ApiResponse<Task> res = taskService.deleteTask(1l,"xyz");
        assertEquals("Invalid request",res.getMessage());
    }

    @Test
    void deleteTask() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        ApiResponse<Task> res = taskService.deleteTask(1l,"xyz");
        assertEquals("Task deleted successfully",res.getMessage());
    }

    @Test
    void updateTaskStatusInvalidTask() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.updateTaskStatus(task,"xyz");
        assertEquals("Task id is invalid",res.getMessage());
    }

    @Test
    void updateTaskStatusUserInvalid() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.updateTaskStatus(task,"xyz");
        assertEquals("User not Found",res.getMessage());
    }

    @Test
    void updateTaskStatusTaskNotAssigned() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(1l,1l)).thenReturn(false);
        ApiResponse<Task> res = taskService.updateTaskStatus(task,"xyz");
        assertEquals("This task is not assigned to you",res.getMessage());
    }

    @Test
    void updateTaskStatus() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(1l,1l)).thenReturn(true);
        ApiResponse<Task> res = taskService.updateTaskStatus(task,"xyz");
        assertEquals("Status of the task updated",res.getMessage());
    }

    @Test
    void updateTaskTaskNotFound() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("Task not Found",res.getMessage());
    }

    @Test
    void updateTaskUserNotFound() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("User not Found",res.getMessage());
    }

    @Test
    void updateTaskPriviledgeFailed() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,2l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("You cannot update task details",res.getMessage());
    }

    @Test
    void updateTaskEmptyTitle() {
        Task task = new Task(1l,"","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("Title cannot be empty",res.getMessage());
    }

    @Test
    void updateTaskEmptyDescription() {
        Task task = new Task(1l,"title","",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("Description cannot be empty",res.getMessage());
    }

    @Test
    void updateTaskInvalidDate() {
        Task task = new Task(1l,"title","description",null,4,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("Date is not valid",res.getMessage());
    }

    @Test
    void updateTaskPriorityInvalid() {
        Task task = new Task(1l,"title","description",LocalDate.now(),8,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("Priority invalid",res.getMessage());
    }

    @Test
    void updateTask() {
        Task task = new Task(1l,"title","description",LocalDate.now(),4,STATUS.INPROGRESS,1l,1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<Task> res = taskService.updateTask(task,"xyz");
        assertEquals("Task updated successfully",res.getMessage());
    }

    @Test
    void assigningTaskInvalidTask() {
        //Arrange
        Long taskId = 1l;
        List<Long> idList = new ArrayList<>();
        idList.add(1l);
        idList.add(2l);
        idList.add(3l);
        String token = "xyz";
        Task task = new Task();
        task.setUserId(5l);
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        //Act
        ApiResponse<Task> res = taskService.assigningTask(taskId,idList,token);
        assertEquals("Task not found",res.getMessage());
    }

    @Test
    void assigningTaskInvalidUser() {
        //Arrange
        Long taskId = 1l;
        List<Long> idList = new ArrayList<>();
        idList.add(1l);
        idList.add(2l);
        idList.add(3l);
        String token = "xyz";
        Task task = new Task();
        task.setUserId(5l);
        User mainUser = new User(5l,"mainUser","main@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken(token)).thenReturn(mainUser.getUserId().toString());
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(mainUser.getId())).thenReturn(Optional.empty());
        //Act
        ApiResponse<Task> res = taskService.assigningTask(taskId,idList,token);
        assertEquals("User not found",res.getMessage());
    }

    @Test
    void assigningTaskPriviledgeFailed() {
        //Arrange
        Long taskId = 1l;
        List<Long> idList = new ArrayList<>();
        idList.add(1l);
        idList.add(2l);
        idList.add(3l);
        String token = "xyz";
        Task task = new Task();
        task.setUserId(4l);
        User mainUser = new User(5l,"mainUser","main@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken(token)).thenReturn(mainUser.getUserId().toString());
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(mainUser.getId())).thenReturn(Optional.of(mainUser));
        //Act
        ApiResponse<Task> res = taskService.assigningTask(taskId,idList,token);
        assertEquals("You cannot assign task to anyone",res.getMessage());
    }

    @Test
    void assigningTaskWhenUserAlreadyAssignedAndUserNotFound() {
        //Arrange
        Long taskId = 1l;
        List<Long> idList = new ArrayList<>();
        idList.add(1l);
        idList.add(2l);
        idList.add(3l);
        String token = "xyz";
        Task task = new Task();
        task.setUserId(5l);
        User usr1 = new User(1l,"one","one@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        User usr2 = new User(2l,"two","two@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        User usr3 = new User(3l,"three","three@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        User mainUser = new User(5l,"mainUser","main@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken(token)).thenReturn(mainUser.getUserId().toString());
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(usr1.getId())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(usr2.getId())).thenReturn(Optional.of(usr2));
        Mockito.when(userRepository.findById(usr3.getId())).thenReturn(Optional.of(usr3));
        Mockito.when(userRepository.findById(mainUser.getId())).thenReturn(Optional.of(mainUser));
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(taskId,usr2.getId())).thenReturn(true);
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(taskId,usr3.getId())).thenReturn(false);
        Mockito.when(assignmentTaskRepository.save(any())).thenReturn(any());
        //Act
        ApiResponse<Task> res = taskService.assigningTask(taskId,idList,token);
        assertEquals("1, User not found, 2, Already assigned to this users, rest user assigned the task",res.getMessage());
    }

    @Test
    void assigningTask() {
        //Arrange
        Long taskId = 1l;
        List<Long> idList = new ArrayList<>();
        idList.add(1l);
        idList.add(2l);
        idList.add(3l);
        String token = "xyz";
        Task task = new Task();
        task.setUserId(5l);
        User usr1 = new User(1l,"one","one@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        User usr2 = new User(2l,"two","two@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        User usr3 = new User(3l,"three","three@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        User mainUser = new User(5l,"mainUser","main@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken(token)).thenReturn(mainUser.getUserId().toString());
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(usr1.getId())).thenReturn(Optional.of(usr1));
        Mockito.when(userRepository.findById(usr2.getId())).thenReturn(Optional.of(usr2));
        Mockito.when(userRepository.findById(usr3.getId())).thenReturn(Optional.of(usr3));
        Mockito.when(userRepository.findById(mainUser.getId())).thenReturn(Optional.of(mainUser));
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(taskId,usr2.getId())).thenReturn(false);
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(taskId,usr1.getId())).thenReturn(false);
        Mockito.when(assignmentTaskRepository.existsByTaskIdAndUserId(taskId,usr3.getId())).thenReturn(false);
        Mockito.when(assignmentTaskRepository.save(any())).thenReturn(any());
        //Act
        ApiResponse<Task> res = taskService.assigningTask(taskId,idList,token);
        assertEquals("Task assigned successfully",res.getMessage());
    }

    @Test
    void assignTaskWithInvalidUserToken(){
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.assignTask(1l,2l,"xyz");
        assertEquals("Invalid token user",res.getMessage());
    }

    @Test
    void assignTaskToEmptyUser(){
        User mainUser = new User(1l,"main user","user@main.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(mainUser));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.assignTask(1l,2l,"xyz");
        assertEquals("User you provided not Found",res.getMessage());
    }

    @Test
    void assignTaskWhenTaskInvalid(){
        User mainUser = new User(1l,"main user","user@main.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user = new User(2l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(mainUser));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Task> res = taskService.assignTask(1l,2l,"xyz");
        assertEquals("Task not Found",res.getMessage());
    }

    @Test
    void assignTaskWithoutPrivilege(){
        User mainUser = new User(1l,"main user","user@main.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user = new User(2l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,3l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(mainUser));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        ApiResponse<Task> res = taskService.assignTask(1l,2l,"xyz");
        assertEquals("Task is not assigned to you",res.getMessage());
    }

    @Test
    void assignTaskToAlreadyAssignedPerson(){
        User mainUser = new User(1l,"main user","user@main.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        User user = new User(2l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,2l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(mainUser));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        ApiResponse<Task> res = taskService.assignTask(1l,2l,"xyz");
        assertEquals("already assigned to user",res.getMessage());
    }

    @Test
    void assignTask(){
        User mainUser = new User(1l,"main user","user@main.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user = new User(2l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Task task = new Task(1l,"title","description",LocalDate.now(),3,STATUS.INPROGRESS,1l,1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(mainUser));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1l)).thenReturn(Optional.of(task));
        ApiResponse<Task> res = taskService.assignTask(1l,2l,"xyz");
        assertEquals("Task assigned to user",res.getMessage());
    }
}
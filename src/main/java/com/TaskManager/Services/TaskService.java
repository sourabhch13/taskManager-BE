package com.TaskManager.Services;

import com.TaskManager.Model.*;
import com.TaskManager.Repository.*;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.ROLE;
import com.TaskManager.UtilityClasses.STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private BoardTaskTableRepository boardTaskTableRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private AssignmentTaskRepository assignmentTaskRepository;

    @Autowired
    private BoardAssignmentRepository boardAssignmentRepository;

    public ApiResponse<List<Task>> getAllTask(Long boardId,String token) {
        Optional<Board> b = boardRepository.findById(boardId);
        if(!b.isPresent()) return new ApiResponse<>(400,"Not Found","Board id not found");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        String res = "";
        List<Task> listAns = new ArrayList<>();
        if(boardAssignmentRepository.existsByBoardIdAndUserId(boardId,userId)){
            List<Long> list = boardTaskTableRepository.findTaskIdByBoardId(boardId);
            for(Long i:list){
                Optional<Task> t = taskRepository.findById(i);
                if(t.isPresent()){
                    listAns.add(t.get());
                }
            }
        }
        else return new ApiResponse<>(400,"Failed","This board is not assigned to you");
        return new ApiResponse<>(200,"Success","List of all task for this board",listAns);
    }

    public ApiResponse<Task> createTask(Task task, String token) {
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> u = userRepository.findById(userId);
        if(!u.isPresent()) return new ApiResponse<>(400,"Failed","User not Found");
        Long boardId = task.getBoardId();
        Optional<Board> b = boardRepository.findById(boardId);
        if(!b.isPresent()) return new ApiResponse<>(400,"Failed","Board not found");
        if(task.getTitle().isEmpty()) return new ApiResponse<>(400,"Failed","title cannot be empty");
        if(task.getDescription().isEmpty()) return new ApiResponse<>(400,"Failed","description cannot be empty");
        if (task.getDeadline() == null || task.getDeadline().isBefore(LocalDate.now())) return new ApiResponse<>(400,"Failed","deadline cannot be empty");
        if(task.getPriority()!=1 && task.getPriority()!=2 && task.getPriority()!=3 && task.getPriority()!=4 && task.getPriority()!=5) return new ApiResponse<>(400,"Failed","Priority invalid");
        if(task.getStatus()==null || (!task.getStatus().equals(STATUS.BLOCKER) && !task.getStatus().equals(STATUS.INPROGRESS) && !task.getStatus().equals(STATUS.COMPLETE))) return new ApiResponse<>(400,"Failed","Status invalid");
        task.setUserId(userId);
        taskRepository.save(task);
        Long taskId = task.getTaskId();
        boardTaskTableRepository.save(new BoardTaskTable(boardId,taskId));
        assignmentTaskRepository.save(new AssignmentTask(taskId, userId));
        return new ApiResponse<>(201,"Created","Task created successfully",task);
    }

    public ApiResponse<Task> assignTask(Long taskId, Long userId, String token){
        Long usrId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(usrId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","Invalid token user");
        Optional<User> user2 = userRepository.findById(userId);
        if(!user2.isPresent()) return new ApiResponse<>(400,"Failed","User you provided not Found");
        Optional<Task> task = taskRepository.findById(taskId);
        if(!task.isPresent()) return new ApiResponse<>(400,"Failed","Task not Found");
        if(!Objects.equals(task.get().getUserId(), usrId) && user.get().getRole()!=ROLE.MANAGER) return new ApiResponse<>(400,"Failed","Task is not assigned to you");
        if(task.get().getUserId().equals(userId)) return new ApiResponse<>(400,"Failed","already assigned to "+user2.get().getName());
        assignmentTaskRepository.deleteByUserIdAndTaskId(task.get().getUserId(),taskId);
        task.get().setUserId(userId);
        taskRepository.save(task.get());
        assignmentTaskRepository.save(new AssignmentTask(taskId,userId));
        return new ApiResponse<>(200,"Assigned","Task assigned to "+user2.get().getName());
    }

    public ApiResponse<Task> deleteTask(Long taskId, String token) {
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","User not found");
        Optional<Task> task = taskRepository.findById(taskId);
        if(!task.isPresent()) return new ApiResponse<>(400,"Failed","Task not found");
        if(task.get().getUserId()==userId || user.get().getRole()== ROLE.MANAGER){
            boardTaskTableRepository.deleteByTaskId(taskId);
            assignmentTaskRepository.deleteByTaskId(taskId);
            taskRepository.deleteById(taskId);
            return new ApiResponse<>(200,"Success","Task deleted successfully");
        }
        return new ApiResponse<>(400,"Failed","Invalid request");
    }

    public ApiResponse<Task> updateTaskStatus(Task task, String token){
        Long taskId = task.getTaskId();
        STATUS status = task.getStatus();
        if(status!=STATUS.BLOCKER && status!=STATUS.COMPLETE && status!=STATUS.INPROGRESS) return new ApiResponse<>(400,"Failed","Status invalid");
        Optional<Task> t = taskRepository.findById(taskId);
        if(!t.isPresent()) return new ApiResponse<>(400,"Failed","Task id is invalid");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","User not Found");
        if(!assignmentTaskRepository.existsByTaskIdAndUserId(taskId,userId)) return new ApiResponse<>(400,"Failed","This task is not assigned to you");
        t.get().setStatus(status);
        taskRepository.save(t.get());
        return new ApiResponse<>(200,"Success","Status of the task updated");
    }

    public ApiResponse<Task> updateTask(Task task, String token){
        Optional<Task> t = taskRepository.findById(task.getTaskId());
        if(!t.isPresent()) return new ApiResponse<>(400,"Failed","Task not Found");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","User not Found");
        if(task.getUserId()!=userId && user.get().getRole()!=ROLE.MANAGER) return new ApiResponse<>(400,"Failed","You cannot update task details");
        if(task.getTitle().isEmpty()) return new ApiResponse<>(400,"Failed","Title cannot be empty");
        if(task.getDescription().isEmpty()) return new ApiResponse<>(400,"Failed","Description cannot be empty");
        if(task.getDeadline()==null || task.getDeadline().isBefore(LocalDate.now())) return new ApiResponse<>(400,"Failed","Date is not valid");
        if(task.getPriority()!=1 && task.getPriority()!=2 && task.getPriority()!=3 && task.getPriority()!=4 && task.getPriority()!=5 ) return new ApiResponse<>(400,"Failed","Priority invalid");
        t.get().setDeadline(task.getDeadline());
        t.get().setTitle(task.getTitle());
        t.get().setDescription(task.getDescription());
        t.get().setPriority(task.getPriority());
        taskRepository.save(t.get());
        return new ApiResponse<>(200,"Success","Task updated successfully");
    }

    public ApiResponse<Task> assigningTask(Long taskId, List<Long> idList, String token){
        Optional<Task> task = taskRepository.findById(taskId);
        if(!task.isPresent()) return new ApiResponse<>(400,"Failed","Task not found");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","User not found");
        if(task.get().getUserId()!=userId && user.get().getRole()!=ROLE.MANAGER) return new ApiResponse<>(400,"Failed","You cannot assign task to anyone");
        String l = "";
        String l2 = "";
        for(Long i:idList){
            Optional<User> u = userRepository.findById(i);
            if(u.isPresent()){
                if(assignmentTaskRepository.existsByTaskIdAndUserId(taskId,i)) {
                    l2+=(i+", ");
                }
                else {
                    assignmentTaskRepository.save(new AssignmentTask(taskId,i));
                }

            }else {
                l+=(i+", ");
            }
        }
        String message = "";
        if(l.length()>0) message+=(l+"User not found, ");
        if(l2.length()>0) message+=(l2+"Already assigned to this users, ");
        if(l2.length()>0 || l.length()>0) return new ApiResponse<>(200,"Success",message+"rest user assigned the task");
        return new ApiResponse<>(200,"Success","Task assigned successfully");
    }
}

package com.TaskManager.Controller;

import com.TaskManager.Model.Task;
import com.TaskManager.Services.TaskService;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.LongList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public String hello(){
    return "Hello";}

    @PostMapping
    public ResponseEntity<ApiResponse<Task>> createTask(@RequestBody Task task, @RequestHeader("Authorization") String header){
        ApiResponse<Task> res = taskService.createTask(task,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<List<Task>>> getAllTask(@PathVariable Long boardId, @RequestHeader("Authorization") String header){
        ApiResponse<List<Task>> res =  taskService.getAllTask(boardId,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Task>> deleteTask(@PathVariable Long taskId, @RequestHeader("Authorization") String header){
        ApiResponse<Task> res = taskService.deleteTask(taskId,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ApiResponse<Task>> updateTask(@RequestBody Task task, @RequestHeader("Authorization") String header){
        ApiResponse<Task> res = taskService.updateTask(task,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PutMapping(path = "/status")
    public ResponseEntity<ApiResponse<Task>> updateTaskStatus(@RequestBody Task task, @RequestHeader("Authorization") String header){
        ApiResponse<Task> res = taskService.updateTaskStatus(task,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PostMapping(path = "/assignTaskTo/{userId}")
    public ResponseEntity<ApiResponse<Task>> assignTask(@PathVariable Long userId, @RequestBody Task task, @RequestHeader("Authorization") String header){
        ApiResponse<Task> res = taskService.assignTask(task.getTaskId(), userId, header.substring(7));
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PostMapping(path = "/assigningTask/{taskId}")
    public ResponseEntity<ApiResponse<Task>> assigningTask(@PathVariable Long taskId,@RequestBody LongList idList, @RequestHeader("Authorization") String header){
        ApiResponse<Task> res = taskService.assigningTask(taskId, idList.getIdList(),header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }
}

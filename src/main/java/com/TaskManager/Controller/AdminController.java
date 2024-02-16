package com.TaskManager.Controller;

import com.TaskManager.Model.User;
import com.TaskManager.Services.AdminService;
import com.TaskManager.UtilityClasses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<User>> makeManager(@RequestBody Map<String,String> password, @RequestHeader("Authorization") String Header){
        ApiResponse<User> res = adminService.makeManager(password.get("password"), Header.substring(7));
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getStatusCode()));
    }
}

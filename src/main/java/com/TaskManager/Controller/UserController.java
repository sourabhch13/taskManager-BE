package com.TaskManager.Controller;
import com.TaskManager.Model.User;
import com.TaskManager.Services.UserServices;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    @Autowired
    private UserServices userServices;


    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> allUser(){
        return new ResponseEntity<>(userServices.allUser(),HttpStatusCode.valueOf(200));
    }

    @PostMapping(path = "signup")
    public ResponseEntity<ApiResponse<User>> setUser(@RequestBody User user){
        ApiResponse<User> res =  userServices.signup(user);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PostMapping(path = "login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody User user){
        ApiResponse<JwtResponse> res = userServices.login(user.getEmail(),user.getPassword());
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<User>> deleteUser(@RequestHeader("Authorization") String header){
        ApiResponse<User> res = userServices.deleteUser(header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PostMapping(path = "getMe")
    public ResponseEntity<ApiResponse<User>> getMe(@RequestHeader("Authorization") String header){
//        if(header.length()<7) return new ResponseEntity<>(new ApiResponse<>(),HttpStatusCode.valueOf(400));
        ApiResponse<User> res = userServices.getMe(header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }
}

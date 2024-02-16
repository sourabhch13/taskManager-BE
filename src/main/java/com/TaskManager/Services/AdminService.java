package com.TaskManager.Services;

import com.TaskManager.Model.User;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.ROLE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Value("${MANAGER_PASSWORD}")
    private String Password;

    public void setPassword(String password) {
        Password = password;
    }

    @Autowired
    private JwtHelper jwtHelper;

    public ApiResponse<User> makeManager(String password,String token) {
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","Invalid user");
        if (!password.equals(Password)) return new ApiResponse<>(400,"Failed","Password Failed");
        if(user.get().getRole()==ROLE.MANAGER) return new ApiResponse<>(200,"Success","You're already a Manager");
        user.get().setRole(ROLE.MANAGER);
        userRepository.save(user.get());
        return new ApiResponse<>(200,"Success","You are manager now");
    }
}

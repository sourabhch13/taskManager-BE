package com.TaskManager.Services;

import com.TaskManager.Model.User;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.DESIGNATION;
import com.TaskManager.UtilityClasses.ROLE;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Value("${MANAGER_PASSWORD}")
    private String Password;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtHelper jwtHelper;

    @Test
    void makeManagerInvalidUser() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<User> res = adminService.makeManager("blackPearl","xyz");
        assertEquals("Invalid user",res.getMessage());
    }

    @Test
    void makeManagerAlreadyManager() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        String password = "blackPearl";
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        adminService.setPassword("blackPearl");
        ApiResponse<User> res = adminService.makeManager("blackPearl","xyz");
        assertEquals("You're already a Manager",res.getMessage());
    }

    @Test
    void makeManagerPasswordFailed() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ApiResponse<User> res = adminService.makeManager("12345","xyz");
        assertEquals("Password Failed",res.getMessage());
    }

    @Test
    void makeManager() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        adminService.setPassword("blackPearl");
        ApiResponse<User> res = adminService.makeManager("blackPearl","xyz");
        assertEquals("You are manager now",res.getMessage());
    }
}
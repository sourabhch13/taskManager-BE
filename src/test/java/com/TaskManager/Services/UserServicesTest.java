package com.TaskManager.Services;

import com.TaskManager.Model.User;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServicesTest {

    @InjectMocks
    private UserServices userServices;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserByToken userByToken;

    @Mock
    private UserRepository userRepository;

    @Test
    void signupEmptyName() {
        User user = new User(1l,"","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        ApiResponse<User> usr = userServices.signup(user);
        assertEquals("Please provide your name",usr.getMessage());
    }

    @Test
    void signupNullDesignation() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER,null);
        ApiResponse<User> usr = userServices.signup(user);
        assertEquals("Please provide your designation",usr.getMessage());
    }

    @Test
    void signupEmptyEmail() {
        User user = new User(1l,"user","","12345", ROLE.USER, DESIGNATION.INTERN);
        ApiResponse<User> usr = userServices.signup(user);
        assertEquals("Email required",usr.getMessage());
    }

    @Test
    void signupEmptyPassword() {
        User user = new User(1l,"user","user@gmail.com","", ROLE.USER, DESIGNATION.INTERN);
        ApiResponse<User> usr = userServices.signup(user);
        assertEquals("Password is required",usr.getMessage());
    }

    @Test
    void signupEmailBadlyFormatted() {
        User user = new User(1l,"user","userEmail","12345", ROLE.USER, DESIGNATION.INTERN);
        ApiResponse<User> usr = userServices.signup(user);
        assertEquals("Email badly formatted",usr.getMessage());
    }

    @Test
    void signupEmailExist() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        ApiResponse<User> usr = userServices.signup(user);
        String message = user.getEmail()+" already exits";
        assertEquals(message,usr.getMessage());
    }

    @Test
    void signupSuccess() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        ApiResponse<User> usr = userServices.signup(user);
        assertEquals("User created Successfully",usr.getMessage());
    }

    @Test
    void loginEmptyEmail() {
        String email = "";
        String password = "12345";
        ApiResponse<JwtResponse> res = userServices.login(email,password);
        assertEquals("Email cannot be empty",res.getMessage());
    }

    @Test
    void loginEmptyPassword() {
        String email = "user@gmail.com";
        String password = "";
        ApiResponse<JwtResponse> res = userServices.login(email,password);
        assertEquals("Password cannot be empty",res.getMessage());
    }

    @Test
    void loginEmailBadlyFormatted() {
        String email = "userEmail";
        String password = "12345";
        ApiResponse<JwtResponse> res = userServices.login(email,password);
        assertEquals("Email badly formatted",res.getMessage());
    }

    @Test
    void loginInvalidPassword() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        String email = "user@gmail.com";
        String password = "12345";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        ApiResponse<JwtResponse> res = userServices.login(email,password);
        assertEquals("Invalid Credentials",res.getMessage());
    }

    @Test
    void login() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        String email = "user@gmail.com";
        String password = "12345";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(true);
        ApiResponse<JwtResponse> res = userServices.login(email,password);
        assertEquals("Logged In",res.getMessage());
    }

    @Test
    void loginInvalidUser() {
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        String email = "user@gmail.com";
        String password = "1234";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        ApiResponse<JwtResponse> res = userServices.login(email,password);
        assertEquals("Invalid Credentials",res.getMessage());
    }

    @Test
    void allUser() {
        List<User> list = new ArrayList<>();
        list.add(new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN));
        list.add(new User(2l,"user2","user2@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN));
        Mockito.when(userRepository.findAll()).thenReturn(list);
        ApiResponse<List<User>> res = userServices.allUser();
        assertEquals(res.getData(),list);
    }

    @Test
    void deleteUserNot() {
        String token = "xyz";
        User user = new User(1l,"user","","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken(token)).thenReturn(user);
        ApiResponse<User> res = userServices.deleteUser(token);
        assertEquals("User not Found",res.getMessage());
    }

    @Test
    void deleteUser() {
        String token = "xyz";
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken(token)).thenReturn(user);
        ApiResponse<User> res = userServices.deleteUser(token);
        assertEquals(user.getEmail()+" Deleted successfully",res.getMessage());
    }

    @Test
    void loadUserByUsername() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        assertEquals(user,userServices.loadUserByUsername("1"));
    }

    @Test
    void getMeEmailInvalid() {
        User user = new User(1l,"user","","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<User> res = userServices.getMe("xyz");
        assertEquals("Invalid request",res.getMessage());
    }

    @Test
    void getMe() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<User> res = userServices.getMe("xyz");
        assertEquals("User exits",res.getMessage());
    }
}
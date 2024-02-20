package com.TaskManager.Services;
import com.TaskManager.Model.User;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.JwtResponse;
import com.TaskManager.UtilityClasses.ROLE;
import com.TaskManager.UtilityClasses.UserByToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserByToken userByToken;
    @Autowired
    private JwtHelper jwtHelper;

    public ApiResponse<User> signup(User user) {
        if(user.getName().isEmpty()) return new ApiResponse<User>(400,"Failed","Please provide your name");
        user.setRole(ROLE.USER);
        if(user.getDesignation()==null) return new ApiResponse<User>(400,"Failed","Please provide your designation");
        if(user.getPassword().isEmpty()) return new ApiResponse<User>(400,"Failed","Password is required");
        if(user.getEmail().isEmpty()) return new ApiResponse<User>(400,"Failed","Email required");
        if(!user.getEmail().matches(EMAIL_REGEX)) return new ApiResponse<User>(400,"Failed","Email badly formatted");
        String email = user.getEmail();
        Optional<User> u = userRepository.findByEmail(email);
        if(u.isPresent()){
            return new ApiResponse<User>(400,"Failed",email+" already exits");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ApiResponse<User>(201,"Created","User created Successfully");
    }

    public ApiResponse<JwtResponse> login(String email, String password){
        if(email.isEmpty()) return new ApiResponse<JwtResponse>(400,"Failed","Email cannot be empty");
        if(password.isEmpty()) return new ApiResponse<JwtResponse>(400,"Failed","Password cannot be empty");
        if(!email.matches(EMAIL_REGEX)) return new ApiResponse<JwtResponse>(400,"Failed","Email badly formatted");
        Optional<User> u = userRepository.findByEmail(email);
        if(u.isPresent()) {
            User currUser = u.get();
            if(!bCryptPasswordEncoder.matches(password,currUser.getPassword())) return new ApiResponse<JwtResponse>(400,"Failed","Invalid Credentials");
            currUser.setPassword("");
            String token = jwtHelper.generateToken(currUser);
            return new ApiResponse<JwtResponse>(200,"Success","Logged In",new JwtResponse(currUser,token));
        }
        return new ApiResponse<JwtResponse>(400,"Failed","Invalid Credentials!");
    }

    public ApiResponse<List<User>> allUser(){
        return new ApiResponse<>(200,"Success","List of all Users",userRepository.findAll());
    }

    public ApiResponse<User> deleteUser(String token){
        User u = userByToken.userByToken(token);
        if(u.getEmail().isEmpty()){
            return new ApiResponse<User>(400,"Failed","User not Found");
        }
        userRepository.deleteById(u.getId());
        return new ApiResponse<User>(200,"Success",u.getEmail()+" Deleted successfully");
    }

    public User loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> opUser=userRepository.findById(Long.parseLong(userId));
        return opUser.isPresent() ? opUser.get() : new User();
    }

    public ApiResponse<User> getMe(String token) {
        User u = userByToken.userByToken(token);
        if(u.getEmail().isEmpty()) {
            return new ApiResponse<User>(400,"Failed","Invalid request");
        }
        u.setPassword("");
        return new ApiResponse<User>(200, "Success", "User exits",u);
    }


}

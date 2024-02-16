package com.TaskManager.UtilityClasses;

import com.TaskManager.Model.User;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserByToken {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;
    public User userByToken(String token){
        String userId = jwtHelper.getUserIdFromToken(token);
        Optional<User> user = userRepository.findById(Long.parseLong(userId));
        if(user.isPresent()){
            User u = user.get();
            return new User(u.getId(),u.getName(),u.getEmail(),u.getRole(),u.getDesignation());
        }
        return new User();
    }
}

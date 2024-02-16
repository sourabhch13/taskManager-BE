package com.TaskManager.Model;

import com.TaskManager.UtilityClasses.DESIGNATION;
import com.TaskManager.UtilityClasses.ROLE;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "role",nullable = false)
    @Enumerated(EnumType.STRING)
    private ROLE role;
    @Column(name = "designation", nullable = false)
    @Enumerated(EnumType.STRING)
    private DESIGNATION designation;

    public User() {
    }

    public User(Long id, String name, String email, String password, ROLE role, DESIGNATION designation) {
        Id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.designation = designation;
    }

    public User(Long id, String name, String email, ROLE role, DESIGNATION designation) {
        Id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.designation = designation;
    }

    public User(String name, String email, String password, ROLE role, DESIGNATION designation) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.designation = designation;
    }

    public User(User user) {
        this.name = user.name;
        this.email = user.email;
        this.password = user.password;
        this.role = user.role;
        this.designation = user.designation;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getUserId(){
        return ""+this.getId();
    }
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }

    public DESIGNATION getDesignation() {
        return designation;
    }

    public void setDesignation(DESIGNATION designation) {
        this.designation = designation;
    }
}

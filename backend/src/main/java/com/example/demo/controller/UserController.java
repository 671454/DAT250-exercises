package com.example.demo.controller;

import com.example.demo.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.PollManagerV2;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController //                 <--- Combines @Controller and @ResponsBody. Makes everything returned serialized to JSON (via Jackson dependency)
@RequestMapping("/users") //    <--- Sets a base path for all methods in the class
public class UserController {
    private final PollManagerV2 manager;

    public UserController(PollManagerV2 manager) {
        this.manager = manager;
    }

    @PostMapping
    public User createUser(@RequestBody User user) { //     <--- @RequestBody binds the JSON-body from the request to a Java object (User)
        if(user.getEmail() == null || user.getUsername() == null || user.getEmail().isBlank() || user.getUsername().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and Email must be set");
        int userId = manager.createUser(user.getUsername(), user.getEmail());
        return manager.getUser(userId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") int userId) { //        <--- @PathVariable binds URL-variable to method-parameter
        try {
            return manager.getUser(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public Collection<User> listUsers() {
        try {
            return manager.getAllUsers();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



}

package com.example.demo.controller;

import com.example.demo.domain.User;
import org.springframework.web.bind.annotation.*;
import service.PollManager;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final PollManager manager;

    public UserController(PollManager manager) {
        this.manager = manager;
    }

    private record UserResponse(int id, String username, String email){}

    @GetMapping
    public List<UserResponse> listUsers(){
        return manager.getUsersById()
                .entrySet()
                .stream()
                .map(e -> new UserResponse(e.getKey(), e.getValue().getUsername(), e.getValue().getEmail())).toList();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        User u = manager.getUsersById().get(id);
        if(u == null) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        return u;
    }

    @PostMapping
    public User createUser(@RequestBody String username, String email) {
        if(username != null && email != null) {
            int id = manager.createUser(username, email);
            return manager.getUser(id);
        }
        throw new RuntimeException("Either username or email is empty");
    }


}

package domain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public List<UserResponse> listUsers(){
        return manager.getUsersById()
                .entrySet()
                .stream()
                .map(e -> new UserResponse(e.getKey(), e.getValue().getUsername(), e.getValue().getEmail())).toList();
    }

}

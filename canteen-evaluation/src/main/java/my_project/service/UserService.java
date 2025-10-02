package my_project.service;

import my_project.entity.User;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);
    boolean register(User user);
    User login(String username, String password);
}


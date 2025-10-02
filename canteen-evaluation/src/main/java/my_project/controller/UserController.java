package my_project.controller;

import my_project.entity.User;
import my_project.service.UserService;
import my_project.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> resp = new HashMap<>();
        // 禁止客户端直接指定角色（首个用户自动成为 admin，之后为 user）
        user.setRole(null);
        if (userService.findByUsername(user.getUsername()) != null) {
            resp.put("success", false);
            resp.put("message", "用户名已存在");
            return resp;
        }
        boolean ok = userService.register(user);
        if (ok) {
            // 重新查询用户获取分配的角色
            User saved = userService.findByUsername(user.getUsername());
            String token = JwtUtil.generateToken(saved.getId(), saved.getUsername(), saved.getRole());
            resp.put("success", true);
            resp.put("message", "注册成功");
            resp.put("role", saved.getRole());
            resp.put("token", token);
        } else {
            resp.put("success", false);
            resp.put("message", "注册失败");
        }
        return resp;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        User loginUser = userService.login(username, password);
        Map<String, Object> result = new HashMap<>();
        if (loginUser != null) {
            String token = JwtUtil.generateToken(loginUser.getId(), loginUser.getUsername(), loginUser.getRole());
            result.put("token", token);
            result.put("role", loginUser.getRole());
            result.put("success", true);
            result.put("message", "登录成功");
        } else {
            result.put("token", null);
            result.put("role", null);
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    @GetMapping("/me")
    public User me(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}

package my_project.controller;

import my_project.entity.Dish;
import my_project.service.DishService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("")
    public List<Dish> getAllDishes() {
        return dishService.getAllDishes();
    }

    @GetMapping("/{id}")
    public Dish getDishById(@PathVariable Long id) {
        return dishService.getDishById(id);
    }

    @PostMapping("")
    public boolean addDish(@RequestBody Dish dish, HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !"admin".equals(claims.get("role"))) return false;
        return dishService.addDish(dish);
    }

    @PutMapping("")
    public boolean updateDish(@RequestBody Dish dish, HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !"admin".equals(claims.get("role"))) return false;
        return dishService.updateDish(dish);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDish(@PathVariable Long id, HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !"admin".equals(claims.get("role"))) return false;
        return dishService.deleteDish(id);
    }
}

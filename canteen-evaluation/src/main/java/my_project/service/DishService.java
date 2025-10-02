package my_project.service;

import my_project.entity.Dish;

import java.util.List;

public interface DishService {
    List<Dish> getAllDishes();

    Dish getDishById(Long id);

    boolean addDish(Dish dish);

    boolean updateDish(Dish dish);

    boolean deleteDish(Long id);
}

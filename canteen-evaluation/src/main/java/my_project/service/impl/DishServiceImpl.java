package my_project.service.impl;

import my_project.entity.Dish;
import my_project.mapper.DishMapper;
import my_project.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Dish> getAllDishes() {
        return dishMapper.findAll();
    }

    @Override
    public Dish getDishById(Long id) {
        return dishMapper.findById(id);
    }

    @Override
    public boolean addDish(Dish dish) {
        return dishMapper.insert(dish) > 0;
    }

    @Override
    public boolean updateDish(Dish dish) {
        return dishMapper.update(dish) > 0;
    }

    @Override
    public boolean deleteDish(Long id) {
        return dishMapper.delete(id) > 0;
    }
}

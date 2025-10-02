package my_project.mapper;

import my_project.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {
    List<Dish> findAll();

    Dish findById(Long id);

    int insert(Dish dish);

    int update(Dish dish);

    int delete(Long id);
}

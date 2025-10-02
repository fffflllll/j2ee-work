package my_project.mapper;
import my_project.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EvaluationMapper {
    List<Evaluation> findByDishId(Long dishId);
    int insert(Evaluation evaluation);
    List<Evaluation> findAll();
    int deleteById(Long id); // 新增删除
}

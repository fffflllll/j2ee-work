package my_project.service;
import my_project.entity.Evaluation;
import java.util.List;

public interface EvaluationService {
    List<Evaluation> getEvaluationsByDishId(Long dishId);
    boolean addEvaluation(Evaluation evaluation);
    List<Evaluation> getAllEvaluations();
    boolean deleteEvaluation(Long id); // 新增
}

package my_project.service.impl;

import my_project.entity.Evaluation;
import my_project.mapper.EvaluationMapper;
import my_project.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    @Autowired
    private EvaluationMapper evaluationMapper;

    @Override
    public List<Evaluation> getEvaluationsByDishId(Long dishId) {
        return evaluationMapper.findByDishId(dishId);
    }
    @Override
    public boolean addEvaluation(Evaluation evaluation) {
        return evaluationMapper.insert(evaluation) > 0;
    }

    @Override
    public List<Evaluation> getAllEvaluations() {
        return evaluationMapper.findAll();
    }

    @Override
    public boolean deleteEvaluation(Long id) {
        return evaluationMapper.deleteById(id) > 0;
    }
}

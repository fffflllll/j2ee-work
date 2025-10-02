package my_project.controller;

import my_project.entity.Evaluation;
import my_project.service.EvaluationService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @GetMapping("/dish/{dishId}")
    public List<Evaluation> getEvaluationsByDishId(@PathVariable Long dishId) {
        return evaluationService.getEvaluationsByDishId(dishId);
    }

    @PostMapping("")
    public boolean addEvaluation(@RequestBody Evaluation evaluation, HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null) return false;
        Object uid = claims.get("userId");
        Long userId = null;
        if (uid instanceof Integer) userId = ((Integer) uid).longValue();
        else if (uid instanceof Long) userId = (Long) uid;
        else if (uid instanceof Number) userId = ((Number) uid).longValue();
        if (userId == null) return false;
        evaluation.setUserId(userId);
        return evaluationService.addEvaluation(evaluation);
    }

    @GetMapping("")
    public List<Evaluation> getAllEvaluations() {
        return evaluationService.getAllEvaluations();
    }

    @DeleteMapping("/{id}")
    public boolean deleteEvaluation(@PathVariable Long id, HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !"admin".equals(claims.get("role"))) return false;
        return evaluationService.deleteEvaluation(id);
    }
}

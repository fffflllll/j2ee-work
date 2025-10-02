package my_project.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation {
    private Long id;
    private Long dishId;
    private Long userId;
    private Integer score;
    private String comment;
    private Timestamp createTime;
}

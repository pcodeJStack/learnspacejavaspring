package phucitdev.course.modules.lesson_quiz.dto.submission;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradedAnswerResponse {
    private UUID answerId;
    private String questionContent;
    private Integer maxPoints;
    private Integer gradedPoints;
}
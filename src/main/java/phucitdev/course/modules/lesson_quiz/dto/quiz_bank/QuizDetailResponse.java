package phucitdev.course.modules.lesson_quiz.dto.quiz_bank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import phucitdev.course.modules.lesson_quiz.entity.QuizType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class QuizDetailResponse {

    private UUID id;

    private String title;

    private String description;

    private Integer durationMinutes;

    private Integer passScore;

    private QuizType quizType;
    private Integer version;
    private List<QuestionDetailResponse> questions;
}

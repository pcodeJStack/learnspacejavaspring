package phucitdev.course.modules.lesson_quiz.dto.result_quiz;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {

    private UUID submissionId;

    private UUID quizId;

    private String quizTitle;

    private Integer score;

    private Integer passScore;

    private Boolean passed;

    private String status;

    private List<QuestionResultResponse> questions;

}
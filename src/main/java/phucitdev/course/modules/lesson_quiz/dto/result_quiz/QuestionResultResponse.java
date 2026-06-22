package phucitdev.course.modules.lesson_quiz.dto.result_quiz;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultResponse {

    private UUID questionId;

    private String questionContent;

    private Integer points;
    private Integer gradedPoints;

    private Boolean correct;

    // student trả lời gì
    private UUID selectedOptionId;

    private String essayAnswer;

    // đáp án
    private List<OptionResultResponse> options;
}
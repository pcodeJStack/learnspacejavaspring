package phucitdev.course.modules.lesson_quiz.dto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private UUID questionId;
    private String content;
    private Integer points;

    // ESSAY
    private String essayAnswer;

    // MULTIPLE_CHOICE
    private List<OptionResponse>
            options;
}
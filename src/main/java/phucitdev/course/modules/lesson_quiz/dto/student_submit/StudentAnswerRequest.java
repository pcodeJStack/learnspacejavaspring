package phucitdev.course.modules.lesson_quiz.dto.student_submit;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StudentAnswerRequest {
    private UUID questionId;

    // MULTIPLE_CHOICE
    private UUID selectedOptionId;

    // ESSAY
    private String essayAnswer;
}
package phucitdev.course.modules.lesson_quiz.dto.submission;

import lombok.Data;

import java.util.UUID;

@Data
public class GradeAnswerRequest {
    private UUID answerId;
    private Integer score;
}
package phucitdev.course.modules.lesson_quiz.dto.submission;

import lombok.Data;

import java.util.List;

@Data
public class GradeSubmissionRequest {
    private List<GradeAnswerRequest> answers;
}
package phucitdev.course.modules.lesson_quiz.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GradedSubmissionResponse {
    private UUID submissionId;
    private String studentName;
    private Integer totalScore;
    private Boolean passed;
    private List<GradedAnswerResponse> answers;
}

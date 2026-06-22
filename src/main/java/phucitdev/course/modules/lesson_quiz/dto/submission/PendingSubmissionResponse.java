package phucitdev.course.modules.lesson_quiz.dto.submission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingSubmissionResponse {
    private UUID submissionId;
    private String studentName;
    private List<PendingAnswerResponse> answers;
}
package phucitdev.course.modules.lesson_quiz.dto.student_submit;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SubmitQuizRequest {
    private List<StudentAnswerRequest> answers;
}
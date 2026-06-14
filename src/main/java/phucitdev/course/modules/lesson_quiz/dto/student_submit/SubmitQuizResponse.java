package phucitdev.course.modules.lesson_quiz.dto.student_submit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubmitQuizResponse {

    private String message;
    private Integer score;
    private Boolean passed;
}
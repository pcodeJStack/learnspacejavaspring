package phucitdev.course.modules.lesson_quiz.dto.checking_lessonQuizCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckingLessonQuizCodeResponse {
    private Boolean success;
    private String message;
}

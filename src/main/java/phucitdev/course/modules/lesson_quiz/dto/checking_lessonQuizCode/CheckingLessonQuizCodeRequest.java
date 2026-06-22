package phucitdev.course.modules.lesson_quiz.dto.checking_lessonQuizCode;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckingLessonQuizCodeRequest {
    @NotBlank(message = "Mã quiz không được để trống!")
    private String lessonQuizCode;
}

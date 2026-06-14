package phucitdev.course.modules.lesson_quiz.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter
@Setter
public class CreateLessonQuizRequest {

    @NotBlank(message = "Tên quiz không được để trống")
    private String title;

    private String description;

    @NotNull(message = "SnapLessonId không được để trống")
    private UUID snapLessonId;

    private Integer durationMinutes;

    private Integer passScore;
}
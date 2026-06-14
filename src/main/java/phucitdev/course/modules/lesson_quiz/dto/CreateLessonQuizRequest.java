package phucitdev.course.modules.lesson_quiz.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import phucitdev.course.modules.lesson_quiz.entity.QuizType;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class CreateLessonQuizRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    @NotNull(message = "SnapLessonId không được để trống")
    private UUID snapLessonId;

    @Min(value = 1, message = "Thời gian phải lớn hơn 0")
    private Integer durationMinutes;

    @Min(value = 0, message = "Điểm đạt không hợp lệ")
    private Integer passScore;

    @NotNull(message = "Loại quiz không được để trống")
    private QuizType quizType;

    @Valid
    @NotEmpty(message = "Phải có ít nhất 1 câu hỏi")
    private List<QuestionRequest> questions;
}
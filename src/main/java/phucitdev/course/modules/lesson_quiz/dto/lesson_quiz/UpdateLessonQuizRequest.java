package phucitdev.course.modules.lesson_quiz.dto.lesson_quiz;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLessonQuizRequest {
    @NotBlank(message = "Tiêu đề không được để trống!")
    private String title;
    @NotBlank(message = "Mô tả không được để trống!")
    private String description;
    @NotNull(message = "Thời lượng không được để trống!")
    private Integer durationMinutes;
    @NotNull(message = "Điểm cần pass không được để trống!")
    private Integer passScore;
}

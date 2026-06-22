package phucitdev.course.modules.lesson_quiz.dto.quiz_update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phucitdev.course.modules.lesson_quiz.dto.OptionRequest;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionUpdateRequest {
    private UUID id;
    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;

    @Min(value = 1, message = "Điểm phải lớn hơn 0")
    private Integer points = 1;
    // ESSAY
    private String essayAnswer;

    // MULTIPLE_CHOICE
    private List<OptionUpdateRequest> options;
}

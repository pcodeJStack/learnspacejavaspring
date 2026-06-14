package phucitdev.course.modules.lesson_quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionRequest {

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;

    @Min(value = 1, message = "Điểm phải lớn hơn 0")
    private Integer points = 1;
    // ESSAY
    private String essayAnswer;

    // MULTIPLE_CHOICE
    private List<OptionRequest> options;
}
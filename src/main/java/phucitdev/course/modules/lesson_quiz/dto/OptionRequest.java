package phucitdev.course.modules.lesson_quiz.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionRequest {

    @NotBlank(message = "Nội dung đáp án không được để trống")
    private String content;
    @NotNull(message = "Trạng thái đáp án đúng/sai không được để trống")
    private Boolean correct;
}
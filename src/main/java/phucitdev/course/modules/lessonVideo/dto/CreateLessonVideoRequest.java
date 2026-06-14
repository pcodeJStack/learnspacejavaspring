package phucitdev.course.modules.lessonVideo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phucitdev.course.modules.lessonVideo.entity.VideoType;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLessonVideoRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    @NotNull(message = "Loại video không được để trống")
    private VideoType videoType;
    @NotBlank(message = "Video không được để trống")
    private String fileKey;
    @NotNull
    private UUID snapLessonId;
}

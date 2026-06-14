package phucitdev.course.modules.lessonVideo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phucitdev.course.modules.lessonVideo.entity.VideoType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLessonVideoResponse {
    private UUID id;
    private String title;
    private String videoUrl;
    private VideoType videoType;
    private LocalDateTime createdAt;
}

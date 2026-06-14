package phucitdev.course.modules.lesson_quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLessonQuizResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer passScore;
}

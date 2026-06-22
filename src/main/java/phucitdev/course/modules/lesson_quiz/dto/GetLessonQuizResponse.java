package phucitdev.course.modules.lesson_quiz.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetLessonQuizResponse {
    private UUID quizId;
    private String title;
    private String description;
    private String lessonQuizCode;
    private Integer durationMinutes;
    private Integer passScore;
    private String quizType;
    private List<QuestionResponse> questions;
}
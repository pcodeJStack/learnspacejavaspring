package phucitdev.course.modules.lesson_quiz.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingAnswerResponse {
    private UUID answerId;
    private String questionContent;
    private String essayAnswer;
}
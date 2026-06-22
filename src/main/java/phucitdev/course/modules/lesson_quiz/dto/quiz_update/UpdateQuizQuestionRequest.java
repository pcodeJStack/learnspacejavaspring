package phucitdev.course.modules.lesson_quiz.dto.quiz_update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuizQuestionRequest {
    private List<QuestionUpdateRequest> questions;
}

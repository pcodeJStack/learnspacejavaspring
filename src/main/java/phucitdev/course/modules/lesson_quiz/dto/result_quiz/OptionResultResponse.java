package phucitdev.course.modules.lesson_quiz.dto.result_quiz;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionResultResponse {

    private UUID optionId;

    private String content;

    private Boolean correct;
}
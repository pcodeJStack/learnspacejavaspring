package phucitdev.course.modules.lesson_quiz.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponse {
    private UUID optionId;
    private String content;
    private Boolean correct;
}
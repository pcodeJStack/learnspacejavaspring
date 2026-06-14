package phucitdev.course.modules.lesson_quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phucitdev.course.commo.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private StudentQuizSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    // MCQ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "selected_option_id"
    )
    private QuestionOption selectedOption;

    // ESSAY
    @Column(columnDefinition = "TEXT")
    private String essayAnswer;

    private Boolean correct;

    private Integer score;
}
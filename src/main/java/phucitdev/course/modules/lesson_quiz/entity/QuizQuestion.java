package phucitdev.course.modules.lesson_quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phucitdev.course.commo.base.BaseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion extends BaseEntity {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    private Integer points = 1;
    // chỉ dùng cho tự luận
    @Column(columnDefinition = "TEXT")
    private String essayAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private LessonQuiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionOption> options =new HashSet<>();
}
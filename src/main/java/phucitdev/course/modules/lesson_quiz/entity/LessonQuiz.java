package phucitdev.course.modules.lesson_quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import phucitdev.course.commo.base.BaseEntity;
import phucitdev.course.modules.snap_lesson.entity.SnapLesson;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lesson_quizzes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LessonQuiz extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer durationMinutes;
    private Integer passScore;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snap_lesson_id", nullable = false)
    private SnapLesson snapLesson;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> questions = new ArrayList<>();
}
package phucitdev.course.modules.lesson_quiz.entity;

import jakarta.persistence.*;
import lombok.*;

import phucitdev.course.commo.base.BaseEntity;

import phucitdev.course.modules.snap_lessonquiz.entity.SnapLessonQuiz;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;

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
    @Column(name = "lesson_quiz_code", nullable = false, unique = true)
    private String lessonQuizCode;
    private Integer version = 1;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizType quizType;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "snap_lesson_id", nullable = false)
//    private SnapLesson snapLesson;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherProfile teacher;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> questions = new ArrayList<>();

//    @OneToMany(mappedBy = "lessonQuiz")
//    private List<StudentQuizSubmission> submissions = new ArrayList<>();
    @OneToMany(mappedBy = "lessonQuiz")
    private List<SnapLessonQuiz> snapLessonQuizzes = new ArrayList<>();




}
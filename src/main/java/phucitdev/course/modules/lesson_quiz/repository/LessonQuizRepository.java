package phucitdev.course.modules.lesson_quiz.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.UpdateAssignedQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.UpdateAssignedQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_bank.QuizListResponse;
import phucitdev.course.modules.lesson_quiz.entity.LessonQuiz;
import phucitdev.course.modules.lesson_quiz.entity.QuizType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonQuizRepository extends JpaRepository<LessonQuiz, UUID> {
        @Query("""
        SELECT new phucitdev.course.modules.lesson_quiz.dto.quiz_bank.QuizListResponse(
            q.id,
            q.title,
            q.description,
            q.lessonQuizCode,
            q.durationMinutes,
            q.passScore,
            q.quizType,
            q.version,
            SIZE(q.questions)
        )
        FROM LessonQuiz q
        WHERE q.teacher.id = :teacherId
        AND (
            :title IS NULL
            OR LOWER(q.title)
            LIKE LOWER(
                CONCAT('%', :title, '%')
            )
        )
        AND (
            :quizType IS NULL
            OR q.quizType = :quizType
        )
        ORDER BY q.createdAt DESC
    """)
        Page<QuizListResponse> getTeacherQuizzes(
                UUID teacherId,
                String title,
                QuizType quizType,
                Pageable pageable
        );


    Optional<LessonQuiz> findByIdAndTeacherId(
            UUID quizId,
            UUID teacherId
    );
    boolean existsByLessonQuizCode(String lessonQuizCode);
    @Query("""
    SELECT DISTINCT q
    FROM LessonQuiz q
    LEFT JOIN FETCH q.questions qq
    LEFT JOIN FETCH qq.options
    WHERE q.id = :quizId
""")
    Optional<LessonQuiz> findDetail(
            @Param("quizId") UUID quizId
    );
}
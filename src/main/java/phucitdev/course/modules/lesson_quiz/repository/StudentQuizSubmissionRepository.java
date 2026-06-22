package phucitdev.course.modules.lesson_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import phucitdev.course.modules.lesson_quiz.entity.StudentQuizSubmission;
import phucitdev.course.modules.lesson_quiz.entity.SubmissionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentQuizSubmissionRepository extends JpaRepository<StudentQuizSubmission, UUID> {
    List<StudentQuizSubmission>
    findAllBySnapLessonQuizIdAndStudentIdOrderByCreatedAtDesc(
            UUID snapLessonQuizId,
            UUID studentId
    );
    long countBySnapLessonQuizIdAndStudentId(
            UUID snapLessonQuizId,
            UUID studentId
    );
    boolean  existsBySnapLessonQuizId(UUID snapLessonQuizId);
    boolean existsBySnapLessonQuiz_LessonQuiz_Id(UUID lessonQuizId);
    List<StudentQuizSubmission> findBySnapLessonQuizIdAndStatus(UUID snapLessonQuizId, SubmissionStatus status);
}

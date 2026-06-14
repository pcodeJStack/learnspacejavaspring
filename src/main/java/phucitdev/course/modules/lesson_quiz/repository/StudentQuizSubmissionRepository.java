package phucitdev.course.modules.lesson_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import phucitdev.course.modules.lesson_quiz.entity.StudentQuizSubmission;

import java.util.Optional;
import java.util.UUID;

public interface StudentQuizSubmissionRepository extends JpaRepository<StudentQuizSubmission, UUID> {
    boolean existsByLessonQuizIdAndStudentId(UUID quizId, UUID studentId);
    Optional<StudentQuizSubmission>
    findByLessonQuizIdAndStudentId(
            UUID quizId,
            UUID studentId
    );
}

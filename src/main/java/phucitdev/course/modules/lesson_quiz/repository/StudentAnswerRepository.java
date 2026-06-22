package phucitdev.course.modules.lesson_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import phucitdev.course.modules.lesson_quiz.entity.StudentAnswer;

import java.util.List;
import java.util.UUID;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, UUID> {
    List<StudentAnswer> findBySubmissionSnapLessonQuizIdAndScoreIsNull(UUID snapLessonQuizId);
}

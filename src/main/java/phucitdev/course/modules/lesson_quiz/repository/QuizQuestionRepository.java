package phucitdev.course.modules.lesson_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import phucitdev.course.modules.lesson_quiz.entity.QuizQuestion;

import java.util.UUID;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {
}

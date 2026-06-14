package phucitdev.course.modules.lesson_quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import phucitdev.course.modules.lesson_quiz.entity.QuestionOption;

import java.util.UUID;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, UUID> {
}

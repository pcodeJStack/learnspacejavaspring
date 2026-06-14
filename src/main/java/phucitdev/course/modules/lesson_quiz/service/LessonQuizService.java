package phucitdev.course.modules.lesson_quiz.service;

import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.LessonQuiz;

import java.util.List;
import java.util.UUID;

public interface LessonQuizService {
    CreateLessonQuizResponse createQuiz(CreateLessonQuizRequest request);
    List<GetLessonQuizResponse> getQuizzes(UUID snapLessonId);
}

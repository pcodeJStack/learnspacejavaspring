package phucitdev.course.modules.lesson_quiz.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.LessonQuiz;
import phucitdev.course.modules.lesson_quiz.service.LessonQuizService;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RequestMapping("/api")
@RestController
@SecurityRequirement(name = "api")
public class LessonQuizAPI {
    @Autowired
    LessonQuizService lessonQuizService;
    @PostMapping("/lesson-quiz")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody CreateLessonQuizRequest request) {
        CreateLessonQuizResponse response = lessonQuizService.createQuiz(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/snap-lessons/{snapLessonId}/quizzes")
    public ResponseEntity<?> getQuizzes(@PathVariable UUID snapLessonId) {
        List<GetLessonQuizResponse> response = lessonQuizService.getQuizzes(snapLessonId);
        return ResponseEntity.ok(response);
    }
}

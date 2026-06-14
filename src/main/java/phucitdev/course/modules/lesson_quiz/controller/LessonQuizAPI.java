package phucitdev.course.modules.lesson_quiz.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuizResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizResponse;
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
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody CreateLessonQuizRequest request) {
        CreateLessonQuizResponse response = lessonQuizService.createQuiz(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/lesson-quiz/{quizId}")
    public ResponseEntity<?> getQuizzes(@PathVariable UUID quizId) {
        GetLessonQuizResponse response = lessonQuizService.getQuizzes(quizId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/student/quizzes/{quizId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitQuiz(
            @PathVariable UUID quizId,
            @RequestBody
            SubmitQuizRequest request
    ) {
        SubmitQuizResponse response = lessonQuizService.submitQuiz(quizId, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/student/quizzes/{quizId}/result")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getQuizResult(@PathVariable UUID quizId) {
        QuizResultResponse response = lessonQuizService.getQuizResult(quizId);
        return ResponseEntity.ok(response);
    }
}

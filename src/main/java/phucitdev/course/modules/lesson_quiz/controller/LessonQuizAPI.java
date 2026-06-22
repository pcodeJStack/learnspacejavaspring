package phucitdev.course.modules.lesson_quiz.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.AssignQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.AssignQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.UpdateAssignedQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.UpdateAssignedQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.checking_lessonQuizCode.CheckingLessonQuizCodeRequest;
import phucitdev.course.modules.lesson_quiz.dto.checking_lessonQuizCode.CheckingLessonQuizCodeResponse;
import phucitdev.course.modules.lesson_quiz.dto.lesson_quiz.UpdateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.lesson_quiz.UpdateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_update.UpdateQuizQuestionRequest;
import phucitdev.course.modules.lesson_quiz.dto.quiz_update.UpdateQuizQuestionResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_bank.QuizListResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuizResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.QuizType;
import phucitdev.course.modules.lesson_quiz.service.LessonQuizService;

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
    @GetMapping("/lesson-quizs")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getMyQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false)
            QuizType quizType) {
        Page<QuizListResponse> response = lessonQuizService.getMyQuizzes(page, size, title, quizType);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/assign")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> assignQuiz(@Valid @RequestBody AssignQuizRequest request) {
       AssignQuizResponse response = lessonQuizService.assignQuiz(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getQuizDetail(@PathVariable UUID quizId) {
        return ResponseEntity.ok(lessonQuizService.getQuizDetail(quizId)
        );
    }


    @GetMapping("/lesson/{snapLessonId}")
    @PreAuthorize("""
    hasRole('TEACHER') or hasRole('STUDENT') 
    """)
    public ResponseEntity<?> getAssignedQuizzes(@PathVariable UUID snapLessonId) {
        return ResponseEntity.ok(lessonQuizService.getAssignedQuizzes(snapLessonId)
        );
    }
    @PutMapping("/assign/{snapLessonQuizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateAssignedQuiz(
            @PathVariable
            UUID snapLessonQuizId,
            @Valid
            @RequestBody
            UpdateAssignedQuizRequest request
    ) {
        UpdateAssignedQuizResponse response = lessonQuizService.updateAssignedQuiz(snapLessonQuizId, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/lesson-quiz/{quizId}")
    public ResponseEntity<?> getQuizzes(@PathVariable UUID quizId) {
        GetLessonQuizResponse response = lessonQuizService.getQuizzes(quizId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/lesson-quiz/checkingLessonQuizCode/{quizId}")
    public ResponseEntity<?> checkLessonQuizCode(@PathVariable UUID quizId, @Valid @RequestBody CheckingLessonQuizCodeRequest request) {
        CheckingLessonQuizCodeResponse response = lessonQuizService.checkLesonQuizCode(quizId, request);
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

    @PutMapping("/lessonQuiz/{lessonQuizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable UUID lessonQuizId, @Valid @RequestBody UpdateLessonQuizRequest request){
        UpdateLessonQuizResponse response =  lessonQuizService.updateLessonQuiz(lessonQuizId, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{quizId}/questions")
    public ResponseEntity<UpdateQuizQuestionResponse> updateQuestions(@PathVariable UUID quizId, @RequestBody @Valid UpdateQuizQuestionRequest request) {
        System.out.println("updateQuestions");
        return ResponseEntity.ok(lessonQuizService.updateQuestions(quizId, request));
    }

}

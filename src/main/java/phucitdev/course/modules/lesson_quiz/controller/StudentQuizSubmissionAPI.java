package phucitdev.course.modules.lesson_quiz.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.lesson_quiz.dto.submission.GradeSubmissionRequest;
import phucitdev.course.modules.lesson_quiz.dto.submission.GradeSubmissionResponse;
import phucitdev.course.modules.lesson_quiz.service.StudentQuizSubmissionService;

import java.util.UUID;

@CrossOrigin("*")
@RequestMapping("/api")
@RestController
@SecurityRequirement(name = "api")
public class StudentQuizSubmissionAPI {
    @Autowired
    StudentQuizSubmissionService studentQuizSubmissionService;
    @GetMapping("/snap-lesson-quizzes/{id}/pending")
    public ResponseEntity<?> getPending(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(studentQuizSubmissionService.getPendingAnswers(id));
    }
    @GetMapping("/snap-lesson-quizzes/{id}/graded")
    public ResponseEntity<?> getGraded(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(studentQuizSubmissionService.getGradedSubmissions(id));
    }
    @PostMapping("/teacher/submissions/{submissionId}/grade")
    public ResponseEntity<?> teacherGrading(@PathVariable("submissionId") UUID submissionId, @RequestBody GradeSubmissionRequest request) {
        GradeSubmissionResponse response = studentQuizSubmissionService.gradeSubmission(submissionId,request );
        return ResponseEntity.ok(response);
    }
}

package phucitdev.course.modules.lesson_quiz.service;

import phucitdev.course.modules.lesson_quiz.dto.submission.*;

import java.util.List;
import java.util.UUID;

public interface StudentQuizSubmissionService {
    List<PendingSubmissionResponse> getPendingAnswers(UUID snapLessonQuizId);
    List<GradedSubmissionResponse> getGradedSubmissions(UUID snapLessonQuizId);
    GradeSubmissionResponse gradeSubmission(UUID submissionId, GradeSubmissionRequest request);
}

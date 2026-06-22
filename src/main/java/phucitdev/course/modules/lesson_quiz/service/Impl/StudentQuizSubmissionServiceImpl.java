package phucitdev.course.modules.lesson_quiz.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.lesson_quiz.dto.submission.*;
import phucitdev.course.modules.lesson_quiz.entity.StudentAnswer;
import phucitdev.course.modules.lesson_quiz.entity.StudentQuizSubmission;
import phucitdev.course.modules.lesson_quiz.entity.SubmissionStatus;
import phucitdev.course.modules.lesson_quiz.repository.StudentAnswerRepository;
import phucitdev.course.modules.lesson_quiz.repository.StudentQuizSubmissionRepository;
import phucitdev.course.modules.lesson_quiz.service.StudentQuizSubmissionService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Service
public class StudentQuizSubmissionServiceImpl implements StudentQuizSubmissionService {
    @Autowired
    StudentAnswerRepository studentAnswerRepository;
    @Autowired
    StudentQuizSubmissionRepository studentQuizSubmissionRepository;
    @Override
    public List<PendingSubmissionResponse> getPendingAnswers(UUID snapLessonQuizId) {
        List<StudentQuizSubmission> submissions =
                studentQuizSubmissionRepository
                        .findBySnapLessonQuizIdAndStatus(
                                snapLessonQuizId,
                                SubmissionStatus.PENDING
                        );
        return submissions.stream()
                .map(submission ->
                        new PendingSubmissionResponse(
                                submission.getId(),
                                submission.getStudent().getAccount().getFullName(),
                                submission.getAnswers()
                                        .stream()
                                        .map(answer ->
                                                new PendingAnswerResponse(
                                                        answer.getId(),
                                                        answer.getQuestion().getContent(),
                                                        answer.getEssayAnswer()
                                                )
                                        )
                                        .toList()
                        )
                )
                .toList();
    }

    @Override
    public List<GradedSubmissionResponse> getGradedSubmissions(
            UUID snapLessonQuizId
    ) {

        List<StudentQuizSubmission> submissions =
                studentQuizSubmissionRepository
                        .findBySnapLessonQuizIdAndStatus(
                                snapLessonQuizId,
                                SubmissionStatus.GRADED
                        );

        return submissions.stream()
                .map(submission -> {

                    List<GradedAnswerResponse> answers =
                            submission.getAnswers()
                                    .stream()
                                    .map(answer ->
                                            new GradedAnswerResponse(
                                                    answer.getId(),
                                                    answer.getQuestion().getContent(),
                                                    answer.getQuestion().getPoints(),
                                                    answer.getScore()
                                            )
                                    )
                                    .toList();

                    return new GradedSubmissionResponse(
                            submission.getId(),
                            submission.getStudent().getAccount().getFullName(),
                            submission.getScore(),
                            submission.getPassed(),
                            answers
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public GradeSubmissionResponse gradeSubmission(UUID submissionId, GradeSubmissionRequest request) {
        StudentQuizSubmission submission =
                studentQuizSubmissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new NotFoundException("Submission không tồn tại")
                        );

        for (GradeAnswerRequest req : request.getAnswers()) {
            StudentAnswer answer = studentAnswerRepository.findById(req.getAnswerId())
                            .orElseThrow(() ->
                                    new NotFoundException("Answer không tồn tại")
                            );

            if (!answer.getSubmission().getId().equals(submissionId)) {
                throw new BadRequestException("Answer không thuộc submission");
            }

            Integer maxScore = answer.getQuestion().getPoints();

            if (req.getScore() < 0 || req.getScore() > maxScore) {
                throw new BadRequestException(
                        "Điểm không hợp lệ cho câu hỏi: "
                                + answer.getQuestion().getContent()
                );
            }

            answer.setScore(req.getScore());
        }

        int totalScore = submission.getAnswers()
                .stream()
                .map(StudentAnswer::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        submission.setScore(totalScore);

        Integer passScore = submission.getSnapLessonQuiz()
                .getLessonQuiz()
                .getPassScore();

        submission.setPassed(totalScore >= passScore);
        submission.setStatus(SubmissionStatus.GRADED);

        return new GradeSubmissionResponse(
                "Chấm bài thành công!"
        );
    }
}

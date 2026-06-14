package phucitdev.course.modules.lesson_quiz.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.lesson_quiz.dto.*;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.OptionResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuestionResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuizResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.StudentAnswerRequest;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.*;
import phucitdev.course.modules.lesson_quiz.repository.*;
import phucitdev.course.modules.lesson_quiz.service.LessonQuizService;
import phucitdev.course.modules.snap_lesson.entity.SnapLesson;
import phucitdev.course.modules.snap_lesson.repository.SnapLessonRepository;
import java.util.List;
import java.util.UUID;
@Service
public class LessonQuizServiceImpl implements LessonQuizService {
    @Autowired
    SnapLessonRepository  snapLessonRepository;
    @Autowired
    LessonQuizRepository lessonQuizRepository;
    @Autowired
    QuizQuestionRepository quizQuestionRepository;
    @Autowired
    QuestionOptionRepository questionOptionRepository;
    @Autowired
    StudentQuizSubmissionRepository studentQuizSubmissionRepository;
    @Autowired
    StudentAnswerRepository studentAnswerRepository;
    @Override
    public CreateLessonQuizResponse createQuiz(CreateLessonQuizRequest request) {
        validateQuestions(request);
        SnapLesson snapLesson = snapLessonRepository.findById(request.getSnapLessonId())
                        .orElseThrow(() -> new NotFoundException("SnapLesson không tồn tại"));
        LessonQuiz lessonQuiz = new LessonQuiz();
        lessonQuiz.setTitle(request.getTitle());
        lessonQuiz.setDescription(request.getDescription());
        lessonQuiz.setDurationMinutes(request.getDurationMinutes());
        lessonQuiz.setPassScore(request.getPassScore());
        lessonQuiz.setQuizType(request.getQuizType());
        lessonQuiz.setSnapLesson(snapLesson);

        lessonQuizRepository.save(lessonQuiz);

        for (QuestionRequest q : request.getQuestions()) {
            QuizQuestion question = new QuizQuestion();
            question.setContent(q.getContent());
            question.setPoints(q.getPoints());
            question.setQuiz(lessonQuiz);
            // ESSAY
            if (request.getQuizType() == QuizType.ESSAY) {
                question.setEssayAnswer(q.getEssayAnswer());
            }
            quizQuestionRepository.save(question);
            // MULTIPLE CHOICE
            if (request.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                for (OptionRequest o : q.getOptions()) {
                    QuestionOption option = new QuestionOption();
                    option.setContent(o.getContent());
                    option.setCorrect(o.getCorrect());
                    option.setQuestion(question);
                    questionOptionRepository.save(option);
                }
            }
        }

        return new CreateLessonQuizResponse(
                "Tạo đề thành công!"
        );
    }

    @Override
    public GetLessonQuizResponse getQuizzes(UUID quizId) {
        LessonQuiz quiz = lessonQuizRepository.findById(quizId).orElseThrow(() ->
                                new NotFoundException("Quiz không tồn tại")
                        );
        return new GetLessonQuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getPassScore(),
                quiz.getQuizType().name(),

                quiz.getQuestions()
                        .stream()
                        .map(question ->

                                new QuestionResponse(

                                        question.getId(),
                                        question.getContent(),
                                        question.getPoints(),

                                        // essay
                                        question.getEssayAnswer(),

                                        // options
                                        question.getOptions()
                                                .stream()
                                                .map(option ->

                                                        new OptionResponse(
                                                                option.getId(),
                                                                option.getContent(),
                                                                option.getCorrect()
                                                        )
                                                )
                                                .toList()
                                )
                        )
                        .toList()
        );
    }

    @Override
    public SubmitQuizResponse submitQuiz(UUID quizId, SubmitQuizRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        LessonQuiz quiz = lessonQuizRepository.findById(quizId).orElseThrow(() ->
                                new NotFoundException("Quiz không tồn tại")
                        );
        // check submit rồi chưa
        boolean submitted = studentQuizSubmissionRepository.existsByLessonQuizIdAndStudentId(
                                quizId, currentAccount.getStudent().getId());
        if (submitted) {
            throw new BadRequestException(
                    "Bạn đã nộp bài rồi"
            );
        }
        StudentQuizSubmission submission = new StudentQuizSubmission();
        submission.setLessonQuiz(quiz);
        submission.setStudent(currentAccount.getStudent());
        submission = studentQuizSubmissionRepository.save(submission);
        int totalScore = 0;
        // MULTIPLE_CHOICE
        if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
            for (StudentAnswerRequest answerRequest : request.getAnswers()) {
                QuizQuestion question = quizQuestionRepository.findById(answerRequest.getQuestionId())
                                .orElseThrow(() -> new NotFoundException("Question không tồn tại"));
                QuestionOption selectedOption = questionOptionRepository.findById(answerRequest.getSelectedOptionId())
                                .orElseThrow(() -> new NotFoundException("Đáp án không tồn tại"));
                boolean correct = Boolean.TRUE.equals(selectedOption.getCorrect());
                StudentAnswer studentAnswer = new StudentAnswer();
                studentAnswer.setSubmission(submission);
                studentAnswer.setQuestion(question);
                studentAnswer.setSelectedOption(selectedOption);
                studentAnswer.setCorrect(correct);
                if (correct) {
                    totalScore += question.getPoints();
                }
                studentAnswerRepository.save(studentAnswer);
            }

            submission.setScore(totalScore);
            submission.setPassed(totalScore >= quiz.getPassScore());
            submission.setStatus(SubmissionStatus.GRADED);

        }
        // ESSAY
        else {
            submission.setStatus(SubmissionStatus.PENDING);
            submission.setPassed(false);
            submission.setScore(0);
            for (StudentAnswerRequest answerRequest : request.getAnswers()) {
                QuizQuestion question = quizQuestionRepository.findById(answerRequest.getQuestionId())
                                .orElseThrow(() -> new NotFoundException("Question không tồn tại"));
                StudentAnswer studentAnswer = new StudentAnswer();
                studentAnswer.setSubmission(submission);
                studentAnswer.setQuestion(question);
                studentAnswer.setEssayAnswer(answerRequest.getEssayAnswer());
                studentAnswerRepository.save(studentAnswer);
            }
        }
        studentQuizSubmissionRepository.save(submission);
        return new SubmitQuizResponse(
                "Nộp bài thành công",
                submission.getScore(),
                submission.getPassed()
        );
    }

    @Override
    public QuizResultResponse getQuizResult(
            UUID quizId
    ) {

        Account currentAccount =
                SecurityUtils
                        .getCurrentAccount();

        StudentQuizSubmission
                submission =
                studentQuizSubmissionRepository
                        .findByLessonQuizIdAndStudentId(
                                quizId,
                                currentAccount
                                        .getStudent()
                                        .getId()
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Bạn chưa nộp bài này"
                                )
                        );

        LessonQuiz quiz =
                submission.getLessonQuiz();

        // chưa nộp
        if (submission.getStatus() == null) {

            return new QuizResultResponse(
                    submission.getId(),
                    quiz.getId(),
                    quiz.getTitle(),
                    null,
                    quiz.getPassScore(),
                    false,
                    "NOT_SUBMITTED",
                    List.of()
            );
        }

        List<QuestionResultResponse>
                questions =
                submission.getAnswers()
                        .stream()
                        .map(answer -> {

                            QuizQuestion question =
                                    answer.getQuestion();

                            return new QuestionResultResponse(
                                    question.getId(),
                                    question.getContent(),
                                    question.getPoints(),
                                    answer.getCorrect(),
                                    answer.getSelectedOption() != null
                                            ? answer.getSelectedOption().getId()
                                            : null,
                                    answer.getEssayAnswer(),
                                    question.getOptions()
                                            .stream()
                                            .map(option ->
                                                    new OptionResultResponse(
                                                            option.getId(),
                                                            option.getContent(),
                                                            option.getCorrect()
                                                    )
                                            )
                                            .toList()
                            );
                        })
                        .toList();

        return new QuizResultResponse(
                submission.getId(),
                quiz.getId(),
                quiz.getTitle(),
                submission.getScore(),
                quiz.getPassScore(),
                submission.getPassed(),
                submission.getStatus().name(),
                questions
        );
    }

    private void validateQuestions(CreateLessonQuizRequest request) {
        for (QuestionRequest q :
                request.getQuestions()) {

            // ESSAY
            if (request.getQuizType()
                    == QuizType.ESSAY) {

                if (q.getEssayAnswer()
                        == null ||
                        q.getEssayAnswer()
                                .isBlank()) {

                    throw new BadRequestException(
                            "Câu tự luận phải có đáp án mẫu"
                    );
                }
            }

            // MULTIPLE_CHOICE
            if (request.getQuizType()
                    == QuizType.MULTIPLE_CHOICE) {

                if (q.getOptions()
                        == null ||
                        q.getOptions()
                                .isEmpty()) {

                    throw new BadRequestException(
                            "Câu trắc nghiệm phải có đáp án"
                    );
                }

                long correctCount =
                        q.getOptions()
                                .stream()
                                .filter(
                                        OptionRequest
                                                ::getCorrect
                                )
                                .count();

                if (correctCount != 1) {

                    throw new BadRequestException(
                            "Phải có đúng 1 đáp án đúng"
                    );
                }
            }
        }
    }
}

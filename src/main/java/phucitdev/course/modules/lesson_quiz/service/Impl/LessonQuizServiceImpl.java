package phucitdev.course.modules.lesson_quiz.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.lesson_quiz.dto.*;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.*;
import phucitdev.course.modules.lesson_quiz.dto.quiz_bank.OptionDetailResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_bank.QuestionDetailResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_bank.QuizDetailResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_bank.QuizListResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.OptionResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuestionResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuizAttemptResponse;
import phucitdev.course.modules.lesson_quiz.dto.result_quiz.QuizResultResponse;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.StudentAnswerRequest;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.student_submit.SubmitQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.*;
import phucitdev.course.modules.lesson_quiz.repository.*;
import phucitdev.course.modules.lesson_quiz.service.LessonQuizService;
import phucitdev.course.modules.snap_lesson.entity.SnapLesson;
import phucitdev.course.modules.snap_lesson.repository.SnapLessonRepository;
import phucitdev.course.modules.snap_lessonquiz.entity.SnapLessonQuiz;
import phucitdev.course.modules.snap_lessonquiz.repository.SnapLessonQuizRepository;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;

import java.util.*;

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
    @Autowired
    SnapLessonQuizRepository  snapLessonQuizRepository;
    @Override
    @Transactional
    public CreateLessonQuizResponse createQuiz(CreateLessonQuizRequest request) {

        validateQuestions(request);
        Account account = SecurityUtils.getCurrentAccount();
        TeacherProfile teacherProfile = account.getTeacher();

        LessonQuiz lessonQuiz = new LessonQuiz();
        lessonQuiz.setTitle(request.getTitle());
        lessonQuiz.setDescription(request.getDescription());
        lessonQuiz.setDurationMinutes(request.getDurationMinutes());
        lessonQuiz.setPassScore(request.getPassScore());
        lessonQuiz.setQuizType(request.getQuizType());
        lessonQuiz.setTeacher(teacherProfile);

        lessonQuiz = lessonQuizRepository .save(lessonQuiz);

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
    public SubmitQuizResponse submitQuiz(UUID snapLessonQuizId, SubmitQuizRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID studentId = currentAccount .getStudent() .getId();
        SnapLessonQuiz lessonQuiz = snapLessonQuizRepository.findById(snapLessonQuizId)
                        .orElseThrow(() ->
                                new NotFoundException("Quiz không tồn tại"));

        LessonQuiz quiz = lessonQuiz.getLessonQuiz();

        long attemptCount = studentQuizSubmissionRepository.countBySnapLessonQuizIdAndStudentId( snapLessonQuizId, studentId );
        Integer maxAttempts = lessonQuiz.getMaxAttempts();
        if (maxAttempts != null && attemptCount >= maxAttempts) { throw new BadRequestException( "Bạn đã hết số lần làm bài" ); }
        // tạo submission
        StudentQuizSubmission submission = new StudentQuizSubmission();
        submission.setSnapLessonQuiz( lessonQuiz );
        submission.setStudent( currentAccount.getStudent());
        submission.setStatus( SubmissionStatus.PENDING );
        submission = studentQuizSubmissionRepository.save(submission);

        List<StudentAnswerRequest> answers = request.getAnswers();

        boolean hasValidAnswer = answers != null && answers.stream().anyMatch(a ->
                a.getQuestionId() != null &&
                        (a.getSelectedOptionId() != null || (a.getEssayAnswer() != null && !a.getEssayAnswer().isBlank()))
        );

        if (!hasValidAnswer) {
            submission.setScore(0);
            submission.setPassed(false);
            submission.setStatus(SubmissionStatus.ABANDONED);

            studentQuizSubmissionRepository.save(submission);

            return new SubmitQuizResponse(
                    "Bạn chưa trả lời câu hỏi nào",
                    0,
                    false
            );
        }
        int totalScore = 0;
        // chống submit trùng question
        Set<UUID> answeredQuestions = new HashSet<>();
        // MULTIPLE_CHOICE
        if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
            for (StudentAnswerRequest answerRequest : request.getAnswers()) {
                QuizQuestion question = quizQuestionRepository.findById(answerRequest.getQuestionId())
                                .orElseThrow(() -> new NotFoundException("Question không tồn tại"));
                if (!question.getQuiz().getId().equals(quiz.getId())) {
                    throw new BadRequestException( "Câu hỏi không thuộc quiz" );
                }
//               Chống trả lời trùng câu hỏi
                if (!answeredQuestions.add( question.getId())) {
                    throw new BadRequestException( "Không được trả lời trùng câu hỏi" );
                }

                QuestionOption selectedOption = questionOptionRepository.findById(answerRequest.getSelectedOptionId())
                                .orElseThrow(() -> new NotFoundException("Đáp án không tồn tại"));
                // check option thuộc question
                if (!selectedOption.getQuestion().getId().equals(question.getId())) {
                    throw new BadRequestException( "Đáp án không thuộc câu hỏi" );
                }
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
                // check question thuộc quiz
                if (!question.getQuiz().getId().equals(quiz.getId())) {
                    throw new BadRequestException( "Câu hỏi không thuộc quiz" );
                }
                // chống duplicate
                if (!answeredQuestions.add( question.getId())) {
                    throw new BadRequestException( "Không được trả lời trùng câu hỏi" );
                }
                if (answerRequest.getEssayAnswer() == null || answerRequest.getEssayAnswer().isBlank()) {
                    throw new BadRequestException( "Không được bỏ trống câu trả lời" );
                }

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
    @Transactional(readOnly = true)
    public QuizResultResponse getQuizResult(UUID snapLessonQuizId) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID studentId = currentAccount.getStudent().getId();
        List<StudentQuizSubmission> submissions = studentQuizSubmissionRepository
                        .findAllBySnapLessonQuizIdAndStudentIdOrderByCreatedAtDesc(
                                snapLessonQuizId,
                                studentId
                        );

        if (submissions.isEmpty()) {
            throw new NotFoundException("Bạn chưa nộp bài này");
        }

        LessonQuiz quiz = submissions.get(0).getSnapLessonQuiz().getLessonQuiz();
        List<QuizAttemptResponse> attempts = new ArrayList<>();
        int totalAttempts = submissions.size();
        for (int i = 0; i < totalAttempts; i++) {
            StudentQuizSubmission submission = submissions.get(i);
            List<QuestionResultResponse> questions = submission.getAnswers()
                            .stream()
                            .map(answer -> {
                                QuizQuestion question =
                                        answer.getQuestion();

                                return new QuestionResultResponse(
                                        question.getId(),
                                        question.getContent(),
                                        question.getPoints(),
                                        answer.getCorrect(),

                                        answer.getSelectedOption()
                                                != null
                                                ? answer
                                                .getSelectedOption()
                                                .getId()
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

            attempts.add(
                    new QuizAttemptResponse(
                            submission.getId(),

                            // lần làm
                            totalAttempts - i,

                            submission.getScore(),
                            submission.getPassed(),

                            submission.getStatus()
                                    .name(),

                            submission.getCreatedAt(),

                            questions
                    )
            );
        }

        return new QuizResultResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPassScore(),
                attempts
        );
    }



    @Override
    @Transactional(readOnly = true)
    public Page<QuizListResponse> getMyQuizzes(int page, int size, String title, QuizType quizType) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID teacherId = currentAccount.getTeacher().getId();
        Pageable pageable = PageRequest.of(page, size);
        return lessonQuizRepository
                .getTeacherQuizzes(
                        teacherId,
                        title,
                        quizType,
                        pageable
                );
    }

    @Transactional
    @Override
    public AssignQuizResponse assignQuiz(AssignQuizRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID teacherId = currentAccount.getTeacher().getId();
        SnapLesson snapLesson = snapLessonRepository.findById(request.getSnapLessonId())
                        .orElseThrow(() -> new NotFoundException("Lesson không tồn tại"));
        LessonQuiz lessonQuiz = lessonQuizRepository.findByIdAndTeacherId(request.getLessonQuizId(), teacherId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Quiz không tồn tại hoặc không thuộc về bạn"
                                ));

        // chống assign trùng
        boolean exists = snapLessonQuizRepository.existsBySnapLessonIdAndLessonQuizId(
                                request.getSnapLessonId(),
                                request.getLessonQuizId()
                        );

        if (exists) {
            throw new BadRequestException(
                    "Quiz đã được gán vào lesson"
            );
        }

        SnapLessonQuiz mapping = new SnapLessonQuiz();
        mapping.setSnapLesson(snapLesson);
        mapping.setLessonQuiz(lessonQuiz);
        mapping.setRequired(request.getRequired());
        mapping.setMaxAttempts(request.getMaxAttempts());
        mapping.setDisplayOrder(request.getDisplayOrder());
        snapLessonQuizRepository.save(mapping);
        return new AssignQuizResponse("Gán quiz thành công");
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonAssignedQuizResponse> getAssignedQuizzes(UUID snapLessonId) {
        snapLessonRepository.findById(snapLessonId)
                .orElseThrow(() -> new NotFoundException("Buổi học không tồn tại"));
        return snapLessonQuizRepository.getAssignedQuizzesByLesson(snapLessonId);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDetailResponse getQuizDetail(UUID quizId) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID teacherId = currentAccount.getTeacher().getId();
        LessonQuiz quiz = lessonQuizRepository.findByIdAndTeacherId(quizId, teacherId)
                        .orElseThrow(() ->
                                new NotFoundException("Không tìm thấy quiz")
                        );

        List<QuestionDetailResponse> questions =
                quiz.getQuestions()
                        .stream()
                        .map(question ->
                                new QuestionDetailResponse(
                                        question.getId(),
                                        question.getContent(),
                                        question.getPoints(),
                                        question.getEssayAnswer(),

                                        question.getOptions()
                                                .stream()
                                                .map(option ->
                                                        new OptionDetailResponse(
                                                                option.getId(),
                                                                option.getContent(),
                                                                option.getCorrect()
                                                        )
                                                )
                                                .toList()
                                )
                        )
                        .toList();

        return new QuizDetailResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getPassScore(),
                quiz.getQuizType(),
                questions
        );
    }

    @Transactional
    @Override
    public UpdateAssignedQuizResponse updateAssignedQuiz(UUID snapLessonQuizId, UpdateAssignedQuizRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID teacherId = currentAccount.getTeacher().getId();
        SnapLessonQuiz mapping = snapLessonQuizRepository.findById(snapLessonQuizId)
                        .orElseThrow(() ->
                                new NotFoundException("Quiz gán không tồn tại"));
        // check quiz thuộc teacher
        LessonQuiz lessonQuiz =
                lessonQuizRepository
                        .findByIdAndTeacherId(
                                request.getLessonQuizId(),
                                teacherId
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Quiz không tồn tại hoặc không thuộc về bạn"
                                ));

        // chống duplicate
        boolean exists =
                snapLessonQuizRepository
                        .existsBySnapLessonIdAndLessonQuizIdAndIdNot(
                                mapping
                                        .getSnapLesson()
                                        .getId(),
                                request
                                        .getLessonQuizId(),
                                snapLessonQuizId
                        );

        if (exists) {
            throw new BadRequestException("Quiz đã được gán vào lesson");
        }
        mapping.setLessonQuiz(lessonQuiz);
        if (request.getRequired() != null) {
            mapping.setRequired(request.getRequired());
        }
        if (request.getMaxAttempts() != null) {
            mapping.setMaxAttempts(request.getMaxAttempts());
        }
        mapping.setDisplayOrder(request.getDisplayOrder());
        snapLessonQuizRepository.save(mapping);
        return new UpdateAssignedQuizResponse("Cập nhật thành công!");
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

package phucitdev.course.modules.lesson_quiz.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.commo.utils.LessonQuizCodeGenerator;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.entity.Role;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.lesson_quiz.dto.*;
import phucitdev.course.modules.lesson_quiz.dto.assignQuiz.*;
import phucitdev.course.modules.lesson_quiz.dto.checking_lessonQuizCode.CheckingLessonQuizCodeRequest;
import phucitdev.course.modules.lesson_quiz.dto.checking_lessonQuizCode.CheckingLessonQuizCodeResponse;
import phucitdev.course.modules.lesson_quiz.dto.lesson_quiz.UpdateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.lesson_quiz.UpdateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.quiz_update.QuestionUpdateRequest;
import phucitdev.course.modules.lesson_quiz.dto.quiz_update.UpdateQuizQuestionRequest;
import phucitdev.course.modules.lesson_quiz.dto.quiz_update.UpdateQuizQuestionResponse;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Autowired
    LessonQuizCodeGenerator lessonQuizCodeGenerator;
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
        lessonQuiz.setLessonQuizCode(lessonQuizCodeGenerator.generateUniqueCode());
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
        Account account = SecurityUtils.getCurrentAccount();
        boolean isTeacher = account.getRole() == Role.TEACHER;
        LessonQuiz quiz = lessonQuizRepository.findById(quizId).orElseThrow(() ->
                                new NotFoundException("Quiz không tồn tại")
                        );
        return new GetLessonQuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getLessonQuizCode(),
                quiz.getDurationMinutes(),
                quiz.getPassScore(),
                quiz.getQuizType().name(),
                quiz.getVersion(),
                quiz.getQuestions()
                        .stream()
                        .map(question -> new QuestionResponse(
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
                                                                isTeacher ? option.getCorrect() : false
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
                        .orElseThrow(() -> new NotFoundException("Quiz không tồn tại"));
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
            return new SubmitQuizResponse("Bạn chưa trả lời câu hỏi nào", 0, false);
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
                quiz.getVersion(),
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

    @Override
    @Transactional
    public UpdateLessonQuizResponse updateLessonQuiz(UUID lessonQuizId, UpdateLessonQuizRequest request) {
        LessonQuiz lessonQuiz = lessonQuizRepository
                .findById(lessonQuizId).orElseThrow(() -> new NotFoundException("lessonQuiz không tồn tại!"));
        lessonQuiz.setTitle(request.getTitle());
        lessonQuiz.setDescription(request.getDescription());
        lessonQuiz.setDurationMinutes(request.getDurationMinutes());
        lessonQuiz.setPassScore(request.getPassScore());
        return new UpdateLessonQuizResponse("Cập nhật thành công!");
    }

    @Override
    public CheckingLessonQuizCodeResponse checkLesonQuizCode(UUID quizId, CheckingLessonQuizCodeRequest request) {
        LessonQuiz lessonQuiz = lessonQuizRepository
                .findById(quizId).orElseThrow(() -> new NotFoundException("lessonQuiz không tồn tại!"));
        boolean isValid = lessonQuiz.getLessonQuizCode()
                .equalsIgnoreCase(request.getLessonQuizCode().trim());
        if (!isValid) {
            throw new BadRequestException("Mã quiz không chính xác!");
        }
        return new CheckingLessonQuizCodeResponse(
                true,
                "Xác thực mã quiz thành công!"
        );
    }

    @Override
    @Transactional
    public UpdateQuizQuestionResponse updateQuestions(UUID quizId, UpdateQuizQuestionRequest request) {
        System.out.println("service start");
        LessonQuiz quiz = lessonQuizRepository.findDetail(quizId).orElseThrow(() ->
                        new NotFoundException("Quiz không tồn tại"));
        System.out.println("after find quiz");
        boolean hasSubmission = studentQuizSubmissionRepository.existsBySnapLessonQuiz_LessonQuiz_Id(quizId);
        System.out.println("hasSubmission = " + hasSubmission);
        if (!hasSubmission) {
            System.out.println("Submit nè");
            updateQuestionSet(quiz, request);
        }else {
            LessonQuiz newQuiz = cloneQuiz(quiz);
            createQuestionSet(newQuiz, request);
        }
        return new UpdateQuizQuestionResponse("Cập nhật thành công!");
    }
    private LessonQuiz cloneQuiz(LessonQuiz oldQuiz) {
        LessonQuiz newQuiz = new LessonQuiz();

        newQuiz.setTitle(oldQuiz.getTitle());
        newQuiz.setDescription(oldQuiz.getDescription());
        newQuiz.setDurationMinutes(oldQuiz.getDurationMinutes());
        newQuiz.setPassScore(oldQuiz.getPassScore());
        newQuiz.setLessonQuizCode(lessonQuizCodeGenerator.generateUniqueCode());
        newQuiz.setQuizType(oldQuiz.getQuizType());
        newQuiz.setTeacher(oldQuiz.getTeacher());
        Integer version = oldQuiz.getVersion() == null ? 1 : oldQuiz.getVersion() + 1;
        newQuiz.setVersion(version);
        return lessonQuizRepository.save(newQuiz);
    }
    private void updateQuestionSet(LessonQuiz quiz, UpdateQuizQuestionRequest request) {
        List<QuizQuestion> questions = quiz.getQuestions();
        Map<UUID, QuizQuestion> questionMap =
                quiz.getQuestions()
                        .stream()
                        .collect(Collectors.toMap(
                                QuizQuestion::getId,
                                Function.identity(),
                                (oldValue, newValue) -> oldValue
                        ));
        for (QuestionUpdateRequest q : request.getQuestions()) {
            if (q.getId() == null) {
                QuizQuestion newQuestion = new QuizQuestion();
                newQuestion.setQuiz(quiz);
                newQuestion.setContent(q.getContent());
                newQuestion.setPoints(q.getPoints());
                newQuestion.setEssayAnswer(q.getEssayAnswer());
                newQuestion = quizQuestionRepository.save(newQuestion);
                if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                    saveOptions(newQuestion, q);
                }

                continue;
            }
            QuizQuestion existingQuestion = questionMap.get(q.getId());
            System.out.println("existing question = " + existingQuestion);
            if (existingQuestion == null) {
                throw new NotFoundException(
                        "Question không tồn tại: " + q.getId()
                );
            }
            existingQuestion.setContent(q.getContent());
            existingQuestion.setPoints(q.getPoints());
            existingQuestion.setEssayAnswer(q.getEssayAnswer());

            quizQuestionRepository.save(existingQuestion);
            if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                updateOptions(existingQuestion, q);
            }
        }
    }
    private void createQuestionSet(
            LessonQuiz quiz,
            UpdateQuizQuestionRequest request
    ) {
        for (QuestionUpdateRequest q : request.getQuestions()) {
            QuizQuestion question = new QuizQuestion();
            question.setQuiz(quiz);
            question.setContent(q.getContent());
            question.setPoints(q.getPoints());
            question.setEssayAnswer(q.getEssayAnswer());

            question = quizQuestionRepository.save(question);

            if (quiz.getQuizType() == QuizType.MULTIPLE_CHOICE) {
                saveOptions(question, q);
            }
        }
    }
    private void saveOptions(QuizQuestion question, QuestionUpdateRequest questionRequest) {
        if (questionRequest.getOptions() == null) {
            return;
        }

        for (var optionRequest : questionRequest.getOptions()) {
            QuestionOption option = new QuestionOption();
            option.setQuestion(question);
            option.setContent(optionRequest.getContent());
            option.setCorrect(optionRequest.getCorrect());

            questionOptionRepository.save(option);
        }
    }
    private void updateOptions(QuizQuestion question, QuestionUpdateRequest request) {
        if (request.getOptions() == null) {
            return;
        }
        Map<UUID, QuestionOption> optionMap = question.getOptions()
                        .stream()
                        .collect(Collectors.toMap(
                                QuestionOption::getId,
                                Function.identity()
                        ));
        System.out.println("question id = " + question.getId());
        System.out.println("option size = " + question.getOptions().size());
        for (var optionRequest : request.getOptions()) {
            if (optionRequest.getId() == null) {
                QuestionOption option = new QuestionOption();
                option.setQuestion(question);
                option.setContent(optionRequest.getContent());
                option.setCorrect(optionRequest.getCorrect());
                questionOptionRepository.save(option);
                continue;
            }
            QuestionOption existingOption = optionMap.get(optionRequest.getId());
            if (existingOption == null) {
                throw new NotFoundException("Option không tồn tại");
            }
            existingOption.setContent(optionRequest.getContent());
            existingOption.setCorrect(optionRequest.getCorrect());
            questionOptionRepository.save(existingOption);
        }
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

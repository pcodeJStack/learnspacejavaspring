package phucitdev.course.modules.lesson_quiz.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizRequest;
import phucitdev.course.modules.lesson_quiz.dto.CreateLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.LessonQuiz;
import phucitdev.course.modules.lesson_quiz.repository.LessonQuizRepository;
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
    @Override
    public CreateLessonQuizResponse createQuiz(CreateLessonQuizRequest request) {
        SnapLesson snapLesson = snapLessonRepository.findById(request.getSnapLessonId())
                        .orElseThrow(() -> new NotFoundException("SnapLesson không tồn tại"));
        LessonQuiz lessonQuiz = new LessonQuiz();
        lessonQuiz.setTitle(request.getTitle());
        lessonQuiz.setDescription(request.getDescription());
        lessonQuiz.setDurationMinutes(request.getDurationMinutes());
        lessonQuiz.setPassScore(request.getPassScore());
        lessonQuiz.setSnapLesson(snapLesson);
        lessonQuizRepository.save(lessonQuiz);
        return new CreateLessonQuizResponse("Tạo quiz thành công");
    }

    @Override
    public List<GetLessonQuizResponse> getQuizzes(UUID snapLessonId) {
        snapLessonRepository.findById(snapLessonId).orElseThrow(() ->
                new NotFoundException("SnapLesson không tồn tại")
        );
        return lessonQuizRepository.findQuizBySnapLessonId(snapLessonId);
    }
}

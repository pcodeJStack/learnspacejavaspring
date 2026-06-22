package phucitdev.course.modules.snap_lessonquiz.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.lesson_quiz.entity.StudentQuizSubmission;
import phucitdev.course.modules.lesson_quiz.repository.StudentQuizSubmissionRepository;
import phucitdev.course.modules.snap_lessonquiz.dto.DeleteSnapLessonQuizResponse;
import phucitdev.course.modules.snap_lessonquiz.entity.SnapLessonQuiz;
import phucitdev.course.modules.snap_lessonquiz.repository.SnapLessonQuizRepository;
import phucitdev.course.modules.snap_lessonquiz.service.SnapLessonQuizService;
import java.util.UUID;
@Service
public class SnapLessonQuizServiceImpl implements SnapLessonQuizService {
     @Autowired
    SnapLessonQuizRepository  snapLessonQuizRepository;
     @Autowired
     StudentQuizSubmissionRepository studentQuizSubmissionRepository;
    @Override
    public DeleteSnapLessonQuizResponse deleteSnapLessonQuiz(UUID snapLessonQuizId) {
      SnapLessonQuiz snapLessonQuiz = snapLessonQuizRepository.findSnapLessonQuizById(snapLessonQuizId)
                .orElseThrow(() -> new NotFoundException("SnapLessonQuiz không tồn tại"));
        boolean hasSubmission = studentQuizSubmissionRepository.existsBySnapLessonQuizId(snapLessonQuizId);
        if (hasSubmission) {
            throw new BadRequestException("Không thể gỡ vì đã có học viên nộp bài");
        }
        snapLessonQuizRepository.delete(snapLessonQuiz);
        return new DeleteSnapLessonQuizResponse("SnapLessonQuiz đã bị gỡ!");
    }
}

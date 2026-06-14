package phucitdev.course.modules.lesson_quiz.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse;
import phucitdev.course.modules.lesson_quiz.entity.LessonQuiz;
import java.util.List;
import java.util.UUID;

public interface LessonQuizRepository extends JpaRepository<LessonQuiz, UUID> {
//    @Query("""
//    SELECT new phucitdev.course.modules.lesson_quiz.dto.GetLessonQuizResponse(
//        q.id,
//        q.title,
//        q.description,
//        q.durationMinutes,
//        q.passScore
//    )
//    FROM LessonQuiz q
//    WHERE q.snapLesson.id = :snapLessonId
//""")
//    List<GetLessonQuizResponse> findQuizBySnapLessonId(
//            UUID snapLessonId
//    );
    List<LessonQuiz> findBySnapLessonId(UUID snapLessonId);
}
package phucitdev.course.modules.lessonVideo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phucitdev.course.modules.lessonVideo.dto.GetLessonVideoResponse;
import phucitdev.course.modules.lessonVideo.entity.LessonVideo;
import phucitdev.course.modules.lessonVideo.entity.VideoType;

import java.util.List;
import java.util.UUID;

public interface LessonVideoRepository extends JpaRepository<LessonVideo, UUID> {
//    @Query("""
//        SELECT new phucitdev.course.modules.lessonVideo.dto.GetLessonVideoResponse(
//            lv.id,
//            lv.title,
//            lv.videoUrl,
//            lv.videoType,
//            lv.createdAt
//        )
//        FROM LessonVideo lv
//        WHERE lv.snapLesson.id = :snapLessonId
//        ORDER BY lv.createdAt DESC
//    """)
//    List<GetLessonVideoResponse> findBySnapLessonId(
//            @Param("snapLessonId") UUID snapLessonId
//    );
    List<LessonVideo> findBySnapLessonId(UUID snapLessonId);
    List<LessonVideo> findBySnapLessonIdAndVideoType(UUID snapLessonId, VideoType videoType);
//    @Query("""
//    SELECT new phucitdev.course.modules.lessonVideo.dto.GetLessonVideoResponse(
//        lv.id,
//        lv.title,
//        lv.videoUrl,
//        lv.videoType,
//        lv.createdAt
//    )
//    FROM LessonVideo lv
//    WHERE lv.snapLesson.id = :snapLessonId
//    AND lv.videoType = :videoType
//    ORDER BY lv.createdAt DESC
//""")
//    List<GetLessonVideoResponse> findBySnapLessonIdAndVideoType(UUID snapLessonId, VideoType videoType);

//    @Query("SELECT lv.fileKey FROM LessonVideo lv WHERE lv.snapLesson.id = :snapLessonId")
//    String findFileKey(@Param("snapLessonId") UUID snapLessonId);

}


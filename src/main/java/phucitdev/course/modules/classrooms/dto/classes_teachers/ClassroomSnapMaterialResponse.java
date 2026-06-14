package phucitdev.course.modules.classrooms.dto.classes_teachers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomSnapMaterialResponse {
    private UUID materialId;
    private UUID sourceMaterialId;
    private Integer materialOrder;
    private String title;
    private String description;

    private List<SnapLessonResponse> lessons;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnapLessonResponse {
        private UUID lessonId;
        private UUID sourceLessonId;
        private Integer lessonOrder;
        private String title;
        // thêm lesson videos
        private List<LessonVideoResponse> lessonVideos;
        // lesson quizzes
        private List<LessonQuizResponse> lessonQuizzes;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonVideoResponse {
        private UUID lessonVideoId;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonQuizResponse {

        private UUID quizId;
        private String title;
        private String description;
        private Integer durationMinutes;
        private Integer passScore;
    }
}
package phucitdev.course.modules.lessonVideo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phucitdev.course.commo.base.BaseEntity;
import phucitdev.course.modules.lessons.entity.Lesson;
import phucitdev.course.modules.snap_lesson.entity.SnapLesson;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LessonVideo extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String videoUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snap_lesson_id", nullable = false)
    private SnapLesson snapLesson;
    private String fileKey;
    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private VideoType videoType;
}

package phucitdev.course.modules.snap_lesson.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phucitdev.course.commo.base.BaseEntity;
import phucitdev.course.modules.lessonResource.entity.LessonResource;
import phucitdev.course.modules.lessonVideo.entity.LessonVideo;
import phucitdev.course.modules.lesson_quiz.entity.LessonQuiz;
import phucitdev.course.modules.snap_classroommaterial.entity.SnapClassroomMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "snap_lessons")
public class SnapLesson extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "snap_classroom_material_id",
            nullable = false
    )
    private SnapClassroomMaterial snapClassroomMaterial;
    // lesson gốc
    @Column(nullable = false)
    private UUID sourceLessonId;
    @Column(nullable = false)
    private String title;
    private Integer lessonOrder;

    @OneToMany(mappedBy = "snapLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonVideo> lessonVideos = new ArrayList<>();

    @OneToMany(mappedBy = "snapLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonResource> lessonResources = new ArrayList<>();

    @OneToMany(mappedBy = "snapLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonQuiz>  lessonQuizzes = new ArrayList<>();
}



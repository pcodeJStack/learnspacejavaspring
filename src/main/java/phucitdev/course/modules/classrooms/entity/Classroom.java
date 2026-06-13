package phucitdev.course.modules.classrooms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phucitdev.course.commo.base.BaseEntity;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.classSchedule.entity.ClassSchedule;
import phucitdev.course.modules.classroomMaterial.entity.ClassroomMaterial;
import phucitdev.course.modules.lessons.entity.Lesson;
import phucitdev.course.modules.material.entity.Material;
import phucitdev.course.modules.snap_classroommaterial.entity.SnapClassroomMaterial;
import phucitdev.course.modules.studentProfile.entity.StudentProfile;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Classroom extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(unique = true, nullable = false)
    private String code;
    @Enumerated(EnumType.STRING)
    private ClassroomStatus status;
    private Integer totalStudent = 0;
    // Ngày bắt đầu lớp học
    @Column(nullable = false)
    private LocalDate startDate;

    // Ngày kết thúc lớp học
    @Column(nullable = false)
    private LocalDate endDate;


    @OneToMany(mappedBy = "classroom")
    private List<ClassSchedule>  classSchedules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherProfile teacherProfile;


    @ManyToMany
    @JoinTable(
            name = "classroom_students",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "studentProfile_id")
    )
    private List<StudentProfile> studentProfiles;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SnapClassroomMaterial> snapClassroomMaterials = new ArrayList<>();


}

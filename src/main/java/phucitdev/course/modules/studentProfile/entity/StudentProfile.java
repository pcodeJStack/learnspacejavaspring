package phucitdev.course.modules.studentProfile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phucitdev.course.commo.base.BaseEntity;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.classrooms.entity.Classroom;
import phucitdev.course.modules.lesson_quiz.entity.StudentQuizSubmission;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true)
    private Account account;
    private String address;
    private String avatar;
    @ManyToMany(mappedBy = "studentProfiles")
    private List<Classroom> classrooms;


    @OneToMany(mappedBy = "student")
    private List<StudentQuizSubmission> submissions = new ArrayList<>();
}

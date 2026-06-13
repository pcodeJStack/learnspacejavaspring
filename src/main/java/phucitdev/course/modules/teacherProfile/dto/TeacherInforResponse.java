package phucitdev.course.modules.teacherProfile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeacherInforResponse {
    private String id;
    private String fullName;
    private String email;
    private String specialization;
    private Integer yearsExperience;
    private String avatar;
}
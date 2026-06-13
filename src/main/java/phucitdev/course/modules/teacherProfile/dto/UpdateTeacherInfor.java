package phucitdev.course.modules.teacherProfile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeacherInfor {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    private String specialization;
    private Integer yearsExperience;
    private String avatar;
}
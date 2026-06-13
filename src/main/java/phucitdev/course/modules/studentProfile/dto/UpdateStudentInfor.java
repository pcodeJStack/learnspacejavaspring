package phucitdev.course.modules.studentProfile.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UpdateStudentInfor {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    private String address;
    private String avatar;
}
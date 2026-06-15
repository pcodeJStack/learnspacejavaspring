package phucitdev.course.modules.auth.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Mật khẩu mới không được để trống!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm in hoa, số và ký tự đặc biệt"
    )
     private String newPassword;
    @NotBlank(message = "Confirm mật khẩu không được để trống!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm in hoa, số và ký tự đặc biệt"
    )
     private String confirmPassword;
}

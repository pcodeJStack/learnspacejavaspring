package phucitdev.course.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phucitdev.course.modules.auth.entity.Role;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private UUID id;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isActive;
}

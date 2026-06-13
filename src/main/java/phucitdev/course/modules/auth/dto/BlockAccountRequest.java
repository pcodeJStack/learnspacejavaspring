package phucitdev.course.modules.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlockAccountRequest {
    @NotNull(message = "Type không được để trống")
    private AccountActionType type;
}
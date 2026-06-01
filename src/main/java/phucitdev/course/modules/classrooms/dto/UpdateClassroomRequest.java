package phucitdev.course.modules.classrooms.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phucitdev.course.modules.classrooms.entity.ClassroomStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClassroomRequest {
    @NotBlank(message = "Tên lớp không được để trống")
    private String name;
    private String description;
    @NotNull(message = "Trạng thái không được để trống")
    private ClassroomStatus status;
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;
}

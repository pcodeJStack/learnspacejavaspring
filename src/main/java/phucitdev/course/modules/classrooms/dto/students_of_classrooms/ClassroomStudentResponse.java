package phucitdev.course.modules.classrooms.dto.students_of_classrooms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phucitdev.course.modules.auth.entity.Role;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomStudentResponse {
    private UUID studentId;
    private UUID accountId;
    private String fullName;
    private Role role;
    private Boolean isActive;
    private String email;
    private String avatar;
    private String address;
}
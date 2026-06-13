package phucitdev.course.modules.studentProfile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentInforResponse {
    private String id;
    private String fullName;
    private String email;
    private String address;
    private String avatar;
}
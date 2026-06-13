package phucitdev.course.modules.studentProfile.service;

import phucitdev.course.modules.studentProfile.dto.StudentInforResponse;
import phucitdev.course.modules.studentProfile.dto.UpdateStudentInfor;
import phucitdev.course.modules.studentProfile.dto.UpdateStudentInforResponse;

import java.util.UUID;

public interface StudentProfileService {
    UpdateStudentInforResponse updateStudentInfor(UpdateStudentInfor request);
    StudentInforResponse getStudentInfor();
}

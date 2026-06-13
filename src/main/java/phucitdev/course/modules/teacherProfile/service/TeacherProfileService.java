package phucitdev.course.modules.teacherProfile.service;

import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.classrooms.entity.ClassroomStatus;
import phucitdev.course.modules.teacherProfile.dto.*;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;

import java.util.Optional;
import java.util.UUID;

public interface TeacherProfileService {
    GetAllTeacherResponse getAllTeacher(Integer page, Integer size, String keyword);
    GetTeacherScheduleResponse getTeacherSchedule(UUID teacherId, Integer page, Integer size, ClassroomStatus status);
    TeacherInforResponse getTeacherInfor();
    UpdateTeacherInforResponse updateTeacherInfor(
            UpdateTeacherInfor request
    );
}

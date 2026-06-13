package phucitdev.course.modules.teacherProfile.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.classrooms.entity.ClassroomStatus;
import phucitdev.course.modules.teacherProfile.dto.GetAllTeacherResponse;
import phucitdev.course.modules.teacherProfile.dto.GetTeacherScheduleResponse;
import phucitdev.course.modules.teacherProfile.dto.UpdateTeacherInfor;
import phucitdev.course.modules.teacherProfile.service.TeacherProfileService;

import java.util.UUID;

@CrossOrigin("*")
@RequestMapping("/api/")
@RestController
@SecurityRequirement(name = "api")
public class TeacherProfileAPI {
    @Autowired
    TeacherProfileService teacherProfileService;
    @GetMapping("teacher")
    public ResponseEntity<?> getTeacherInfor() {
        return ResponseEntity.ok(teacherProfileService.getTeacherInfor());
    }
    @PutMapping("teacher")
    public ResponseEntity<?> updateTeacherInfor(@Valid @RequestBody UpdateTeacherInfor request) {
        return ResponseEntity.ok(teacherProfileService.updateTeacherInfor(request)
        );
    }
    @GetMapping("teachers")
    public ResponseEntity<GetAllTeacherResponse> getAllTeacher(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword
    ) {
        GetAllTeacherResponse response = teacherProfileService.getAllTeacher(page, size, keyword);
        return ResponseEntity.ok(response);
    }
    @GetMapping("teachers/{teacherId}/schedule")
    public ResponseEntity<GetTeacherScheduleResponse> getTeacherSchedule(
            @PathVariable UUID teacherId,
            @RequestParam(defaultValue = "0")
            Integer page,
            @RequestParam(defaultValue = "10")
            Integer size,
            @RequestParam(required = false)
            ClassroomStatus status
    ) {

        GetTeacherScheduleResponse response = teacherProfileService.getTeacherSchedule(
                                teacherId,
                                page,
                                size,
                                status
                        );
        return ResponseEntity.ok(response);
    }
}

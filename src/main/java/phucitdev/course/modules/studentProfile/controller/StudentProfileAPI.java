package phucitdev.course.modules.studentProfile.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.studentProfile.dto.UpdateStudentInfor;
import phucitdev.course.modules.studentProfile.service.StudentProfileService;

import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@SecurityRequirement(name = "api")
public class StudentProfileAPI {
    @Autowired
    StudentProfileService  studentProfileService;
    @GetMapping("")
    public ResponseEntity<?> getStudentInfor() {
        return ResponseEntity.ok(studentProfileService.getStudentInfor());
    }
    @PutMapping("")
    public ResponseEntity<?> updateStudentInfor( @Valid @RequestBody UpdateStudentInfor request) {
        return ResponseEntity.ok(
                studentProfileService.updateStudentInfor(request)
        );
    }
}

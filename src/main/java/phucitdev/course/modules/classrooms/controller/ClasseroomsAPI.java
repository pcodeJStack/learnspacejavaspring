package phucitdev.course.modules.classrooms.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phucitdev.course.modules.classrooms.dto.*;
import phucitdev.course.modules.classrooms.service.ClassroomService;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RequestMapping("/api/")
@RestController
@SecurityRequirement(name = "api")
public class ClasseroomsAPI {
    @Autowired
    ClassroomService classService;
    @PostMapping("class")
    public ResponseEntity<?> createNewClass(@Valid @RequestBody CreateClassroomRequest createClassRequest) {
        CreateClassroomResponse createClassResponse = classService.createNewClass(createClassRequest);
        return ResponseEntity.ok().body(createClassResponse);
    }
    @GetMapping("classes")
    public ResponseEntity<?> getClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        return ResponseEntity.ok(
                classService.getClasses(pageable,code,name)
        );
    }
    @PostMapping("enrolling-classroom")
    public ResponseEntity<?> enrollClassroom(@Valid @RequestBody EnrollingClassroomRequest enrollingClassroomRequest) {
        EnrollingClassroomResponse response = classService.enrollingClassroom(enrollingClassroomRequest);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("my-classes")
    public ResponseEntity<?> getMyClasses(){
        List<ClassResponse> response = classService.getMyClasses();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("class/{classroomId}")
    public ResponseEntity<?> updateClassroom(@Valid @RequestBody UpdateClassroomRequest updateClassroomRequest, @PathVariable UUID classroomId) {
        UpdateClassroomResponse response = classService.updateClassroom(updateClassroomRequest, classroomId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("class/{classroomId}")
    public ResponseEntity<?> deleteClassroom(@PathVariable UUID classroomId) {
        DeleteClassroomResponse response = classService.deleteClassroom(classroomId);
        return ResponseEntity.ok().body(response);
    }
}

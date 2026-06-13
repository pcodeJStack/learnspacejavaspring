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
import phucitdev.course.modules.classrooms.dto.assign.AssignMaterialsRequest;
import phucitdev.course.modules.classrooms.dto.assign.AssignMaterialsResponse;
import phucitdev.course.modules.classrooms.dto.assign_teacher.*;
import phucitdev.course.modules.classrooms.service.ClassroomService;
import java.util.List;
import java.util.UUID;
@CrossOrigin("*")
@RequestMapping("/api/")
@RestController
@SecurityRequirement(name = "api")
public class ClassroomsAPI {
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
    @PostMapping("/{classroomId}/materials/assign")
    public ResponseEntity<AssignMaterialsResponse> assignMaterialsToClassroom(
            @PathVariable UUID classroomId,
            @Valid @RequestBody
            AssignMaterialsRequest request
    ) {
        return ResponseEntity.ok(
                classService.assignMaterialsToClassroom(classroomId, request)
        );
    }
    @PostMapping("class/{classroomId}/assign-teacher")
    public ResponseEntity<?> assignTeacherToClassroom(
            @PathVariable UUID classroomId,
            @Valid @RequestBody AssignTeacherToClassroomRequest request) {
        AssignTeacherToClassroomResponse response = classService.assignTeacherToClassroom(classroomId, request);
        return ResponseEntity.ok().body(response);

    }
    @GetMapping("classrooms-with-teacher")
    public ResponseEntity<?> getAllClassroomsWithTeacher(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            Boolean hasTeacher) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(classService.getAllClassroomsWithTeacher(pageable, hasTeacher)
        );
    }
    @PutMapping("class/{classroomId}/edit-teacher")
    public ResponseEntity<?> updateTeacherClassroom(
            @PathVariable UUID classroomId,
            @Valid @RequestBody
            UpdateTeacherClassroomRequest request
    ) {
        UpdateTeacherClassroomResponse response = classService.updateTeacherClassroom(classroomId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("class/{classroomId}/remove-teacher")
    public ResponseEntity<?> removeTeacherFromClassroom(
            @PathVariable UUID classroomId
    ) {

        RemoveTeacherFromClassroomResponse response =
                classService.removeTeacherFromClassroom(
                        classroomId
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("teacher/classrooms")
    public ResponseEntity<?> getClassroomsByTeacher() {

        return ResponseEntity.ok(
                classService.getClassroomsByTeacher()
        );
    }
    @GetMapping("class/{classroomId}/snap-materials")
    public ResponseEntity<?> getSnapMaterialsByClassroom(
            @PathVariable UUID classroomId
    ) {

        return ResponseEntity.ok(classService.getSnapMaterialsByClassroom(classroomId)
        );
    }
    @GetMapping("class/{classroomId}/students")
    public ResponseEntity<?> getStudentsByClassroom(
            @PathVariable UUID classroomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return ResponseEntity.ok(
                classService.getStudentsByClassroom(
                        classroomId,
                        pageable,
                        keyword
                )
        );
    }
}

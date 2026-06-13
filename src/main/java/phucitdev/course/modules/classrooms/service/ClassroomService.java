package phucitdev.course.modules.classrooms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import phucitdev.course.modules.classrooms.dto.*;
import phucitdev.course.modules.classrooms.dto.assign.AssignMaterialsRequest;
import phucitdev.course.modules.classrooms.dto.assign.AssignMaterialsResponse;
import phucitdev.course.modules.classrooms.dto.assign_teacher.*;
import phucitdev.course.modules.classrooms.dto.classes_teachers.ClassroomSnapMaterialResponse;
import phucitdev.course.modules.classrooms.dto.classes_teachers.GetClassroomWithTeacherResponse;
import phucitdev.course.modules.classrooms.dto.students_of_classrooms.ClassroomStudentResponse;
import phucitdev.course.modules.classrooms.dto.teacher_response.TeacherClassroomResponse;

import java.util.List;
import java.util.UUID;

public interface ClassroomService {
    CreateClassroomResponse createNewClass(CreateClassroomRequest createClassRequest);
    GetClassesResponse getClasses(Pageable pageable, String code, String name);
    EnrollingClassroomResponse enrollingClassroom(EnrollingClassroomRequest enrollingClassroomRequest);
    List<ClassResponse> getMyClasses();
    UpdateClassroomResponse updateClassroom(UpdateClassroomRequest updateClassroomRequest, UUID classroomId);
    DeleteClassroomResponse deleteClassroom(UUID classroomId);
    AssignMaterialsResponse assignMaterialsToClassroom(UUID classroomId, AssignMaterialsRequest request);
    AssignTeacherToClassroomResponse assignTeacherToClassroom(UUID classroomId, AssignTeacherToClassroomRequest request);
    Page<GetClassroomWithTeacherResponse> getAllClassroomsWithTeacher(Pageable pageable, Boolean hasTeacher);
    UpdateTeacherClassroomResponse updateTeacherClassroom(UUID classroomId, UpdateTeacherClassroomRequest request);
    RemoveTeacherFromClassroomResponse removeTeacherFromClassroom(UUID classroomId);
    List<TeacherClassroomResponse> getClassroomsByTeacher();
    List<ClassroomSnapMaterialResponse> getSnapMaterialsByClassroom(UUID classroomId);
    Page<ClassroomStudentResponse> getStudentsByClassroom(UUID classroomId, Pageable pageable, String keyword);
}

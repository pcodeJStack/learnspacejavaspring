package phucitdev.course.modules.classrooms.service;

import org.springframework.data.domain.Pageable;
import phucitdev.course.modules.classrooms.dto.*;

import java.util.List;
import java.util.UUID;

public interface ClassroomService {
    CreateClassroomResponse createNewClass(CreateClassroomRequest createClassRequest);
    GetClassesResponse getClasses(Pageable pageable, String code, String name);
    EnrollingClassroomResponse enrollingClassroom(EnrollingClassroomRequest enrollingClassroomRequest);
    List<ClassResponse> getMyClasses();
    UpdateClassroomResponse updateClassroom(UpdateClassroomRequest updateClassroomRequest, UUID classroomId);
    DeleteClassroomResponse deleteClassroom(UUID classroomId);
}

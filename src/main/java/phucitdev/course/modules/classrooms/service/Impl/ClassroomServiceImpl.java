package phucitdev.course.modules.classrooms.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import phucitdev.course.commo.exception.auth.AlreadyExistsException;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.auth.ForbiddenException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.commo.exception.material.DuplicationException;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.entity.Role;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.classSchedule.repository.ClassScheduleRepository;
import phucitdev.course.modules.classrooms.dto.*;
import phucitdev.course.modules.classrooms.entity.Classroom;
import phucitdev.course.modules.classrooms.entity.ClassroomStatus;
import phucitdev.course.modules.classrooms.repository.ClassroomRepository;
import phucitdev.course.modules.classrooms.service.ClassroomService;
import phucitdev.course.modules.studentProfile.entity.ClassroomStudent;
import phucitdev.course.modules.studentProfile.entity.EnrollmentStatus;
import phucitdev.course.modules.studentProfile.entity.StudentProfile;
import phucitdev.course.modules.studentProfile.repository.ClassroomStudentRepository;
import phucitdev.course.modules.studentProfile.repository.StudentProfileRepository;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;
import phucitdev.course.modules.teacherProfile.repository.TeacherProfileRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    ClassroomRepository classroomRepository;
    @Autowired
    TeacherProfileRepository teacherProfileRepository;
    @Autowired
    StudentProfileRepository studentProfileRepository;
    @Autowired
    ClassroomStudentRepository classroomStudentRepository;
    @Autowired
    ClassScheduleRepository classScheduleRepository;
    @Override
    public CreateClassroomResponse createNewClass(CreateClassroomRequest createClassRequest) {
        if (createClassRequest.getStartDate()
                .isAfter(createClassRequest.getEndDate())) {
            throw new BadRequestException(
                    "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc"
            );
        }
        boolean existed = classroomRepository.existsByNameIgnoreCaseAndIsDeletedFalse(createClassRequest.getName().trim());
        if (existed) {
            throw new DuplicationException(
                    "Tên lớp học đã tồn tại"
            );
        }
        Classroom classroom = new Classroom();
        classroom.setName(createClassRequest.getName());
        classroom.setDescription(createClassRequest.getDescription());
        classroom.setCode(generateClassCode());
        classroom.setStartDate(createClassRequest.getStartDate());
        classroom.setEndDate(createClassRequest.getEndDate());
        classroom.setStatus(ClassroomStatus.ACTIVE);
        classroom.setTotalStudent(0);
        Account currentAccount = SecurityUtils.getCurrentAccount();
        Role role = currentAccount.getRole();
        if (role != Role.ADMIN
                && role != Role.TEACHER) {
            throw new ForbiddenException(
                    "Bạn không có quyền tạo lớp học"
            );
        }
        TeacherProfile teacherProfile =
                teacherProfileRepository
                        .findByAccountId(currentAccount.getId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Không tìm thấy hồ sơ giáo viên"
                                ));
        classroom.setTeacherProfile(teacherProfile);
        classroomRepository.save(classroom);
        return new CreateClassroomResponse(
                "Tạo lớp học thành công!"
        );
    }

    @Override
    public GetClassesResponse getClasses(Pageable pageable, String code, String name) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID teacherId = null;
        if (currentAccount.getRole() == Role.TEACHER) {
            teacherId = currentAccount.getTeacher().getId();
        }
        Page<ClassResponse> pageData =  classroomRepository.search(teacherId, name, code,
                pageable
        );
        return new GetClassesResponse(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages()
        );
    }

    @Override
    public EnrollingClassroomResponse enrollingClassroom(EnrollingClassroomRequest enrollingClassroomRequest) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        if (currentAccount.getRole() != Role.STUDENT) {
            throw new ForbiddenException(
                    "Chỉ học sinh mới được tham gia lớp"
            );
        }
        Classroom classroom = classroomRepository.findByCode(enrollingClassroomRequest.getCode())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Mã lớp không tồn tại"
                                ));
        StudentProfile studentProfile = studentProfileRepository.findByAccountId(currentAccount.getId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Không tìm thấy hồ sơ học sinh"
                                ));
        boolean alreadyJoined = classroom.getStudentProfiles().stream()
                        .anyMatch(student ->
                                student.getId()
                                        .equals(
                                                studentProfile
                                                        .getId()
                                        )
                        );
        if (alreadyJoined) {
            throw new AlreadyExistsException(
                    "Bạn đã tham gia lớp học này"
            );
        }
        ClassroomStudent classroomStudent = new ClassroomStudent();
        classroomStudent.setClassroom(classroom);
        classroomStudent.setStudentProfile(studentProfile);

        classroomStudent.setJoinedAt(LocalDate.now());

        classroomStudent.setStatus(EnrollmentStatus.ACTIVE);
        classroomStudentRepository.save(classroomStudent);

        classroom.setTotalStudent(classroom.getTotalStudent() + 1);
        classroomRepository.save(classroom);
        return new EnrollingClassroomResponse("Tham gia lớp học thành công!");
    }

    @Override
    public List<ClassResponse> getMyClasses() {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        if (currentAccount.getRole() != Role.STUDENT) {
            throw new ForbiddenException(
                    "Chỉ học viên mới có quyền truy cập"
            );
        }

        StudentProfile student = currentAccount.getStudent();
        if (student == null) {
            throw new NotFoundException(
                    "Không tìm thấy hồ sơ học viên"
            );
        }
        List<ClassResponse> classes =
                classroomRepository.getMyClasses(
                        student.getId()
                );
        for (ClassResponse item : classes) {

            List<ClassScheduleResponse> schedules =
                    classScheduleRepository
                            .findByClassroomId(item.getId())
                            .stream()
                            .map(schedule ->
                                    new ClassScheduleResponse(
                                            schedule.getId(),
                                            schedule.getDayOfWeek().name(),
                                            schedule.getStartTime(),
                                            schedule.getEndTime(),
                                            schedule.getStudyMode(),
                                            schedule.getLocation(),
                                            schedule.getMeetingUrl()
                                    )
                            )
                            .toList();
            item.setSchedules(schedules);
        }
        return classes;
    }

    @Override
    public UpdateClassroomResponse updateClassroom(UpdateClassroomRequest updateClassroomRequest, UUID classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                        .orElseThrow(() ->
                                new NotFoundException("Không tìm thấy lớp học"));
        if (updateClassroomRequest.getStartDate()
                .isAfter(updateClassroomRequest.getEndDate())) {
            throw new BadRequestException(
                    "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc"
            );
        }
        classroom.setName(updateClassroomRequest.getName());
        classroom.setDescription(updateClassroomRequest.getDescription());
        classroom.setStatus(updateClassroomRequest.getStatus());
        classroom.setStartDate(updateClassroomRequest.getStartDate());
        classroom.setEndDate(updateClassroomRequest.getEndDate());
        classroomRepository.save(classroom);
        return new UpdateClassroomResponse("Cập nhật thành công!");
    }

    @Override
    public DeleteClassroomResponse deleteClassroom(UUID classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() ->
                        new NotFoundException("Không tìm thấy lớp học"));
        validateDelete(classroom);
        classroom.setIsDeleted(true);
        classroomRepository.save(classroom);
        return  new DeleteClassroomResponse("Xoá thành công!");
    }
    private void validateDelete(Classroom classroom) {
        if (classroom.getStatus() == ClassroomStatus.ACTIVE) {
            throw new BadRequestException(
                    "Không thể xóa lớp học đang hoạt động"
            );
        }

    }
    private String generateClassCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}

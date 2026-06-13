package phucitdev.course.modules.classrooms.service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import phucitdev.course.modules.classrooms.dto.assign.AssignMaterialsRequest;
import phucitdev.course.modules.classrooms.dto.assign.AssignMaterialsResponse;
import phucitdev.course.modules.classrooms.dto.assign.MaterialAssignmentItem;
import phucitdev.course.modules.classrooms.dto.assign_teacher.*;
import phucitdev.course.modules.classrooms.dto.classes_teachers.ClassroomSnapMaterialResponse;
import phucitdev.course.modules.classrooms.dto.classes_teachers.GetClassroomWithTeacherResponse;
import phucitdev.course.modules.classrooms.dto.students_of_classrooms.ClassroomStudentResponse;
import phucitdev.course.modules.classrooms.dto.teacher_response.TeacherClassroomResponse;
import phucitdev.course.modules.classrooms.entity.Classroom;
import phucitdev.course.modules.classrooms.entity.ClassroomStatus;
import phucitdev.course.modules.classrooms.repository.ClassroomRepository;
import phucitdev.course.modules.classrooms.service.ClassroomService;
import phucitdev.course.modules.lessons.entity.Lesson;
import phucitdev.course.modules.lessons.repository.LessonRepository;
import phucitdev.course.modules.material.entity.Material;
import phucitdev.course.modules.material.repository.MaterialRepository;
import phucitdev.course.modules.snap_classroommaterial.entity.SnapClassroomMaterial;
import phucitdev.course.modules.snap_classroommaterial.repository.SnapClassroomMaterialRepository;
import phucitdev.course.modules.snap_lesson.entity.SnapLesson;
import phucitdev.course.modules.snap_lesson.repository.SnapLessonRepository;
import phucitdev.course.modules.studentProfile.entity.ClassroomStudent;
import phucitdev.course.modules.studentProfile.entity.EnrollmentStatus;
import phucitdev.course.modules.studentProfile.entity.StudentProfile;
import phucitdev.course.modules.studentProfile.repository.ClassroomStudentRepository;
import phucitdev.course.modules.studentProfile.repository.StudentProfileRepository;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;
import phucitdev.course.modules.teacherProfile.repository.TeacherProfileRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    MaterialRepository materialRepository;
    @Autowired
    SnapClassroomMaterialRepository snapClassroomMaterialRepository;
    @Autowired
    SnapLessonRepository snapLessonRepository;
    @Autowired
    LessonRepository lessonRepository;
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
        if (role != Role.ADMIN) {
            throw new ForbiddenException(
                    "Bạn không có quyền tạo lớp học"
            );
        }
        classroom.setTeacherProfile(null);
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

        @Override
        public AssignMaterialsResponse assignMaterialsToClassroom(UUID classroomId, AssignMaterialsRequest request) {
            Classroom classroom = classroomRepository.findByIdAndIsDeletedFalse(classroomId).orElseThrow(() ->
                                    new NotFoundException("Lớp học không tồn tại")
                            );
            Set<Integer> orderSet = new HashSet<>();
            for (MaterialAssignmentItem item : request.getMaterials()) {
                if (!orderSet.add(item.getOrder())) {
                    throw new BadRequestException(
                            "Thứ tự " + item.getOrder()
                                    + " bị trùng trong danh sách gửi lên"
                    );
                }
            }
            List<UUID> materialIds = request.getMaterials()
                            .stream()
                            .map(item -> item.getMaterialId())
                            .toList();

            List<Material> materialList = materialRepository.findAllByIdInAndIsDeletedFalse(materialIds);
            if (materialList.size() != materialIds.size()) {
                Set<UUID> foundIds = materialList.stream()
                                .map(Material::getId)
                                .collect(Collectors.toSet());
                List<UUID> missingIds = materialIds.stream()
                                .filter(id -> !foundIds.contains(id))
                                .toList();
                throw new NotFoundException("Không tìm thấy đề tài: " + missingIds);
            }
            Map<UUID, Material> materialMap =
                    materialList.stream()
                            .collect(Collectors.toMap(
                                    Material::getId,
                                    material -> material
                            ));
            List<SnapClassroomMaterial> snapMaterials = new ArrayList<>();
            for (MaterialAssignmentItem item : request.getMaterials()) {
                Material material = materialMap.get(item.getMaterialId());
                if (material == null) {
                    continue;
                }
                // check đã assign chưa
                boolean existsMaterial = snapClassroomMaterialRepository.existsByClassroomIdAndSourceMaterialIdAndIsDeletedFalse(classroomId, material.getId());
                if (existsMaterial) {
                    throw new BadRequestException("Đề tài '" + material.getTitle() + "' đã được gán vào lớp");
                }
                // check trùng order
                boolean existsOrder = snapClassroomMaterialRepository.existsByClassroomIdAndMaterialOrderAndIsDeletedFalse(classroomId, item.getOrder());
                if (existsOrder) {
                    throw new BadRequestException("Thứ tự " + item.getOrder() + " đã tồn tại");
                }
                SnapClassroomMaterial snap = SnapClassroomMaterial.builder()
                                .classroom(classroom)
                                .sourceMaterialId(material.getId())
                                .materialOrder(item.getOrder())

                                // snapshot
                                .title(material.getTitle())
                                .description(material.getDescription())
                                .build();
                snapMaterials.add(snap);
                SnapClassroomMaterial savedSnap = snapClassroomMaterialRepository.save(snap);
                List<Lesson> lessons = lessonRepository.findAllByMaterialIdAndIsDeletedFalseOrderByLessonOrderAsc(material.getId());
// clone sang snap lesson
                List<SnapLesson> snapLessons =
                        lessons.stream()
                                .map(lesson -> {
                                    SnapLesson snapLesson = new SnapLesson();
                                    snapLesson.setSnapClassroomMaterial(savedSnap);
                                    snapLesson.setSourceLessonId(lesson.getId());
                                    snapLesson.setTitle(lesson.getTitle());
                                    snapLesson.setLessonOrder(lesson.getLessonOrder());
                                    return snapLesson;
                                })
                                .toList();
                // save snap lessons
                snapLessonRepository.saveAll(snapLessons);
            }
            if (snapMaterials.isEmpty()) {
                throw new BadRequestException(
                        "Không có đề tài nào được gán"
                );
            }
            snapClassroomMaterialRepository.saveAll(snapMaterials);
            return new AssignMaterialsResponse("Gán đề tài thành công!");
        }

    @Override
    public AssignTeacherToClassroomResponse assignTeacherToClassroom(UUID classroomId, AssignTeacherToClassroomRequest request) {
        Classroom classroom = classroomRepository
                .findByIdAndIsDeletedFalse(classroomId)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy lớp học"));
        TeacherProfile teacher = teacherProfileRepository.findByIdAndIsDeletedFalse(request.getTeacherId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy giáo viên")
                        );
        if (classroom.getTeacherProfile() != null) {
            // Nếu chính giáo viên đó đã được gán
            if (classroom.getTeacherProfile()
                    .getId()
                    .equals(teacher.getId())) {
                throw new BadRequestException(
                        "Giáo viên đã được gán cho lớp này"
                );
            }
            // Nếu đã có giáo viên khác
            throw new BadRequestException(
                    "Lớp học này đã có giáo viên phụ trách"
            );
        }
        classroom.setTeacherProfile(teacher);
        classroomRepository.save(classroom);
        return new AssignTeacherToClassroomResponse("Gán giáo viên thành công!");
    }

    @Override
    public Page<GetClassroomWithTeacherResponse> getAllClassroomsWithTeacher(Pageable pageable, Boolean hasTeacher) {
        Page<Classroom> classrooms = classroomRepository.findAllClassroomsWithTeacherFilter(hasTeacher, pageable);
        return classrooms.map(classroom -> {
            GetClassroomWithTeacherResponse response =
                    new GetClassroomWithTeacherResponse();

            response.setClassroomId(classroom.getId());
            response.setClassroomName(classroom.getName());
            response.setCode(classroom.getCode());
            response.setDescription(classroom.getDescription());
            response.setStatus(classroom.getStatus().name());
            response.setTotalStudent(classroom.getTotalStudent());
            response.setStartDate(classroom.getStartDate());
            response.setEndDate(classroom.getEndDate());
            // Nếu có giáo viên
            if (classroom.getTeacherProfile() != null) {
                TeacherProfile teacher = classroom.getTeacherProfile();
                Account account = teacher.getAccount();
                response.setTeacher(
                        new GetClassroomWithTeacherResponse
                                .TeacherInfo(
                                teacher.getId(),
                                account.getFullName(),
                                account.getEmail(),
                                teacher.getAvatar(),
                                teacher.getSpecialization(),
                                teacher.getYearsExperience()
                        )
                );
            }

            return response;
        });
    }

    @Override
    public UpdateTeacherClassroomResponse updateTeacherClassroom(UUID classroomId, UpdateTeacherClassroomRequest request) {
        Classroom classroom = classroomRepository.findByIdAndIsDeletedFalse(classroomId).orElseThrow(() ->
                        new NotFoundException("Không tìm thấy lớp học")
                );
        TeacherProfile newTeacher = teacherProfileRepository.findByIdAndIsDeletedFalse(request.getTeacherId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy giáo viên")
                        );
        // lớp chưa có giáo viên
        if (classroom.getTeacherProfile() == null) {
            throw new BadRequestException("Lớp học chưa có giáo viên để cập nhật");
        }
        // teacher mới giống teacher hiện tại
        if (classroom.getTeacherProfile()
                .getId()
                .equals(newTeacher.getId())) {

            throw new BadRequestException(
                    "Giáo viên này đang phụ trách lớp"
            );
        }

        classroom.setTeacherProfile(newTeacher);

        classroomRepository.save(classroom);

        return new UpdateTeacherClassroomResponse(
                "Cập nhật giáo viên thành công!"
        );
    }

    @Override
    public RemoveTeacherFromClassroomResponse removeTeacherFromClassroom(
            UUID classroomId
    ) {

        Classroom classroom = classroomRepository.findByIdAndIsDeletedFalse(classroomId).orElseThrow(() ->
                        new NotFoundException("Không tìm thấy lớp học")
                );
        // chưa có teacher
        if (classroom.getTeacherProfile() == null) {
            throw new BadRequestException(
                    "Lớp học chưa được phân công giáo viên"
            );
        }
        // gỡ teacher khỏi lớp
        classroom.setTeacherProfile(null);
        classroomRepository.save(classroom);

        return new RemoveTeacherFromClassroomResponse(
                "Xoá giáo viên khỏi lớp thành công!"
        );
    }

    @Override
    public List<TeacherClassroomResponse> getClassroomsByTeacher() {
        // check teacher tồn tại
        Account currentAccount = SecurityUtils.getCurrentAccount();
        UUID teacherId = null;
        if (currentAccount.getRole() == Role.TEACHER) {
            teacherId = currentAccount.getTeacher().getId();
        }
        List<Classroom> classrooms = classroomRepository.findByTeacherProfileIdAndIsDeletedFalse(teacherId);
        return classrooms.stream()
                .map(classroom ->
                        new TeacherClassroomResponse(
                                classroom.getId(),
                                classroom.getName(),
                                classroom.getCode(),
                                classroom.getDescription(),
                                classroom.getStatus(),
                                classroom.getTotalStudent(),
                                classroom.getStartDate(),
                                classroom.getEndDate()
                        )
                )
                .toList();
    }

    @Override
    public List<ClassroomSnapMaterialResponse> getSnapMaterialsByClassroom(UUID classroomId) {
        Classroom classroom = classroomRepository
                .findByIdAndIsDeletedFalse(classroomId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Không tìm thấy lớp học"
                        )
                );
        List<SnapClassroomMaterial> materials = snapClassroomMaterialRepository.findByClassroomIdAndIsDeletedFalseOrderByMaterialOrderAsc(classroom.getId());
        return materials.stream()
                .map(material -> {
                    List<ClassroomSnapMaterialResponse.SnapLessonResponse> lessons = material.getSnapLessons()
                                    .stream()
                                    .sorted(
                                            Comparator.comparing(
                                                    SnapLesson::getLessonOrder
                                            )
                                    )
                                    .map(lesson ->
                                            new ClassroomSnapMaterialResponse
                                                    .SnapLessonResponse(
                                                    lesson.getId(),
                                                    lesson.getSourceLessonId(),
                                                    lesson.getLessonOrder(),
                                                    lesson.getTitle(),
                                                    lesson.getLessonVideos()
                                                            .stream()
                                                            .map(video -> new ClassroomSnapMaterialResponse.LessonVideoResponse(
                                                                    video.getId()
                                                            ))
                                                            .toList()
                                            )
                                    )
                                    .toList();

                    return new ClassroomSnapMaterialResponse(
                            material.getId(),
                            material.getSourceMaterialId(),
                            material.getMaterialOrder(),
                            material.getTitle(),
                            material.getDescription(),
                            lessons
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassroomStudentResponse> getStudentsByClassroom(
            UUID classroomId,
            Pageable pageable,
            String keyword
    ) {

        // check classroom tồn tại
        classroomRepository.findById(classroomId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Không tìm thấy lớp học"
                        ));

        Page<StudentProfile> students =
                studentProfileRepository
                        .findStudentsByClassroom(
                                classroomId,
                                keyword,
                                pageable
                        );

        return students.map(student -> {

            Account account = student.getAccount();

            return new ClassroomStudentResponse(
                    student.getId(),
                    account.getId(),
                    account.getFullName(),
                    account.getRole(),
                    account.getIsActive(),
                    account.getEmail(),
                    student.getAvatar(),
                    student.getAddress()
            );
        });
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

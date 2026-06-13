package phucitdev.course.modules.teacherProfile.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.classSchedule.repository.ClassScheduleRepository;
import phucitdev.course.modules.classrooms.entity.ClassroomStatus;
import phucitdev.course.modules.teacherProfile.dto.*;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;
import phucitdev.course.modules.teacherProfile.repository.TeacherProfileRepository;
import phucitdev.course.modules.teacherProfile.service.TeacherProfileService;

import java.util.List;
import java.util.UUID;

@Service
public class TeacherProfileServiceImpl implements TeacherProfileService {
    @Autowired
    private TeacherProfileRepository teacherProfileRepository;
    @Autowired
    private ClassScheduleRepository  classScheduleRepository;
    @Override
    public GetAllTeacherResponse getAllTeacher(Integer page, Integer size, String keyword) {
        Page<TeacherProfile> teacherPage =
                teacherProfileRepository.getAllTeacher(
                        keyword,
                        PageRequest.of(page, size)
                );

        List<TeacherItemResponse> items = teacherPage.getContent()
                        .stream()
                        .map(tp -> new TeacherItemResponse(
                                tp.getId(),
                                tp.getAccount().getFullName(),
                                tp.getAccount().getEmail(),
                                tp.getAvatar(),
                                tp.getSpecialization(),
                                tp.getYearsExperience()
                        ))
                        .toList();

        return new GetAllTeacherResponse(
                items,
                teacherPage.getNumber(),
                teacherPage.getSize(),
                teacherPage.getTotalElements(),
                teacherPage.getTotalPages()
        );
    }

    @Override
    public GetTeacherScheduleResponse getTeacherSchedule(UUID teacherId, Integer page, Integer size, ClassroomStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TeacherScheduleResponse> schedulePage = classScheduleRepository.getTeacherSchedules(teacherId, status, pageable);
        return new GetTeacherScheduleResponse(
                schedulePage.getContent(),
                page,
                size,
                schedulePage.getTotalElements(),
                schedulePage.getTotalPages()
        );
    }

    @Override
    public TeacherInforResponse getTeacherInfor() {
        try {
            Account currentAccount = SecurityUtils.getCurrentAccount();
            TeacherProfile teacherProfile = teacherProfileRepository.findByAccount(currentAccount)
                            .orElseThrow(() ->
                                    new NotFoundException("Không tìm thấy thông tin giáo viên"));
            return new TeacherInforResponse(
                    teacherProfile.getId().toString(),
                    currentAccount.getFullName(),
                    currentAccount.getEmail(),
                    teacherProfile.getSpecialization(),
                    teacherProfile.getYearsExperience(),
                    teacherProfile.getAvatar()
            );
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UpdateTeacherInforResponse updateTeacherInfor(UpdateTeacherInfor request) {
        try {
            Account currentAccount = SecurityUtils.getCurrentAccount();
            TeacherProfile teacherProfile = teacherProfileRepository.findByAccount(currentAccount)
                            .orElseThrow(() ->
                                    new NotFoundException("Không tìm thấy giáo viên"));

            teacherProfile.setSpecialization(request.getSpecialization());
            teacherProfile.setYearsExperience(request.getYearsExperience());
            teacherProfile.setAvatar(request.getAvatar());
            Account account = teacherProfile.getAccount();
            account.setFullName(request.getFullName());
            teacherProfileRepository.save(teacherProfile);
            return new UpdateTeacherInforResponse("Cập nhật thông tin giáo viên thành công!");
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}

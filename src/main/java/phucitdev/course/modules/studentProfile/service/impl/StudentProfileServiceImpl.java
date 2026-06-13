package phucitdev.course.modules.studentProfile.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phucitdev.course.commo.exception.auth.BadRequestException;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.security.SecurityUtils;
import phucitdev.course.modules.studentProfile.dto.StudentInforResponse;
import phucitdev.course.modules.studentProfile.dto.UpdateStudentInfor;
import phucitdev.course.modules.studentProfile.dto.UpdateStudentInforResponse;
import phucitdev.course.modules.studentProfile.entity.StudentProfile;
import phucitdev.course.modules.studentProfile.repository.StudentProfileRepository;
import phucitdev.course.modules.studentProfile.service.StudentProfileService;

import java.util.UUID;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Override
        public UpdateStudentInforResponse updateStudentInfor(UpdateStudentInfor request) {
            try {
                Account currentAccount = SecurityUtils.getCurrentAccount();
                StudentProfile studentProfile =
                        studentProfileRepository.findByAccount(currentAccount)
                                .orElseThrow(() ->
                                        new NotFoundException("Không tìm thấy học viên"));
                studentProfile.setAddress(request.getAddress());
                studentProfile.setAvatar(request.getAvatar());
                Account account = studentProfile.getAccount();
                account.setFullName(request.getFullName());
                studentProfileRepository.save(studentProfile);
                return new UpdateStudentInforResponse("Cập nhật thông tin học viên thành công!");
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }
    @Override
    @Transactional(readOnly = true)
    public StudentInforResponse getStudentInfor() {
        try {
            Account currentAccount = SecurityUtils.getCurrentAccount();
            StudentProfile studentProfile = studentProfileRepository
                            .findByAccount(currentAccount)
                            .orElseThrow(() ->
                                    new NotFoundException("Không tìm thấy thông tin học viên"));
            return new StudentInforResponse(
                    studentProfile.getId().toString(),
                    currentAccount.getFullName(),
                    currentAccount.getEmail(),
                    studentProfile.getAddress(),
                    studentProfile.getAvatar()
            );
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}


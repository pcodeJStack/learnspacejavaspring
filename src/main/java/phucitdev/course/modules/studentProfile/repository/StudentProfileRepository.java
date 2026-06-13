package phucitdev.course.modules.studentProfile.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.studentProfile.entity.StudentProfile;

import java.util.Optional;
import java.util.UUID;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {
    Optional<StudentProfile> findByAccountId(UUID accountId);
    @Query("""
        SELECT sp
        FROM Classroom c
        JOIN c.studentProfiles sp
        JOIN FETCH sp.account a
        WHERE c.id = :classroomId
        AND (
            :keyword IS NULL
            OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<StudentProfile> findStudentsByClassroom(
            @Param("classroomId") UUID classroomId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    Optional<StudentProfile> findByAccount(
            Account account
    );

}

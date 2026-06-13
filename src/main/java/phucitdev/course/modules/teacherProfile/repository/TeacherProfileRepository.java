package phucitdev.course.modules.teacherProfile.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.teacherProfile.entity.TeacherProfile;
import java.util.Optional;
import java.util.UUID;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, UUID> {
    @Query("""
            SELECT tp
            FROM TeacherProfile tp
            JOIN tp.account a
            WHERE tp.isDeleted = false
            AND a.isDeleted = false
            AND (
                :keyword IS NULL
                OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<TeacherProfile> getAllTeacher(String keyword, Pageable pageable);
    Optional<TeacherProfile> findByIdAndIsDeletedFalse(UUID id);
    Optional<TeacherProfile> findByAccount(
            Account account
    );
}

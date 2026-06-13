package phucitdev.course.modules.auth.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phucitdev.course.modules.auth.entity.Account;
import phucitdev.course.modules.auth.entity.Role;

import java.util.Optional;
import java.util.UUID;
public interface AuthRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    @Query("""
        SELECT a
        FROM Account a
        WHERE
        (
            :keyword IS NULL
            OR LOWER(a.fullName)
                LIKE LOWER(
                    CONCAT('%', :keyword, '%')
                )
            OR LOWER(a.email)
                LIKE LOWER(
                    CONCAT('%', :keyword, '%')
                )
        )
        AND
        (
            :role IS NULL
            OR a.role = :role
        )
    """)
    Page<Account> findAccounts(@Param("keyword") String keyword,
            @Param("role") Role role,
            Pageable pageable
    );
}

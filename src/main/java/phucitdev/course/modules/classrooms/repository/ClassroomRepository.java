package phucitdev.course.modules.classrooms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phucitdev.course.modules.classrooms.dto.ClassResponse;
import phucitdev.course.modules.classrooms.entity.Classroom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    @Query("""
    SELECT new phucitdev.course.modules.classrooms.dto.ClassResponse(
        c.id,
        c.name,
        c.description,
        c.startDate,
        c.endDate,
        c.code,
        CAST(c.status AS string),
        c.totalStudent
    )
    FROM Classroom c
    WHERE c.isDeleted = false
    AND
        (:teacherId IS NULL 
            OR c.teacherProfile.id = :teacherId)
    AND
        (:name IS NULL OR :name = '' 
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND
        (:code IS NULL OR :code = '' 
            OR LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%')))
""")
    Page<ClassResponse> search(
            @Param("teacherId") UUID teacherId,
            @Param("name") String name,
            @Param("code") String code,
            Pageable pageable
    );

    Optional<Classroom> findByCode(String code);

    @Query("""
    SELECT new phucitdev.course.modules.classrooms.dto.ClassResponse(
        c.id,
        c.name,
        c.description,
        c.startDate,
        c.endDate,
        c.code,
        CAST(c.status AS string),
        c.totalStudent,
        COALESCE(a.fullName, '')
    )
    FROM Classroom c
    JOIN c.studentProfiles sp
    LEFT JOIN c.teacherProfile t
    LEFT JOIN t.account a
    WHERE sp.id = :studentId
    
    ORDER BY c.createdAt DESC
""")
    List<ClassResponse> getMyClasses(
            @Param("studentId") UUID studentId
    );

    boolean existsByNameIgnoreCaseAndIsDeletedFalse(
            String name
    );
}

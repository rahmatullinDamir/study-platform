package rahmatullin.dev.studyplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rahmatullin.dev.studyplatform.dtos.CourseDto;
import rahmatullin.dev.studyplatform.models.Course;
import rahmatullin.dev.studyplatform.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findCourseByTitle(String title);
    boolean existsByTitleAndIdNot(String title, String id);

    List<Course> findByOwner_Id(Long ownerId);
    List<Course> findCoursesByCourseMembers_Id(Long studentId);
    @Query("""
    SELECT DISTINCT c FROM Course c
    LEFT JOIN c.courseMembers s ON s.id = :studentId
    WHERE s.id IS NULL
    """)
    List<Course> findAvailableCoursesForStudent(@Param("studentId") Long studentId);
}

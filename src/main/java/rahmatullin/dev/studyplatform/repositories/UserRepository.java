package rahmatullin.dev.studyplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rahmatullin.dev.studyplatform.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findIdByEmail(String email);
}

package rahmatullin.dev.studyplatform.repositories;

import org.springframework.data.repository.CrudRepository;
import rahmatullin.dev.studyplatform.models.RefreshToken;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}

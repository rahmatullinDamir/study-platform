package rahmatullin.dev.studyplatform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rahmatullin.dev.studyplatform.models.TaskFile;

import java.util.List;

public interface TaskFileRepository extends JpaRepository<TaskFile, String> {
    List<TaskFile> findByTaskId(String taskId);
}


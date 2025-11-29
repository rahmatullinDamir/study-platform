package rahmatullin.dev.studyplatform.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinCourseDto {
    private String courseId;
    private String keyword;
}

package rahmatullin.dev.studyplatform.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rahmatullin.dev.studyplatform.models.User;

import java.util.HashSet;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto {

    private String id;

    private String title;

    private String description;

    private String keyword;
    private Boolean isPrivateCourse;
    private List<User> courseMembers;

    //TODO: load task
}

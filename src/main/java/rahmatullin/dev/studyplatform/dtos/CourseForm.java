package rahmatullin.dev.studyplatform.dtos;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseForm {
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    private String description;

    private Boolean isPrivateCourse;
    private String keyword;

    @Hidden
    @AssertTrue(message = "Поля isPrivateCourse и keyword должны быть указаны вместе или опущены вместе")
    public boolean isPrivateCourseAndKeywordConsistent() {
        if (((isPrivateCourse == false) || (isPrivateCourse == null)) && keyword != null) {
            return false;
        }
        if (((isPrivateCourse != false) || (isPrivateCourse == null)) && keyword == null) {
            return false;
        }
        return true;
    }
}

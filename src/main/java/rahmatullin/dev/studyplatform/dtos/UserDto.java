package rahmatullin.dev.studyplatform.dtos;

import lombok.Data;
import rahmatullin.dev.studyplatform.models.enums.UserRoles;

@Data
public class UserDto {
    private Long id;
    private String email;

    private String firstName;
    private String lastName;
    private UserRoles role;

}

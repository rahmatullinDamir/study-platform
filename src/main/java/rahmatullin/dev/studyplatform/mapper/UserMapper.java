package rahmatullin.dev.studyplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rahmatullin.dev.studyplatform.dtos.RegistrationForm;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.models.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User mapToUser(RegistrationForm form, String hashPassword);
    UserDto mapToUserDto(User user);
}

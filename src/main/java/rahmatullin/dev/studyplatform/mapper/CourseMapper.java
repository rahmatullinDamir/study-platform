package rahmatullin.dev.studyplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rahmatullin.dev.studyplatform.dtos.CourseDto;
import rahmatullin.dev.studyplatform.dtos.CourseForm;
import rahmatullin.dev.studyplatform.models.Course;
import rahmatullin.dev.studyplatform.models.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CourseMapper {
    @Mapping(target = "id", ignore = true)
    Course mapToCourse(CourseForm form);
    CourseDto mapToCourseDto(Course course);
}

package rahmatullin.dev.studyplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rahmatullin.dev.studyplatform.dtos.CourseDetailsDto;
import rahmatullin.dev.studyplatform.dtos.CourseDto;
import rahmatullin.dev.studyplatform.dtos.CourseForm;
import rahmatullin.dev.studyplatform.models.Course;
import rahmatullin.dev.studyplatform.models.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public abstract class CourseMapper {
    @Mapping(target = "id", ignore = true)
    public abstract Course mapToCourse(CourseForm form);

    public abstract CourseDto mapToCourseDto(Course course);

    @Mapping(target = "isPrivateCourse", source = "isPrivateCourse")
    public abstract CourseDetailsDto mapToCourseDetailsDto(Course course);

    protected boolean mapIsPrivateCurse(Boolean isPrivateCurse) {
        return isPrivateCurse != null ? isPrivateCurse : false;
    }
}
package rahmatullin.dev.studyplatform.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rahmatullin.dev.studyplatform.dtos.CourseDto;
import rahmatullin.dev.studyplatform.dtos.CourseForm;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.exceptions.*;
import rahmatullin.dev.studyplatform.mapper.CourseMapper;
import rahmatullin.dev.studyplatform.mapper.UserMapper;
import rahmatullin.dev.studyplatform.models.Course;
import rahmatullin.dev.studyplatform.models.User;
import rahmatullin.dev.studyplatform.repositories.CourseRepository;
import rahmatullin.dev.studyplatform.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public CourseDto createCourse(CourseForm courseForm, Long teacherId) {
        courseRepository.findCourseByTitle(courseForm.getTitle())
                .ifPresent(user -> {
                    throw new CourseAlreadyExistsException("Курс с таким названием уже существует.");
                });

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Course course = courseMapper.mapToCourse(courseForm);
        course.setOwner(teacher);

        courseRepository.save(course);

        return courseMapper.mapToCourseDto(course);
    }

    public CourseDto deleteCourse(String courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Курс с таким id не найден"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!(teacher.getId().equals(course.getOwner().getId()))) {
            throw new NotPermitedToCourseException("у вас нет прав удалить этот курс.");
        }

        CourseDto courseDto = courseMapper.mapToCourseDto(course);
        
        courseRepository.deleteById(courseId);

        return courseDto;
    }

    public CourseDto updateCourse(CourseForm courseForm, String courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Курс с таким id не найден"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!(teacher.getId().equals(course.getOwner().getId()))) {
            throw new NotPermitedToCourseException("У вас нет прав изменить этот курс.");
        }

        if (courseRepository.existsByTitleAndIdNot(courseForm.getTitle(), courseId)) {
            throw new CourseAlreadyExistsException("Курс с таким названием уже существует.");
        }

        course.setTitle(courseForm.getTitle());
        course.setDescription(courseForm.getTitle());

        Course updated = courseRepository.save(course);

        return courseMapper.mapToCourseDto(updated);

    }

    public List<CourseDto> findCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByOwner_Id(teacherId).stream()
                .map(courseMapper::mapToCourseDto)
                .toList();
    }

    public List<CourseDto> findEnrolledCoursesByStudentId(Long studentId) {
        return courseRepository.findCoursesByCourseMembers_Id(studentId).stream()
                .map(courseMapper::mapToCourseDto)
                .toList();
    }

    public List<CourseDto> findAvailableCoursesForStudent(Long studentId) {
        return courseRepository.findAvailableCoursesForStudent(studentId).stream()
                .map(courseMapper::mapToCourseDto)
                .toList();
    }

    public List<UserDto> findCourseMembers(String courseId, Long currentUserId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));

        if (!course.getOwner().getId().equals(currentUserId)) {
            throw new NotPermitedToCourseException("Вы не являетесь владельцем этого курса");
        }


        return course.getCourseMembers().stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }
}

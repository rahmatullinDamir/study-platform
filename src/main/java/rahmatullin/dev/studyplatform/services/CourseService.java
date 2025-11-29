package rahmatullin.dev.studyplatform.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rahmatullin.dev.studyplatform.dtos.*;
import rahmatullin.dev.studyplatform.exceptions.*;
import rahmatullin.dev.studyplatform.mapper.CourseMapper;
import rahmatullin.dev.studyplatform.mapper.UserMapper;
import rahmatullin.dev.studyplatform.models.Course;
import rahmatullin.dev.studyplatform.models.User;
import rahmatullin.dev.studyplatform.models.enums.UserRoles;
import rahmatullin.dev.studyplatform.repositories.CourseRepository;
import rahmatullin.dev.studyplatform.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public CourseDetailsDto createCourse(CourseForm courseForm, Long teacherId) {
        courseRepository.findCourseByTitle(courseForm.getTitle())
                .ifPresent(user -> {
                    throw new CourseAlreadyExistsException("Курс с таким названием уже существует.");
                });

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Course course = courseMapper.mapToCourse(courseForm);
        course.setOwner(teacher);

        courseRepository.save(course);

        return courseMapper.mapToCourseDetailsDto(course);
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

    public CourseDetailsDto updateCourse(CourseForm courseForm, String courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Курс с таким id не найден"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!teacher.getId().equals(course.getOwner().getId())) {
            throw new NotPermitedToCourseException("У вас нет прав изменить этот курс.");
        }

        if (courseRepository.existsByTitleAndIdNot(courseForm.getTitle(), courseId)) {
            throw new CourseAlreadyExistsException("Курс с таким названием уже существует.");
        }

        course.setTitle(courseForm.getTitle());
        course.setDescription(courseForm.getDescription());

        course.setIsPrivateCourse(courseForm.getIsPrivateCourse());

        course.setKeyword(courseForm.getKeyword());
        System.out.println(course.getIsPrivateCourse());
        System.out.println(course.getKeyword());

        Course updated = courseRepository.save(course);

        return courseMapper.mapToCourseDetailsDto(updated);

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



    public CourseDetailsDto getCourse(String courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (user.getRole() == UserRoles.STUDENT) {
            boolean isMember = course.getCourseMembers().stream()
                    .anyMatch(member -> member.getId().equals(userId));

            if (!isMember) {
                throw new NotPermitedToCourseException("Доступ к курсу запрещён: вы не состоите в этом курсе");
            }
        }

        else if (user.getRole() == UserRoles.TEACHER) {
           boolean isOwner = course.getOwner().getId().equals(userId);

           if(!isOwner) {
               throw new NotPermitedToCourseException("Доступ к курсу запрещён: вы не cоздатель в этом курсе");
           }
        }

        return courseMapper.mapToCourseDetailsDto(course);
    }


    public CourseDto joinCourse(JoinCourseDto joinCourseDto, Long userId) {
        String courseId = joinCourseDto.getCourseId();
        String providedKeyword = joinCourseDto.getKeyword();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));

        if (courseRepository.existsByMemberId(courseId, userId)) {
            throw new NotPermitedToCourseException("Вы уже состоите в этом курсе");
        }

        if (Boolean.TRUE.equals(course.getIsPrivateCourse())) {
            if (providedKeyword == null || providedKeyword.isBlank()) {
                throw new NotPermitedToCourseException("Для вступления в приватный курс требуется ключ доступа");
            }
            if (!providedKeyword.equals(course.getKeyword())) {
                throw new NotPermitedToCourseException("Неверный ключ доступа к курсу");
            }
        }

        course.getCourseMembers().add(user);

        courseRepository.save(course);

        return courseMapper.mapToCourseDto(course);
    }
}

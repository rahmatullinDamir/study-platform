package rahmatullin.dev.studyplatform.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rahmatullin.dev.studyplatform.dtos.*;
import rahmatullin.dev.studyplatform.services.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;



    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDetailsDto>> getCourse(@PathVariable String courseId, HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);

        CourseDetailsDto courseDto = courseService.getCourse(courseId, userId);
        ApiResponse<CourseDetailsDto> apiResponse = new ApiResponse<>("Курс успешно получен!",
                courseDto);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<CourseDetailsDto>> createCourse(@Valid @RequestBody CourseForm courseForm,
                                                               HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);

        CourseDetailsDto courseDetailsDto = courseService.createCourse(courseForm, userId);
        ApiResponse<CourseDetailsDto> apiResponse = new ApiResponse<>("Курс успешно создан!",
                courseDetailsDto);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<CourseDto>> deleteCourse(@PathVariable String courseId, HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);

        CourseDto courseDto = courseService.deleteCourse(courseId, userId);
        ApiResponse<CourseDto> apiResponse = new ApiResponse<>("Курс успешно удален!", courseDto);
        return ResponseEntity.ok(apiResponse);
    }


    @PatchMapping("/update/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<CourseDetailsDto>> updateCourse(@RequestBody @Valid CourseForm courseForm, @PathVariable String courseId,
                                                               HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);

        System.out.println(courseForm.getIsPrivateCourse());
        System.out.println(courseForm.getKeyword());
        CourseDetailsDto courseDetailsDto = courseService.updateCourse(courseForm, courseId, userId);

        ApiResponse<CourseDetailsDto> apiResponse = new ApiResponse<>("Курс успешно изменен!", courseDetailsDto);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/teacher/courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getTeacherCourses(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);

        List<CourseDto> courses = courseService.findCoursesByTeacherId(userId);
        return ResponseEntity.ok(new ApiResponse<>("Список ваших курсов", courses));
    }

    @GetMapping("/student/courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getStudentCourses(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);
        List<CourseDto> courses = courseService.findEnrolledCoursesByStudentId(userId);
        return ResponseEntity.ok(new ApiResponse<>("Ваши курсы", courses));
    }

    @GetMapping("/student/allCourses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllAvailableStudentCourses(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);
        List<CourseDto> courses = courseService.findAvailableCoursesForStudent(userId);
        return ResponseEntity.ok(new ApiResponse<>("Доступные курсы для записи", courses));
    }

    @GetMapping("/getAllMembers/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllCourseMembers(@PathVariable String courseId,
                                                                          HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);
        List<UserDto> courseMembers = courseService.findCourseMembers(courseId, userId);
        return ResponseEntity.ok(new ApiResponse<>("Участники курса:", courseMembers));
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<CourseDto>> joinCourse(@RequestBody JoinCourseDto joinCourseDto,
                                                             HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = Long.parseLong(userIdHeader);

        CourseDto courseDto = courseService.joinCourse(joinCourseDto, userId);

        return ResponseEntity.ok(new ApiResponse<>("Успешно присоединён к курсу", courseDto));
    }

}

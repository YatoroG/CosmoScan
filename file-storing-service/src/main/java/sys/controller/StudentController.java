package sys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sys.model.Student;
import sys.model.requests.StudentUpdateRequest;
import sys.service.StudentService;

@RestController
@RequestMapping("/api/storage/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/search_by_id/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/search_by_name_and_group")
    public Student getStudentByNameAndGroup(@RequestParam String lastName,
                                                            @RequestParam String firstName,
                                                            @RequestParam(required = false) String patronymic,
                                                            @RequestParam String groupName) {
        return studentService.getStudentByNameAndGroup(lastName, firstName, patronymic, groupName);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student addStudent(@RequestBody StudentUpdateRequest request) {
        return studentService.addStudent(request.lastName(), request.firstName(),
                request.patronymic(), request.groupName());
    }

    @PutMapping("/{student_id}")
    public Student updateStudent(@PathVariable Long id,
                                                 @RequestBody StudentUpdateRequest request) {
        return studentService.updateStudent(id, request);
    }

    @DeleteMapping("/{student_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}

package sys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import sys.model.Student;
import sys.model.requests.StudentUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sys.service.StudentService;

@RestController
@RequestMapping("/api/storage/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/search_by_id/{student_id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search_by_name_and_group")
    public ResponseEntity<Student> getStudentByNameAndGroup(@RequestParam String lastName,
                                                            @RequestParam String firstName,
                                                            @RequestParam(required = false) String patronymic,
                                                            @RequestParam String groupName) {
        return studentService.getStudentByNameAndGroup(lastName, firstName, patronymic, groupName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody StudentUpdateRequest request) {
        Student newStudent = studentService.addStudent(request.lastName(), request.firstName(),
                request.patronymic(), request.groupName());
        return ResponseEntity.status(HttpStatus.CREATED).body(newStudent);
    }

    @PutMapping("/{student_id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                 @RequestBody StudentUpdateRequest request) {
        Student updatedStudent = studentService.updateStudent(id, request);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{student_id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}

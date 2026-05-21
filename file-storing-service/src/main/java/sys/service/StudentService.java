package sys.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import sys.model.Student;
import sys.model.requests.StudentUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.repository.StudentRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentByNameAndGroup(String lastName, String firstName, String patronymic, String groupName) {
        return studentRepository.findStudentByAllFields(lastName, firstName, patronymic, groupName);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student addStudent(String lastName, String firstName, String patronymic, String groupName) {
        Student student = Student.builder().lastName(lastName).firstName(firstName)
                .patronymic(patronymic).groupName(groupName).build();
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentUpdateRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Студент с id = " + id + " не найден"));
        if (request.lastName() != null) {
            student.setLastName(request.lastName());
        }
        if (request.firstName() != null) {
            student.setFirstName(request.firstName());
        }
        if (request.patronymic() != null) {
            student.setPatronymic(request.patronymic());
        }
        if (request.groupName() != null) {
            student.setGroupName(request.groupName());
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Студент с id = " + id + " не найден");
        }
        studentRepository.deleteById(id);
    }
}

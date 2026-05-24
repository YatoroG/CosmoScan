package sys.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.model.Student;
import sys.model.requests.StudentUpdateRequest;
import sys.repository.StudentRepository;
import sys.utils.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id = " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public Student getStudentByNameAndGroup(String lastName, String firstName, String patronymic, String groupName) {
        return studentRepository.findByLastNameAndFirstNameAndPatronymicAndGroupName(
                lastName, firstName, patronymic, groupName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Студент с именем '" + lastName + " " + firstName + " " +
                                patronymic + "' из группы '" + groupName + "' не найден"));
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
                .orElseThrow(() -> new EntityNotFoundException("Студент с id = " + id + " не найден"));
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
            throw new EntityNotFoundException("Студент с id = " + id + " не найден");
        }
        studentRepository.deleteById(id);
    }
}

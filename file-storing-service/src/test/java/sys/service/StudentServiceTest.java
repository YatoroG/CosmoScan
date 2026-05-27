package sys.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sys.model.Student;
import sys.model.requests.StudentUpdateRequest;
import sys.repository.StudentRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок-тесты сервиса студентов")
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student mockStudent;

    @BeforeEach
    void setUp() {
        mockStudent = Student.builder().id(1L).lastName("Иванов").firstName("Иван").patronymic("Иванович")
                .groupName("ГР-26").build();
    }

    @Test
    @DisplayName("Получение студента по ID")
    void testGetStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));

        Student result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals("Иванов", result.getLastName());
    }

    @Test
    @DisplayName("Получение студента по имени и группе")
    void testGetStudentByNameAndGroup() {
        when(studentRepository.findByLastNameAndFirstNameAndPatronymicAndGroupName(any(), any(), any(), any()))
                .thenReturn(Optional.of(mockStudent));

        Student result = studentService.getStudentByNameAndGroup("Иванов",
                "Иван", "Иванович", "ГР-26");

        assertNotNull(result);
        assertEquals("ГР-26", result.getGroupName());
    }

    @Test
    @DisplayName("Получение всех студентов")
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(mockStudent, new Student()));

        List<Student> result = studentService.getAllStudents();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Добавление студента")
    void testAddStudent() {
        when(studentRepository.save(any(Student.class))).thenReturn(mockStudent);

        Student result = studentService.addStudent("Иванов", "Иван", "Иванович", "ГР-26");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Редактирование данных студента")
    void testUpdateStudents() {
        StudentUpdateRequest request = new StudentUpdateRequest("Петров", "Пётр", "Петрович", "ГР-25");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        Student result = studentService.updateStudent(1L, request);

        assertEquals("Петров", result.getLastName());
        assertEquals("ГР-25", result.getGroupName());
    }

    @Test
    @DisplayName("Удаление студента")
    void testDeleteStudent() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
        verify(studentRepository, times(1)).deleteById(1L);
    }
}

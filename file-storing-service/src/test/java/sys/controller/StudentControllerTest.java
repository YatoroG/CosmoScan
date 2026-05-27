package sys.controller;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sys.model.Student;
import sys.model.requests.StudentUpdateRequest;
import sys.service.StudentService;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@DisplayName("Мок-тесты контроллера студентов")
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Получение студента по ID")
    void testGetStudentById() throws Exception {
        Student student = Student.builder().id(1L).lastName("Иванов").firstName("Иван").build();
        when(studentService.getStudentById(1L)).thenReturn(student);

        mockMvc.perform(get("/api/storage/students/search_by_id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    @DisplayName("Получение студента по имени и группе")
    void testGetStudentByNameAndGroup() throws Exception {
        Student student = Student.builder().id(1L).lastName("Иванов").firstName("Иван")
                .patronymic("Иванович").build();
        when(studentService.getStudentByNameAndGroup(anyString(), anyString(), anyString(),
                anyString())).thenReturn(student);

        mockMvc.perform(get("/api/storage/students/search_by_name_and_group")
                        .param("lastName", "Иванов")
                        .param("firstName", "Иван")
                        .param("patronymic", "Иванович")
                        .param("groupName", "ГР-26"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Добавление студента")
    void testAddStudent() throws Exception {
        StudentUpdateRequest request = new StudentUpdateRequest(
                "Иванов", "Иван", "Иванович", "ГР-26");
        Student student = Student.builder().id(1L).lastName("Иванов").build();

        when(studentService.addStudent(anyString(), anyString(), any(), anyString())).thenReturn(student);

        mockMvc.perform(post("/api/storage/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Редактирование студента")
    void testUpdateStudent() throws Exception {
        StudentUpdateRequest request = new StudentUpdateRequest(
                "Петров", "Иван", "Иванович", "ГР-26");
        Student updatedStudent = Student.builder().id(1L).lastName("Петров").build();

        when(studentService.updateStudent(eq(1L), any(StudentUpdateRequest.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/api/storage/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Петров"));
    }

    @Test
    @DisplayName("Удаление студента")
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/api/storage/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Просмотр всех студентов")
    void testGetAllStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(new Student(), new Student()));

        mockMvc.perform(get("/api/storage/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}

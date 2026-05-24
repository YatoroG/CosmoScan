package sys.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sys.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByLastNameAndFirstNameAndPatronymicAndGroupName(String lastName,
                                                                          String firstName,
                                                                          String patronymic,
                                                                          String groupName);
}

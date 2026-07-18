package recruitment.dev.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import recruitment.dev.employeeservice.enities.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional findByName(String name);

}

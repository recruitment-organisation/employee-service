package recruitment.dev.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import recruitment.dev.employeeservice.enities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee , Long> {
    Employee findByEmail(String email);
    Employee findByPhone(String phone);
    Employee findByKeycloakId(String keycloakId);

}

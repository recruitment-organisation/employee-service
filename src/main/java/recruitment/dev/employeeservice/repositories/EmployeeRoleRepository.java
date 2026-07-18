package recruitment.dev.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import recruitment.dev.employeeservice.enities.EmployeeRole;

public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, Long> {
    EmployeeRole findByName(String name);
}

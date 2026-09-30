package recruitment.dev.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import recruitment.dev.employeeservice.enities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee , Long> {
    Employee findByEmail(String email);
    Employee findByPhone(String phone);
    Employee findByKeycloakId(String keycloakId);
    org.springframework.data.domain.Page<Employee> findByCompanyId(Long companyId, org.springframework.data.domain.Pageable pageable);
    long countByCompanyIdAndRoleNameIgnoreCase(Long companyId, String roleName);

}

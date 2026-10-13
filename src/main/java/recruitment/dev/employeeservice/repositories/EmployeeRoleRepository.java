package recruitment.dev.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recruitment.dev.employeeservice.enities.EmployeeRole;

public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, Long> {
    EmployeeRole findByName(String name);
    java.util.Optional<EmployeeRole> findByNameIgnoreCase(String name);
    java.util.Optional<EmployeeRole> findByCompanyIdAndNameIgnoreCase(Long companyId, String name);
    Page<EmployeeRole> findByCompanyId(Long companyId, Pageable pageable);
    boolean existsByCompanyIdAndNameIgnoreCase(Long companyId, String name);
    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(Long companyId, String name, Long id);
}

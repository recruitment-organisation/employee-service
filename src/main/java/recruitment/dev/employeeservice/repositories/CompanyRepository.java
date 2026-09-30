package recruitment.dev.employeeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import recruitment.dev.employeeservice.enities.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlug(String slug);
}

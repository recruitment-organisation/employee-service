package recruitment.dev.employeeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import recruitment.dev.employeeservice.dto.*;
import recruitment.dev.employeeservice.enities.*;
import recruitment.dev.employeeservice.mapper.EmployeeMapper;
import recruitment.dev.employeeservice.repositories.*;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companies;
    private final EmployeeRepository employees;
    private final EmployeeRoleRepository roles;
    private final EmployeeMapper employeeMapper;

    public CompanyDto create(CompanyDto input) {
        String slug = uniqueSlug(input.getSlug() == null || input.getSlug().isBlank() ? input.getName() : input.getSlug(), null);
        if (companies.existsByNameIgnoreCase(input.getName().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Company name already exists");
        Company company = Company.builder().name(input.getName().trim()).slug(slug).email(trim(input.getEmail()))
                .phone(trim(input.getPhone())).address(trim(input.getAddress())).website(trim(input.getWebsite()))
                .logoUrl(trim(input.getLogoUrl())).description(trim(input.getDescription())).active(input.isActive()).build();
        return toDto(companies.save(company));
    }

    @Transactional(readOnly = true)
    public Page<CompanyDto> list(Pageable pageable) { return companies.findAll(pageable).map(this::toDto); }

    @Transactional(readOnly = true)
    public CompanyDto get(Long id) { return toDto(requireCompany(id)); }

    public CompanyDto update(Long id, CompanyDto input) {
        Company company = requireCompany(id);
        company.setName(input.getName().trim());
        company.setSlug(uniqueSlug(input.getSlug() == null || input.getSlug().isBlank() ? input.getName() : input.getSlug(), id));
        company.setEmail(trim(input.getEmail())); company.setPhone(trim(input.getPhone())); company.setAddress(trim(input.getAddress()));
        company.setWebsite(trim(input.getWebsite())); company.setLogoUrl(trim(input.getLogoUrl())); company.setDescription(trim(input.getDescription()));
        return toDto(company);
    }

    public CompanyDto setStatus(Long id, boolean active) { Company c = requireCompany(id); c.setActive(active); return toDto(c); }

    public EmployeeDto createHrProfile(Long companyId, CreateHrProfileRequest request) {
        Company company = requireCompany(companyId);
        if (!company.isActive()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Company is inactive");
        EmployeeRole hrRole = roles.findByNameIgnoreCase("HR").orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "HR employee role is not configured"));
        Employee employee = Employee.builder().keycloakId(request.keycloakId()).firstName(request.firstName()).lastName(request.lastName())
                .email(request.email()).phone(request.phone()).hireDate(LocalDate.now()).position("Responsable RH")
                .role(hrRole).company(company).active(request.active() == null || request.active()).build();
        return employeeMapper.toEmployeeDto(employees.save(employee));
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> listHr(Long companyId, Pageable pageable) {
        requireCompany(companyId);
        return employees.findByCompanyId(companyId, pageable).map(employeeMapper::toEmployeeDto);
    }

    public EmployeeDto setEmployeeStatus(Long id, boolean active) {
        Employee employee = employees.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HR user not found"));
        employee.setActive(active);
        return employeeMapper.toEmployeeDto(employee);
    }

    @Transactional(readOnly = true)
    public AdminDashboardStats stats() {
        long activeCompanies = companies.findAll().stream().filter(Company::isActive).count();
        long hr = employees.findAll().stream().filter(e -> e.getRole() != null && "HR".equalsIgnoreCase(e.getRole().getName())).count();
        long activeHr = employees.findAll().stream().filter(e -> e.isActive() && e.getRole() != null && "HR".equalsIgnoreCase(e.getRole().getName())).count();
        return new AdminDashboardStats(companies.count(), activeCompanies, hr, activeHr);
    }

    private Company requireCompany(Long id) { return companies.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found")); }
    private CompanyDto toDto(Company c) { return CompanyDto.builder().id(c.getId()).name(c.getName()).slug(c.getSlug()).email(c.getEmail()).phone(c.getPhone()).address(c.getAddress()).website(c.getWebsite()).logoUrl(c.getLogoUrl()).description(c.getDescription()).active(c.isActive()).createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt()).hrCount(employees.countByCompanyIdAndRoleNameIgnoreCase(c.getId(), "HR")).build(); }
    private String uniqueSlug(String value, Long currentId) {
        String base = Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD).replaceAll("\\p{M}", "").replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        if (base.isBlank()) base = "company";
        if (currentId != null) {
            Company current = requireCompany(currentId);
            if (current.getSlug().equals(base)) return base;
        }
        String candidate = base; int suffix = 2;
        while (companies.existsBySlug(candidate)) candidate = base + "-" + suffix++;
        return candidate;
    }
    private String trim(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}

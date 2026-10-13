package recruitment.dev.employeeservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recruitment.dev.employeeservice.dto.CompanyDto;
import recruitment.dev.employeeservice.dto.CreateHrProfileRequest;
import recruitment.dev.employeeservice.enities.Company;
import recruitment.dev.employeeservice.enities.Employee;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeMapper;
import recruitment.dev.employeeservice.repositories.CompanyRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock private CompanyRepository companies;
    @Mock private EmployeeRepository employees;
    @Mock private EmployeeRoleRepository roles;
    @Mock private EmployeeMapper employeeMapper;
    @InjectMocks private CompanyService service;

    @Test
    void createsTenantScopedHrRoleWithCompany() {
        CompanyDto request = CompanyDto.builder().name("NovaTech Solutions").active(true).build();
        when(companies.existsByNameIgnoreCase("NovaTech Solutions")).thenReturn(false);
        when(companies.existsBySlug("novatech-solutions")).thenReturn(false);
        when(companies.save(any(Company.class))).thenAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(1L);
            return company;
        });

        CompanyDto created = service.create(request);

        assertThat(created.getId()).isEqualTo(1L);
        verify(roles).save(org.mockito.ArgumentMatchers.argThat(role ->
                "HR".equals(role.getName())
                        && role.getCompany() != null
                        && role.getCompany().getId().equals(1L)));
    }

    @Test
    void createsHrProfileUsingRoleFromRequestedCompany() {
        Company company = Company.builder().id(2L).name("Atlas Digital").active(true).build();
        EmployeeRole hrRole = EmployeeRole.builder().id(7L).name("HR").company(company).build();
        CreateHrProfileRequest request = new CreateHrProfileRequest(
                "kc-123", "Sonia", "Ben Salem", "sonia.hr@example.com", "+21620000001", true);
        when(companies.findById(2L)).thenReturn(Optional.of(company));
        when(roles.findByCompanyIdAndNameIgnoreCase(2L, "HR")).thenReturn(Optional.of(hrRole));
        when(employees.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeMapper.toEmployeeDto(any(Employee.class)))
                .thenReturn(recruitment.dev.employeeservice.dto.EmployeeDto.builder().id(10L).build());

        service.createHrProfile(2L, request);

        verify(roles).findByCompanyIdAndNameIgnoreCase(2L, "HR");
        verify(employees).save(org.mockito.ArgumentMatchers.argThat(employee ->
                employee.getCompany().getId().equals(2L)
                        && employee.getRole().getId().equals(7L)));
    }
}

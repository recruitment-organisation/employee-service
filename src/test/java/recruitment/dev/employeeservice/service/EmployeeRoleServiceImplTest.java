package recruitment.dev.employeeservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.enities.Company;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeRoleMapper;
import recruitment.dev.employeeservice.repositories.CompanyRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRoleServiceImplTest {

    @Mock private EmployeeRoleRepository employeeRoleRepository;
    @Mock private EmployeeRoleMapper employeeRoleMapper;
    @Mock private CompanyRepository companyRepository;
    @InjectMocks private EmployeeRoleServiceImpl service;

    @Test
    void createsUniqueRole() {
        EmployeeRoleDto request = EmployeeRoleDto.builder().name("HR").description("Recruiter").companyId(1L).build();
        EmployeeRole role = new EmployeeRole();
        Company company = Company.builder().id(1L).name("NovaTech Solutions").build();
        when(employeeRoleRepository.existsByCompanyIdAndNameIgnoreCase(1L, "HR")).thenReturn(false);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(employeeRoleMapper.toEmployeeRole(request)).thenReturn(role);
        when(employeeRoleMapper.toEmployeeRoleDto(role)).thenReturn(request);

        assertThat(service.createEmployeeRole(request)).isSameAs(request);
        assertThat(role.getCompany()).isSameAs(company);
        verify(employeeRoleRepository).save(role);
    }

    @Test
    void rejectsDuplicateRoleName() {
        when(employeeRoleRepository.existsByCompanyIdAndNameIgnoreCase(1L, "HR")).thenReturn(true);

        assertThatThrownBy(() -> service.createEmployeeRole(
                EmployeeRoleDto.builder().name("HR").companyId(1L).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee Role already exists");
        verify(employeeRoleRepository, never()).save(any());
    }

    @Test
    void updatesDeletesAndReportsMissingRole() {
        EmployeeRole role = new EmployeeRole();
        role.setCompany(Company.builder().id(1L).build());
        EmployeeRoleDto update = EmployeeRoleDto.builder().name("MANAGER").description("Manager").build();
        when(employeeRoleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(employeeRoleRepository.save(role)).thenReturn(role);
        when(employeeRoleMapper.toEmployeeRoleDto(role)).thenReturn(update);

        assertThat(service.updateEmployeeRole(update, 2L)).isSameAs(update);
        assertThat(role.getName()).isEqualTo("MANAGER");
        service.deleteEmployeeRole(2L);
        verify(employeeRoleRepository).delete(role);

        when(employeeRoleRepository.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getEmployeeRoleById(3L)).isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    void listsOnlyRolesFromRequestedCompany() {
        EmployeeRole role = new EmployeeRole();
        EmployeeRoleDto expected = EmployeeRoleDto.builder().name("Comptable").companyId(2L).build();
        PageRequest pageable = PageRequest.of(0, 20);
        when(employeeRoleRepository.findByCompanyId(2L, pageable))
                .thenReturn(new PageImpl<>(List.of(role), pageable, 1));
        when(employeeRoleMapper.toEmployeeRoleDto(role)).thenReturn(expected);

        assertThat(service.getAllEmployeeRoleByCompany(2L, pageable).getContent())
                .containsExactly(expected);
        verify(employeeRoleRepository, never()).findAll(pageable);
    }
}

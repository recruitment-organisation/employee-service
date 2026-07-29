package recruitment.dev.employeeservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeRoleMapper;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRoleServiceImplTest {

    @Mock private EmployeeRoleRepository employeeRoleRepository;
    @Mock private EmployeeRoleMapper employeeRoleMapper;
    @InjectMocks private EmployeeRoleServiceImpl service;

    @Test
    void createsUniqueRole() {
        EmployeeRoleDto request = EmployeeRoleDto.builder().name("HR").description("Recruiter").build();
        EmployeeRole role = new EmployeeRole();
        when(employeeRoleRepository.findByName("HR")).thenReturn(null);
        when(employeeRoleMapper.toEmployeeRole(request)).thenReturn(role);
        when(employeeRoleMapper.toEmployeeRoleDto(role)).thenReturn(request);

        assertThat(service.createEmployeeRole(request)).isSameAs(request);
        verify(employeeRoleRepository).save(role);
    }

    @Test
    void rejectsDuplicateRoleName() {
        when(employeeRoleRepository.findByName("HR")).thenReturn(new EmployeeRole());

        assertThatThrownBy(() -> service.createEmployeeRole(EmployeeRoleDto.builder().name("HR").build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee Role already exists");
        verify(employeeRoleRepository, never()).save(any());
    }

    @Test
    void updatesDeletesAndReportsMissingRole() {
        EmployeeRole role = new EmployeeRole();
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
}

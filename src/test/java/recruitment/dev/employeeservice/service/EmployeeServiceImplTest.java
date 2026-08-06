package recruitment.dev.employeeservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recruitment.dev.employeeservice.dto.EmployeeDto;
import recruitment.dev.employeeservice.enities.Department;
import recruitment.dev.employeeservice.enities.Employee;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeMapper;
import recruitment.dev.employeeservice.repositories.DepartmentRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeMapper employeeMapper;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private EmployeeRoleRepository employeeRoleRepository;
    @InjectMocks private EmployeeServiceImpl service;

    @Test
    void createsEmployeeWithResolvedDepartmentAndRole() {
        EmployeeDto request = employee(2L, 3L);
        Employee entity = new Employee();
        Department department = new Department();
        EmployeeRole role = new EmployeeRole();
        EmployeeDto expected = employee(2L, 3L);
        when(employeeMapper.toEmployee(request)).thenReturn(entity);
        when(departmentRepository.findById(3L)).thenReturn(Optional.of(department));
        when(employeeRoleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(employeeRepository.save(entity)).thenReturn(entity);
        when(employeeMapper.toEmployeeDto(entity)).thenReturn(expected);

        assertThat(service.createEmployee(request)).isSameAs(expected);
        assertThat(entity.getDepartment()).isSameAs(department);
        assertThat(entity.getRole()).isSameAs(role);
    }

    @Test
    void updatesOnlyOptionalRelationsProvidedByRequest() {
        Employee existing = new Employee();
        existing.setDepartment(new Department());
        existing.setRole(new EmployeeRole());
        EmployeeDto request = employee(null, null);
        EmployeeDto expected = employee(null, null);
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(existing);
        when(employeeMapper.toEmployeeDto(existing)).thenReturn(expected);

        assertThat(service.updateEmployee(request, 5L)).isSameAs(expected);
        assertThat(existing.getFirstName()).isEqualTo("Ada");
        verifyNoInteractions(departmentRepository, employeeRoleRepository);
    }

    @Test
    void updatesRoleAndDepartmentWhenTheyAreProvided() {
        Employee existing = new Employee();
        EmployeeDto request = employee(8L, 9L);
        EmployeeRole role = new EmployeeRole();
        Department department = new Department();
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(employeeRoleRepository.findById(8L)).thenReturn(Optional.of(role));
        when(departmentRepository.findById(9L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(existing)).thenReturn(existing);
        when(employeeMapper.toEmployeeDto(existing)).thenReturn(request);

        assertThat(service.updateEmployee(request, 5L)).isSameAs(request);
        assertThat(existing.getRole()).isSameAs(role);
        assertThat(existing.getDepartment()).isSameAs(department);
        verify(employeeRepository).save(existing);
    }

    @Test
    void failsWhenRequiredDepartmentIsMissingDuringCreate() {
        EmployeeDto request = employee(2L, 3L);
        when(employeeMapper.toEmployee(request)).thenReturn(new Employee());
        when(departmentRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createEmployee(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicatePhoneBeforeCreatingTheEmployee() {
        EmployeeDto request = employee(2L, 3L);
        Employee existing = new Employee();
        existing.setId(9L);
        when(employeeRepository.findByPhone(request.getPhone())).thenReturn(existing);

        assertThatThrownBy(() -> service.createEmployee(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deletesExistingEmployeeAndReportsUnknownId() {
        Employee employee = new Employee();
        when(employeeRepository.findById(6L)).thenReturn(Optional.of(employee));
        service.deleteEmployee(6L);
        verify(employeeRepository).delete(employee);

        when(employeeRepository.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getEmployeeById(7L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee not found");
    }

    private EmployeeDto employee(Long roleId, Long departmentId) {
        return EmployeeDto.builder().keycloakId("kc-1").firstName("Ada").lastName("Lovelace")
                .email("ada@test.local").phone("22000000").hireDate(LocalDate.of(2025, 1, 1))
                .position("Engineer").roleId(roleId).departmentId(departmentId).build();
    }
}

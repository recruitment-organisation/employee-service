package recruitment.dev.employeeservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recruitment.dev.employeeservice.dto.EmployeeDto;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;

public interface EmployeeRoleService {
EmployeeRoleDto createEmployeeRole(EmployeeRoleDto employeeRoleDto);
Page<EmployeeRoleDto> getAllEmployeeRole(Pageable pageable);
EmployeeRoleDto getEmployeeRoleById(Long id);
EmployeeRoleDto updateEmployeeRole(EmployeeRoleDto employeeRoleDto, Long idRole);
void deleteEmployeeRole(Long id);

}

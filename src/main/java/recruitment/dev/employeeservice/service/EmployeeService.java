package recruitment.dev.employeeservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recruitment.dev.employeeservice.dto.EmployeeDto;

public interface EmployeeService {
    EmployeeDto getEmployeeById(Long id);
    EmployeeDto getEmployeeByKeycloakId(String keycloakId);
    EmployeeDto createEmployee(EmployeeDto employeeDto );
    EmployeeDto updateEmployee(EmployeeDto employeeDto , Long idEmployee);
    void deleteEmployee(Long id);
    Page<EmployeeDto> getAllEmployee(Pageable pageable);

}

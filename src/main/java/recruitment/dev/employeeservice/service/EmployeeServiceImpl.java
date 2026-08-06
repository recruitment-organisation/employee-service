package recruitment.dev.employeeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import recruitment.dev.employeeservice.dto.EmployeeDto;

import recruitment.dev.employeeservice.enities.Department;
import recruitment.dev.employeeservice.enities.Employee;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeMapper;
import recruitment.dev.employeeservice.repositories.DepartmentRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {


    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRoleRepository employeeRoleRepository;


    @Override
    public EmployeeDto getEmployeeById(Long id) {

        Employee employee = findEmployeeById(id);

        return employeeMapper.toEmployeeDto(employee);
    }

    @Override
    public EmployeeDto getEmployeeByKeycloakId(String keycloakId) {
        Employee employee = employeeRepository.findByKeycloakId(keycloakId);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        return employeeMapper.toEmployeeDto(employee);
    }


    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        ensureUniqueIdentity(employeeDto, null);

        Employee employee = employeeMapper.toEmployee(employeeDto);


        Department department = findDepartmentById(employeeDto.getDepartmentId());
        EmployeeRole role = findEmployeeRoleById(employeeDto.getRoleId());


        employee.setDepartment(department);
        employee.setRole(role);


        Employee savedEmployee = employeeRepository.save(employee);


        return employeeMapper.toEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employeeDto, Long idEmployee) {


        Employee employee = findEmployeeById(idEmployee);
        ensureUniqueIdentity(employeeDto, idEmployee);


        employee.setKeycloakId(employeeDto.getKeycloakId());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhone(employeeDto.getPhone());
        employee.setHireDate(employeeDto.getHireDate());
        employee.setPosition(employeeDto.getPosition());


        if(employeeDto.getRoleId() != null){

            EmployeeRole role = findEmployeeRoleById(employeeDto.getRoleId());

            employee.setRole(role);
        }


        if(employeeDto.getDepartmentId() != null){

            Department department = findDepartmentById(employeeDto.getDepartmentId());

            employee.setDepartment(department);
        }


        Employee updatedEmployee = employeeRepository.save(employee);


        return employeeMapper.toEmployeeDto(updatedEmployee);
    }



    @Override
    public void deleteEmployee(Long id) {

        Employee employee = findEmployeeById(id);

        employeeRepository.delete(employee);
    }



    @Override
    public Page<EmployeeDto> getAllEmployee(Pageable pageable) {

        Page<Employee> employees = employeeRepository.findAll(pageable);

        return employees.map(employeeMapper::toEmployeeDto);
    }



    private Employee findEmployeeById(Long id){

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));
    }



    private Department findDepartmentById(Long id){

        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Department not found"));
    }



    private EmployeeRole findEmployeeRoleById(Long id){

        return employeeRoleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee role not found"));
    }

    private void ensureUniqueIdentity(EmployeeDto employeeDto, Long employeeId) {
        if (belongsToAnotherEmployee(employeeRepository.findByEmail(employeeDto.getEmail()), employeeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee email already exists");
        }
        if (belongsToAnotherEmployee(employeeRepository.findByPhone(employeeDto.getPhone()), employeeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee phone number already exists");
        }
        if (belongsToAnotherEmployee(employeeRepository.findByKeycloakId(employeeDto.getKeycloakId()), employeeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee profile already exists");
        }
    }

    private boolean belongsToAnotherEmployee(Employee employee, Long employeeId) {
        return employee != null && !java.util.Objects.equals(employee.getId(), employeeId);
    }

}

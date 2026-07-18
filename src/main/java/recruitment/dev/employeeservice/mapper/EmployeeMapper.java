package recruitment.dev.employeeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import recruitment.dev.employeeservice.dto.EmployeeDto;
import recruitment.dev.employeeservice.enities.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "keycloakId", source = "keycloakId")

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "departmentId", source = "department.id")
    EmployeeDto toEmployeeDto(Employee employee);


    @Mapping(target = "role", ignore = true)
    @Mapping(target = "department", ignore = true)
    Employee toEmployee(EmployeeDto employeeDto);

}

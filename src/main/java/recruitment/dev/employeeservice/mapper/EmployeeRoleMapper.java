package recruitment.dev.employeeservice.mapper;

import org.mapstruct.Mapper;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.enities.EmployeeRole;

@Mapper(componentModel = "spring")
public interface EmployeeRoleMapper {
    EmployeeRoleDto toEmployeeRoleDto(EmployeeRole employeeRole);
    EmployeeRole toEmployeeRole(EmployeeRoleDto employeeRoleDto);

}

package recruitment.dev.employeeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.enities.EmployeeRole;

@Mapper(componentModel = "spring")
public interface EmployeeRoleMapper {
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    EmployeeRoleDto toEmployeeRoleDto(EmployeeRole employeeRole);

    @Mapping(target = "company", ignore = true)
    EmployeeRole toEmployeeRole(EmployeeRoleDto employeeRoleDto);

}

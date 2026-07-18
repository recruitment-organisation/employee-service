package recruitment.dev.employeeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import recruitment.dev.employeeservice.dto.DepartmentDto;
import recruitment.dev.employeeservice.enities.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDto toDepartmentDto(Department department);
    @Mapping(target = "employees", ignore = true)

    Department toDepartment(DepartmentDto departmentDto);


}

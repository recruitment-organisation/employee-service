package recruitment.dev.employeeservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recruitment.dev.employeeservice.dto.DepartmentDto;

public interface DepartmentService {
    DepartmentDto createDepartment(DepartmentDto departmentDto);
    DepartmentDto getDepartmentById(Long id);
    DepartmentDto getDepartmentByName(String name);
    DepartmentDto updateDepartment(DepartmentDto departmentDto);
    void deleteDepartment(Long id);
    Page<DepartmentDto> getAllDepartments(Pageable pageable);

}

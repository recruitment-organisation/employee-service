package recruitment.dev.employeeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import recruitment.dev.employeeservice.dto.DepartmentDto;
import recruitment.dev.employeeservice.enities.Department;
import recruitment.dev.employeeservice.mapper.DepartmentMapper;
import recruitment.dev.employeeservice.repositories.DepartmentRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class DepartmentServiceImpl implements DepartmentService {


    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;


    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {

        if (findDepartementByName(departmentDto.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department already exists");
        }
        Department department = departmentMapper.toDepartment(departmentDto);
        departmentRepository.save(department);


        return departmentMapper.toDepartmentDto(department);
    }

    @Override
    public DepartmentDto getDepartmentById(Long id) {
        Department department = findDepartementById(id);

        return departmentMapper.toDepartmentDto(department);


    }
    @Override
    public DepartmentDto getDepartmentByName(String name) {
        Department department = findDepartementByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found with name: " + name));
        return departmentMapper.toDepartmentDto(department);
    }

    @Override
    public DepartmentDto updateDepartment(DepartmentDto departmentDto) {
        Department department = findDepartementById(departmentDto.getId());
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());

        departmentRepository.save(department);


        return departmentMapper.toDepartmentDto(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = findDepartementById(id);
        departmentRepository.delete(department);

    }

    @Override
    public Page<DepartmentDto> getAllDepartments(Pageable pageable) {
        Page<Department> departments = departmentRepository.findAll(pageable);

        return departments.map(departmentMapper::toDepartmentDto);
    }

    private Department findDepartementById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found with id: " + id));
    }

    private Optional<Department> findDepartementByName(String name) {
        return departmentRepository.findByName(name);
    }
}

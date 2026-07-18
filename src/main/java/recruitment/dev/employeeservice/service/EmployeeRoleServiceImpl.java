package recruitment.dev.employeeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeRoleMapper;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

@Service
@RequiredArgsConstructor


public class EmployeeRoleServiceImpl implements EmployeeRoleService {
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeRoleMapper employeeRoleMapper;
    @Override
    public EmployeeRoleDto createEmployeeRole(EmployeeRoleDto employeeRoleDto) {
        if (findEmployeeRoleBy(employeeRoleDto.getName()) != null) {
            throw new RuntimeException("Employee Role already exists");
        }
        EmployeeRole employeeRole = employeeRoleMapper.toEmployeeRole(employeeRoleDto);
        employeeRoleRepository.save(employeeRole);
        return employeeRoleMapper.toEmployeeRoleDto(employeeRole);


    }

    @Override
    public Page<EmployeeRoleDto> getAllEmployeeRole(Pageable pageable) {
        Page<EmployeeRole> employeeRoles = employeeRoleRepository.findAll(pageable);




     return employeeRoles.map(employeeRoleMapper::toEmployeeRoleDto);
    }

    @Override
    public EmployeeRoleDto getEmployeeRoleById(Long id) {
        EmployeeRole employeeRole = findEmployeeRoleById(id);
        return employeeRoleMapper.toEmployeeRoleDto(employeeRole);

    }

    @Override
    public EmployeeRoleDto updateEmployeeRole(EmployeeRoleDto employeeRoleDto, Long idRole) {
        EmployeeRole employeeRole = findEmployeeRoleById(idRole);
        employeeRole.setName(employeeRoleDto.getName());
        employeeRole.setDescription(employeeRoleDto.getDescription());
        employeeRoleRepository.save(employeeRole);
        return employeeRoleMapper.toEmployeeRoleDto(employeeRole);


    }

    @Override
    public void deleteEmployeeRole(Long id) {
        EmployeeRole employeeRole = findEmployeeRoleById(id);
        employeeRoleRepository.delete(employeeRole);

    }
    private EmployeeRole findEmployeeRoleById(Long id) {
        return employeeRoleRepository.findById(id).orElseThrow();
    }
    private EmployeeRole findEmployeeRoleBy(String name) {
        return  employeeRoleRepository.findByName(name);
    }
}

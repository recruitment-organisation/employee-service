package recruitment.dev.employeeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.enities.Company;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.mapper.EmployeeRoleMapper;
import recruitment.dev.employeeservice.repositories.CompanyRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeRoleServiceImpl implements EmployeeRoleService {
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeRoleMapper employeeRoleMapper;
    private final CompanyRepository companyRepository;
    @Override
    public EmployeeRoleDto createEmployeeRole(EmployeeRoleDto employeeRoleDto) {
        if (employeeRoleRepository.existsByCompanyIdAndNameIgnoreCase(
                employeeRoleDto.getCompanyId(), employeeRoleDto.getName())) {
            throw new RuntimeException("Employee Role already exists");
        }
        Company company = companyRepository.findById(employeeRoleDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        EmployeeRole employeeRole = employeeRoleMapper.toEmployeeRole(employeeRoleDto);
        employeeRole.setCompany(company);
        employeeRoleRepository.save(employeeRole);
        return employeeRoleMapper.toEmployeeRoleDto(employeeRole);


    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeRoleDto> getAllEmployeeRole(Pageable pageable) {
        Page<EmployeeRole> employeeRoles = employeeRoleRepository.findAll(pageable);




     return employeeRoles.map(employeeRoleMapper::toEmployeeRoleDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeRoleDto> getAllEmployeeRoleByCompany(Long companyId, Pageable pageable) {
        return employeeRoleRepository.findByCompanyId(companyId, pageable)
                .map(employeeRoleMapper::toEmployeeRoleDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeRoleDto getEmployeeRoleById(Long id) {
        EmployeeRole employeeRole = findEmployeeRoleById(id);
        return employeeRoleMapper.toEmployeeRoleDto(employeeRole);

    }

    @Override
    public EmployeeRoleDto updateEmployeeRole(EmployeeRoleDto employeeRoleDto, Long idRole) {
        EmployeeRole employeeRole = findEmployeeRoleById(idRole);
        if (employeeRoleRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(
                employeeRole.getCompany().getId(), employeeRoleDto.getName(), idRole)) {
            throw new RuntimeException("Employee Role already exists");
        }
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
}

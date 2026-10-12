package recruitment.dev.employeeservice.web;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.service.EmployeeRoleService;

@RestController
@RequestMapping("/employee-role")
@RequiredArgsConstructor
public class EmployeeRoleController {
    private final EmployeeRoleService employeeRoleService;

    @PreAuthorize("hasRole('HR')")
    @PostMapping("/create")
    public ResponseEntity<EmployeeRoleDto> createEmployeeRole(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EmployeeRoleDto employeeRoleDto) {
        employeeRoleDto.setCompanyId(requiredCompany(jwt));
        EmployeeRoleDto role = employeeRoleService.createEmployeeRole(employeeRoleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PreAuthorize("hasRole('HR')")
    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeRoleDto> updateEmployeeRole(@PathVariable Long id,
                                                              @AuthenticationPrincipal Jwt jwt,
                                                              @Valid @RequestBody EmployeeRoleDto employeeRoleDto) {
        ensureTenant(jwt, employeeRoleService.getEmployeeRoleById(id));
        return ResponseEntity.ok(employeeRoleService.updateEmployeeRole(employeeRoleDto, id));
    }

    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<EmployeeRoleDto> getEmployeeRoleById(
            @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        EmployeeRoleDto role = employeeRoleService.getEmployeeRoleById(id);
        ensureTenant(jwt, role);
        return ResponseEntity.ok(role);
    }

    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @GetMapping("/getall")
    public ResponseEntity<Page<EmployeeRoleDto>> getAllEmployeeRoles(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(employeeRoleService.getAllEmployeeRoleByCompany(
                requiredCompany(jwt), Pageable.ofSize(size).withPage(page)));
    }

    @PreAuthorize("hasRole('HR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployeeRole(
            @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        ensureTenant(jwt, employeeRoleService.getEmployeeRoleById(id));
        employeeRoleService.deleteEmployeeRole(id);
        return ResponseEntity.noContent().build();
    }

    private Long requiredCompany(Jwt jwt) {
        Object claim = jwt == null ? null : jwt.getClaim("companyId");
        if (claim instanceof Number number) return number.longValue();
        if (claim instanceof String value && !value.isBlank()) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException ignored) {
                // Handled by the access-denied response below.
            }
        }
        throw new AccessDeniedException("No company is associated with this account");
    }

    private void ensureTenant(Jwt jwt, EmployeeRoleDto role) {
        if (role.getCompanyId() == null || !role.getCompanyId().equals(requiredCompany(jwt))) {
            throw new AccessDeniedException("Cross-company access denied");
        }
    }
}

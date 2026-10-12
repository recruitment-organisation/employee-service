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
import recruitment.dev.employeeservice.dto.EmployeeDto;
import recruitment.dev.employeeservice.service.EmployeeService;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR', 'MANAGER')")
    @GetMapping("/me")
    public ResponseEntity<EmployeeDto> getCurrentEmployee(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(employeeService.getEmployeeByKeycloakId(jwt.getSubject()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<EmployeeDto> createEmployee(   @Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto employee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id,
                                                      @Valid  @RequestBody EmployeeDto employeeDto) {
        EmployeeDto employee = employeeService.updateEmployee(employeeDto, id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/getall")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(hasRole(jwt, "ADMIN")
                ? employeeService.getAllEmployee(pageable)
                : employeeService.getAllEmployeeByCompany(requiredCompany(jwt), pageable));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
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

    @SuppressWarnings("unchecked")
    private boolean hasRole(Jwt jwt, String role) {
        if (jwt == null || jwt.getClaim("realm_access") == null) return false;
        Object roles = ((java.util.Map<String, Object>) jwt.getClaim("realm_access")).get("roles");
        return roles instanceof java.util.Collection<?> values && values.contains(role);
    }

}

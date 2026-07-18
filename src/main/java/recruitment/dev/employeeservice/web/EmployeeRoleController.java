package recruitment.dev.employeeservice.web;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import recruitment.dev.employeeservice.dto.EmployeeRoleDto;
import recruitment.dev.employeeservice.service.EmployeeRoleService;

@RestController
@RequestMapping("/employee-role")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
public class EmployeeRoleController {
    private final EmployeeRoleService employeeRoleService;

    @PostMapping("/create")
    public ResponseEntity<EmployeeRoleDto> createEmployeeRole(   @Valid @RequestBody EmployeeRoleDto employeeRoleDto) {
        EmployeeRoleDto role = employeeRoleService.createEmployeeRole(employeeRoleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeRoleDto> updateEmployeeRole(@PathVariable Long id,
                                                              @Valid @RequestBody EmployeeRoleDto employeeRoleDto) {
        return ResponseEntity.ok(employeeRoleService.updateEmployeeRole(employeeRoleDto, id));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EmployeeRoleDto> getEmployeeRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeRoleService.getEmployeeRoleById(id));
    }

    @GetMapping("/getall")
    public ResponseEntity<Page<EmployeeRoleDto>> getAllEmployeeRoles( @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(employeeRoleService.getAllEmployeeRole(Pageable.ofSize(size).withPage(page)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployeeRole(@PathVariable Long id) {
        employeeRoleService.deleteEmployeeRole(id);
        return ResponseEntity.noContent().build();
    }
}

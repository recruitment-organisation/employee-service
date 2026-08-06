package recruitment.dev.employeeservice.web;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import recruitment.dev.employeeservice.dto.DepartmentDto;
import recruitment.dev.employeeservice.service.DepartmentService;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasRole('HR')")
    @PostMapping("/create")
    public ResponseEntity<DepartmentDto> createDepartment(   @Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto department = departmentService.createDepartment(departmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(department);
    }

    @PreAuthorize("hasRole('HR')")
    @PutMapping("/update")
    public ResponseEntity<DepartmentDto> updateDepartment(   @Valid  @RequestBody DepartmentDto departmentDto) {
        return ResponseEntity.ok(departmentService.updateDepartment(departmentDto));
    }

    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @GetMapping("/getByName/{name}")
    public ResponseEntity<DepartmentDto> getDepartmentByName(@PathVariable String name) {
        return ResponseEntity.ok(departmentService.getDepartmentByName(name));
    }

    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @GetMapping("/getall")
    public ResponseEntity<Page<DepartmentDto>> getAllDepartments( @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(departmentService.getAllDepartments(Pageable.ofSize(size).withPage(page)));
    }

    @PreAuthorize("hasRole('HR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}

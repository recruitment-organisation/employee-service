package recruitment.dev.employeeservice.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import recruitment.dev.employeeservice.dto.*;
import recruitment.dev.employeeservice.service.CompanyService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompanyController {
    private final CompanyService service;

    @PostMapping("/companies") public ResponseEntity<CompanyDto> create(@Valid @RequestBody CompanyDto request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @GetMapping("/companies") public Page<CompanyDto> list(Pageable pageable) { return service.list(pageable); }
    @GetMapping("/companies/{id}") public CompanyDto get(@PathVariable Long id) { return service.get(id); }
    @PutMapping("/companies/{id}") public CompanyDto update(@PathVariable Long id, @Valid @RequestBody CompanyDto request) { return service.update(id, request); }
    @PatchMapping("/companies/{id}/status") public CompanyDto status(@PathVariable Long id, @Valid @RequestBody CompanyStatusRequest request) { return service.setStatus(id, request.active()); }
    @PostMapping("/companies/{id}/hr-profile") public ResponseEntity<EmployeeDto> createHrProfile(@PathVariable Long id, @Valid @RequestBody CreateHrProfileRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.createHrProfile(id, request)); }
    @GetMapping("/companies/{id}/hr") public Page<EmployeeDto> hr(@PathVariable Long id, Pageable pageable) { return service.listHr(id, pageable); }
    @PatchMapping("/hr/{id}/status") public EmployeeDto hrStatus(@PathVariable Long id, @Valid @RequestBody CompanyStatusRequest request) { return service.setEmployeeStatus(id, request.active()); }
    @GetMapping("/dashboard") public AdminDashboardStats dashboard() { return service.stats(); }
}

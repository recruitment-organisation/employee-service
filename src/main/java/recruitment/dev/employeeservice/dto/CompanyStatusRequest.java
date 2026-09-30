package recruitment.dev.employeeservice.dto;

import jakarta.validation.constraints.NotNull;
public record CompanyStatusRequest(@NotNull Boolean active) {}

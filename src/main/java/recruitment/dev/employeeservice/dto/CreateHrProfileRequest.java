package recruitment.dev.employeeservice.dto;

import jakarta.validation.constraints.*;
public record CreateHrProfileRequest(
        @NotBlank String keycloakId,
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 30) String phone,
        Boolean active
) {}

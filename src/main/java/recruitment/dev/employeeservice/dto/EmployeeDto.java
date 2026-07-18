package recruitment.dev.employeeservice.dto;


import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {


    private Long id;

    @NotBlank(message = "Keycloak Id is required")

    private String keycloakId;

    @NotBlank(message = "First name is required")

    private String firstName;

    @NotBlank(message = "Last name is required")

    private String lastName;


    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")

    private String phone;

    @NotNull(message = "Hire date is required")

    private LocalDate hireDate;

    @NotBlank(message = "Position is required")

    private String position;




    @NotNull(message = "Role id is required")
    private Long roleId;


    @NotNull(message = "Department id is required")
    private Long departmentId;

}

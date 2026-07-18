package recruitment.dev.employeeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeRoleDto {

    private Long id;
    @NotBlank(message = "Role name is required")


    private String name;


    private String description;

}

package recruitment.dev.employeeservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import recruitment.dev.employeeservice.enities.Employee;

import java.util.List;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {


    private Long id;


    private String name;



    @NotBlank(message = "Department name is required")

    private String description;




}

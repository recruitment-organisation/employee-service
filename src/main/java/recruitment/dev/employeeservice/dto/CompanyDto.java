package recruitment.dev.employeeservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CompanyDto {
    private Long id;
    @NotBlank @Size(max = 160) private String name;
    @Size(max = 180) private String slug;
    @Email @Size(max = 180) private String email;
    @Size(max = 30) private String phone;
    @Size(max = 500) private String address;
    @Size(max = 500) private String website;
    @Size(max = 1000) private String logoUrl;
    @Size(max = 3000) private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private long hrCount;
}

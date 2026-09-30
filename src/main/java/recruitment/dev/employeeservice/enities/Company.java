package recruitment.dev.employeeservice.enities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 160) private String name;
    @Column(nullable = false, unique = true, length = 180) private String slug;
    @Column(length = 180) private String email;
    @Column(length = 30) private String phone;
    @Column(length = 500) private String address;
    @Column(length = 500) private String website;
    @Column(name = "logo_url", length = 1000) private String logoUrl;
    @Column(length = 3000) private String description;
    @Column(nullable = false) @Builder.Default private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @OneToMany(mappedBy = "company") @Builder.Default private List<Employee> employees = new ArrayList<>();

    @PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
}

package recruitment.dev.employeeservice.enities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String keycloakId;


    private String firstName;


    private String lastName;


    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;


    private LocalDate hireDate;


    private String position;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private EmployeeRole role;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

}
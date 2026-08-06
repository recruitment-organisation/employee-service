package recruitment.dev.employeeservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import recruitment.dev.employeeservice.enities.Department;
import recruitment.dev.employeeservice.enities.EmployeeRole;
import recruitment.dev.employeeservice.repositories.DepartmentRepository;
import recruitment.dev.employeeservice.repositories.EmployeeRoleRepository;

@SpringBootApplication
public class EmployeeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }



    CommandLineRunner initDatabase(
            DepartmentRepository departmentRepository,
            EmployeeRoleRepository roleRepository
    ) {

        return args -> {


            // Departments

            createDepartment(
                    departmentRepository,
                    "DEV",
                    "Dev Department"
            );


            createDepartment(
                    departmentRepository,
                    "RH",
                    "Human Resources Department"
            );
            createDepartment(
                    departmentRepository,
                    "DS ",
                    " DATA Department"
            );


            // Roles

            createRole(
                    roleRepository,
                    "ADMIN",
                    "System administrator"
            );


            createRole(
                    roleRepository,
                    "HR",
                    "Human resources manager"
            );


            createRole(
                    roleRepository,
                    "DevOps",
                    "DevOps specialist"
            );


            createRole(
                    roleRepository,
                    "DEVELOPER",
                    "Software developer"
            );


            createRole(
                    roleRepository,
                    "MANAGER",
                    "Department manager"
            );

            createRole(
                    roleRepository,
                    "CA",
                    "Cloud Architect"
            );


        };
    }



    private void createDepartment(
            DepartmentRepository repository,
            String name,
            String description
    ){

        if(repository.findByName(name) == null){

            Department department = new Department();

            department.setName(name);
            department.setDescription(description);

            repository.save(department);
        }

    }



    private void createRole(
            EmployeeRoleRepository repository,
            String name,
            String description
    ){

        if(repository.findByName(name) == null){

            EmployeeRole role = new EmployeeRole();

            role.setName(name);
            role.setDescription(description);

            repository.save(role);
        }

    }
}

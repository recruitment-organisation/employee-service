package recruitment.dev.employeeservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import recruitment.dev.employeeservice.dto.DepartmentDto;
import recruitment.dev.employeeservice.enities.Department;
import recruitment.dev.employeeservice.mapper.DepartmentMapper;
import recruitment.dev.employeeservice.repositories.DepartmentRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock private DepartmentRepository departmentRepository;
    @Mock private DepartmentMapper departmentMapper;
    @InjectMocks private DepartmentServiceImpl service;

    @Test
    void createsNewDepartment() {
        DepartmentDto request = DepartmentDto.builder().name("Engineering").description("Product team").build();
        Department department = new Department();
        DepartmentDto expected = DepartmentDto.builder().id(1L).name("Engineering").build();
        when(departmentRepository.findByName("Engineering")).thenReturn(Optional.empty());
        when(departmentMapper.toDepartment(request)).thenReturn(department);
        when(departmentMapper.toDepartmentDto(department)).thenReturn(expected);

        assertThat(service.createDepartment(request)).isSameAs(expected);
        verify(departmentRepository).save(department);
    }

    @Test
    void rejectsDepartmentWithExistingName() {
        DepartmentDto request = DepartmentDto.builder().name("Engineering").build();
        when(departmentRepository.findByName("Engineering")).thenReturn(Optional.of(new Department()));

        assertThatThrownBy(() -> service.createDepartment(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updatesAndDeletesExistingDepartment() {
        Department department = new Department();
        department.setName("Old name");
        DepartmentDto update = DepartmentDto.builder().id(3L).name("New name").description("Updated").build();
        when(departmentRepository.findById(3L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDepartmentDto(department)).thenReturn(update);

        assertThat(service.updateDepartment(update)).isSameAs(update);
        assertThat(department.getName()).isEqualTo("New name");
        assertThat(department.getDescription()).isEqualTo("Updated");
        service.deleteDepartment(3L);
        verify(departmentRepository, times(2)).findById(3L);
        verify(departmentRepository).delete(department);
    }

    @Test
    void reportsMissingDepartment() {
        when(departmentRepository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDepartmentById(44L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}

package com.workmotion.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.workmotion.api.EmployeeDtoTestInterface;
import com.workmotion.api.EmployeeTestInterface;
import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.model.persistence.Employee;
import com.workmotion.api.repository.EmployeeRepository;
import com.workmotion.api.statemachine.States;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeServiceTest implements EmployeeTestInterface, EmployeeDtoTestInterface
{
    @InjectMocks
    EmployeeService employeeService;

    @Mock
    EmployeeRepository employeeRepository;

    @BeforeEach
    public void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getByIdTest()
    {
        Employee employee = this.getEmployee(143, "name1", "email1", "description1", null);

        when(employeeRepository.findById(Long.valueOf(143))).thenReturn(Optional.ofNullable(employee));

        Optional<EmployeeDto> employeeDto = employeeService.getById(143);
        assertThat(employeeDto.isPresent()).isTrue();
        checkEmployee(employee, employeeDto.get());
    }

    @Test
    public void getByIdNotFoundTest()
    {
        Employee employee = null;
        when(employeeRepository.findById(Long.valueOf(143))).thenReturn(Optional.ofNullable(employee));

        Optional<EmployeeDto> employeeDto = employeeService.getById(143);
        assertThat(employeeDto.isEmpty()).isTrue();
    }

    @Test
    public void getAllEmployeesTest()
    {
        List<Employee> mockedList = new ArrayList<>();
        Employee employee1 = this.getEmployee(143, "name1", "email1", "desc1", null);
        Employee employee2 = this.getEmployee(276, "name2", "email2", "desc2", null);
        Employee employee3 = this.getEmployee(389, "name3", "email3", "desc3", States.APPROVED.toString());
        mockedList.add(employee1);
        mockedList.add(employee2);
        mockedList.add(employee3);

        when(employeeRepository.findAll()).thenReturn(mockedList);

        List<EmployeeDto> employees = employeeService.getAll();

        assertThat(employees.size()).isEqualTo(3);
        checkEmployee(employee1, employees.get(0));
        checkEmployee(employee2, employees.get(1));
        checkEmployee(employee3, employees.get(2));
    }

    @SneakyThrows
    @Test
    public void saveNewEmployeeTest()
    {
        Employee employee = getEmployee(765, "name1", "email1", "desc", null);
        EmployeeDto employeeDto = getEmployeeDto(765, "name1", "email1", "desc", States.ADDED);

        // Only mock the repository save action in the "create" scenario
        // (the getById method should be mocked only in the update scenario)
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto savedEmployeeDto = employeeService.save(employeeDto);

        checkEmployee(employee, savedEmployeeDto);
    }

    @SneakyThrows
    @Test
    public void saveExistingEmployeeTest()
    {
        Employee beforeChangesEmployee = getEmployee(765, "name1", "email1", "desc", null);
        Employee afterChangesEmployee = getEmployee(765, "name1 edited", "email1 edited", "desc", null);
        EmployeeDto employeeDto = getEmployeeDto(765, "name1 edited", "email1 edited", "desc", States.ADDED);

        when(employeeRepository.getById(beforeChangesEmployee.getId())).thenReturn(beforeChangesEmployee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(afterChangesEmployee);

        EmployeeDto savedEmployeeDto = employeeService.save(employeeDto);

        checkEmployee(afterChangesEmployee, savedEmployeeDto);
    }

    private void checkEmployee(Employee employee, EmployeeDto employeeDto)
    {
        assertThat(employeeDto.getId()).isEqualTo(employee.getId());
        assertThat(employeeDto.getName()).isEqualTo(employee.getName());
        assertThat(employeeDto.getEmail()).isEqualTo(employee.getEmail());
        assertThat(employeeDto.getContractDescription()).isEqualTo(employee.getContractDescription());
        assertThat(employeeDto.getState().toString()).isEqualTo(employee.getState());
        assertThat(employeeDto.getState()).isNotNull();
    }
}

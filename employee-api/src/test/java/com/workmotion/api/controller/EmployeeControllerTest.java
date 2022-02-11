package com.workmotion.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.workmotion.api.EmployeeDtoTestInterface;
import com.workmotion.api.exception.InvalidState;
import com.workmotion.api.exception.StateNotAccepted;
import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.model.dto.request.CreateEmployeeRequest;
import com.workmotion.api.model.dto.request.UpdateEmployeeStateRequest;
import com.workmotion.api.model.dto.response.UpdateEmployeeStateResponse;
import com.workmotion.api.service.EmployeeService;
import com.workmotion.api.statemachine.SecurityChecks;
import com.workmotion.api.statemachine.States;
import com.workmotion.api.statemachine.WorkPermitChecks;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest implements EmployeeDtoTestInterface
{
    @InjectMocks
    EmployeeController employeeController;

    @Mock
    EmployeeService employeeService;

    @AfterEach
    public void resetMocks()
    {
        Mockito.reset(employeeService);
        States.IN_CHECK.reset();
    }

    @Test
    public void testGetEmployee()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeDto employeeDto = getEmployeeDto(189, "test", "email", "description", States.ADDED);

        when(employeeService.getById(189L)).thenReturn(Optional.ofNullable(employeeDto));

        // Assertions
        ResponseEntity<EmployeeDto> responseEntity = employeeController.getEmployee(189L);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().toString()).isEqualTo(employeeDto.toString());
    }

    @Test
    public void testGetEmployeeNotFound()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeDto employeeDto = null;

        when(employeeService.getById(anyLong())).thenReturn(Optional.ofNullable(employeeDto));

        // Assertions
        ResponseEntity<EmployeeDto> responseEntity = employeeController.getEmployee(101L);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testGetEmployees()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeDto employeeDto1 = getEmployeeDto(101L, "test", "email", "desc", States.ADDED);
        EmployeeDto employeeDto2 = getEmployeeDto(202L, "test2", "email2", "desc2", States.ADDED);

        List<EmployeeDto> employees = new ArrayList<>();
        employees.add(employeeDto1);
        employees.add(employeeDto2);

        when(employeeService.getAll()).thenReturn(employees);

        // Assertions
        ResponseEntity<List<EmployeeDto>> responseEntity = employeeController.getEmployees();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().toString()).isEqualTo(employees.toString());
    }

    @SneakyThrows
    @Test
    public void testCreateEmployee()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest()
            .setName("name3")
            .setEmail("email3")
            .setContractDescription("desc3");

        EmployeeDto employeeDto = getEmployeeDto(303, "name3", "email3", "desc3", States.ADDED);

        when(employeeService.save(any(EmployeeDto.class))).thenReturn(employeeDto);

        // Assertions
        ResponseEntity<EmployeeDto> responseEntity = employeeController.createEmployee(createEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
        assertThat(responseEntity.getBody().toString()).isEqualTo(employeeDto.toString());
    }

    @SneakyThrows
    @Test
    public void testCreateEmployeeGeneralError()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest()
            .setName("name3")
            .setEmail("email3")
            .setContractDescription("desc3");

        when(employeeService.save(any(EmployeeDto.class))).thenThrow(RuntimeException.class);

        // Assertions
        ResponseEntity<EmployeeDto> responseEntity = employeeController.createEmployee(createEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
    }

    @SneakyThrows
    @Test
    public void testUpdateEmployeeState()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UpdateEmployeeStateRequest updateEmployeeRequest = new UpdateEmployeeStateRequest()
            .setId(423)
            .setState(States.IN_CHECK.toString());

        EmployeeDto employeeDto = getEmployeeDto(423, "name4", "email4", "desc4", States.getFromString("ADDED"));

        when(employeeService.getById(anyLong())).thenReturn(Optional.ofNullable(employeeDto));
        when(employeeService.updateState(any(EmployeeDto.class), anyString())).thenReturn(true);

        // Assertions
        ResponseEntity<UpdateEmployeeStateResponse> responseEntity =
                employeeController.updateEmployeeState(updateEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("State updated.");
        assertThat(responseEntity.getBody().getNextStates()).isEqualTo(
            Arrays.asList(SecurityChecks.SECURITY_CHECK_STARTED, WorkPermitChecks.WORK_PERMIT_CHECK_STARTED)
        );
    }

    @Test
    public void testUpdateEmployeeStateNotFound()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UpdateEmployeeStateRequest updateEmployeeRequest = new UpdateEmployeeStateRequest()
            .setId(594)
            .setState(States.ADDED.toString());

        when(employeeService.getById(anyLong())).thenReturn(Optional.ofNullable(null));

        // Assertions
        ResponseEntity<UpdateEmployeeStateResponse> responseEntity =
                employeeController.updateEmployeeState(updateEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

    @SneakyThrows
    @Test
    public void testUpdateEmployeeStateWrongStateError()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UpdateEmployeeStateRequest updateEmployeeRequest = new UpdateEmployeeStateRequest()
                .setId(687)
                .setState("WRONG_STATE");

        EmployeeDto employeeDto = getEmployeeDto(687, "name6", "email6", "desc6", States.ADDED);

        when(employeeService.getById(687L)).thenReturn(Optional.ofNullable(employeeDto));
        when(employeeService.updateState(employeeDto, "WRONG_STATE"))
                .thenThrow(new InvalidState("WRONG_STATE", employeeDto.getNextStates()));

        // Assertions
        ResponseEntity<UpdateEmployeeStateResponse> responseEntity =
                employeeController.updateEmployeeState(updateEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("The given state is not valid.");
        assertThat(responseEntity.getBody().getNextStates()).isEqualTo(Arrays.asList(States.IN_CHECK));
    }

    @SneakyThrows
    @Test
    public void testUpdateEmployeeStateNotNextStateError()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UpdateEmployeeStateRequest updateEmployeeRequest = new UpdateEmployeeStateRequest()
                .setId(687)
                .setState("APPROVED");

        EmployeeDto employeeDto = getEmployeeDto(687, "name6", "email6", "desc6", States.ADDED);

        when(employeeService.getById(687L)).thenReturn(Optional.ofNullable(employeeDto));
        when(employeeService.updateState(employeeDto, updateEmployeeRequest.getState()))
                .thenThrow(new StateNotAccepted(updateEmployeeRequest.getState(), employeeDto.getNextStates()));

        // Assertions
        ResponseEntity<UpdateEmployeeStateResponse> responseEntity =
                employeeController.updateEmployeeState(updateEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("The given state is not accepted as a possible next state.");
        assertThat(responseEntity.getBody().getNextStates()).isEqualTo(Arrays.asList(States.IN_CHECK));
    }

    @SneakyThrows
    @Test
    public void testUpdateEmployeeStateGeneralError()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UpdateEmployeeStateRequest updateEmployeeRequest = new UpdateEmployeeStateRequest()
            .setId(687)
            .setState(States.IN_CHECK.toString());

        EmployeeDto employeeDto = getEmployeeDto(687, "name6", "email6", "desc6", States.ADDED);

        when(employeeService.getById(687L)).thenReturn(Optional.ofNullable(employeeDto));
        when(employeeService.updateState(employeeDto, updateEmployeeRequest.getState()))
                .thenThrow(RuntimeException.class);

        // Assertions
        ResponseEntity<UpdateEmployeeStateResponse> responseEntity =
                employeeController.updateEmployeeState(updateEmployeeRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("A general error occurred.");
        assertThat(responseEntity.getBody().getNextStates()).isEqualTo(new ArrayList<>());
    }

    @Test
    public void testDeleteEmployee()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeDto employeeDto = getEmployeeDto(743, "name7", "email7", "desc7", States.ADDED);

        // No need to mock EmployerService::delete as it returns void (not mocked will fall into the successful case)
        when(employeeService.getById(743L)).thenReturn(Optional.ofNullable(employeeDto));

        // Assertions
        ResponseEntity<HttpStatus> responseEntity = employeeController.deleteEmployee(employeeDto.getId());
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    public void testDeleteEmployeeNotFound()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeDto employeeDto = getEmployeeDto(827, "name8", "email8", "desc8", States.ADDED);

        when(employeeService.getById(anyLong())).thenReturn(Optional.ofNullable(null));

        // Assertions
        ResponseEntity<HttpStatus> responseEntity = employeeController.deleteEmployee(employeeDto.getId());
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testDeleteEmployeeGeneralError()
    {
        // Configure mock objects and methods
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EmployeeDto employeeDto = getEmployeeDto(943, "name9", "email9", "desc9", States.ADDED);

        when(employeeService.getById(943L)).thenReturn(Optional.ofNullable(employeeDto));
        when(employeeService.delete(employeeDto)).thenThrow(RuntimeException.class);

        // Assertions
        ResponseEntity<HttpStatus> responseEntity = employeeController.deleteEmployee(employeeDto.getId());
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
    }
}

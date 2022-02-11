package com.workmotion.api.controller;

import com.workmotion.api.exception.DataSetNotFound;
import com.workmotion.api.exception.StateNotAccepted;
import com.workmotion.api.exception.InvalidState;
import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.model.dto.response.UpdateEmployeeStateResponse;
import com.workmotion.api.model.dto.request.CreateEmployeeRequest;
import com.workmotion.api.model.dto.request.UpdateEmployeeStateRequest;
import com.workmotion.api.service.EmployeeServiceInterface;
import com.workmotion.api.statemachine.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController
{
    @Autowired
    private final EmployeeServiceInterface employeeServiceInterface;

    public EmployeeController(EmployeeServiceInterface employeeServiceInterface)
    {
        this.employeeServiceInterface = employeeServiceInterface;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable("id") long id)
    {
        Optional<EmployeeDto> employee = employeeServiceInterface.getById(id);
        if (employee.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employee.get(), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<EmployeeDto>> getEmployees()
    {
        return new ResponseEntity<>(employeeServiceInterface.getAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody CreateEmployeeRequest employeeRequest)
    {
        try {
            //TODO validations the request object; alternatively implement validation in
            // service class with an appropriate set of exceptions to be caught in the controller

            // Convert the request to a EmployeeDto instance, which can be used to communicate with the service layer
            EmployeeDto employeeDto = new EmployeeDto()
                .setName(employeeRequest.getName())
                .setEmail(employeeRequest.getEmail())
                .setContractDescription(employeeRequest.getContractDescription())
            ;

            return new ResponseEntity<>(employeeServiceInterface.save(employeeDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(
                String.format(
                    "Error while creating a new employee. Request: %s. Message: %s. Trace: %s ",
                    employeeRequest,
                    e.getMessage(),
                    e.getStackTrace()
                )
            );
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/state")
    public ResponseEntity<UpdateEmployeeStateResponse> updateEmployeeState(@RequestBody UpdateEmployeeStateRequest stateRequest)
    {
        try {
            Optional<EmployeeDto> employeeDto = employeeServiceInterface.getById((long) stateRequest.getId());
            if (employeeDto.isEmpty()) {
                throw new DataSetNotFound();
            }

            // The given state is one of the possible/accepted states and can be updated
            // the current employee state with the one given in the request;
            employeeDto.get().setState(States.getFromString(stateRequest.getState()));

            // Save the changes in the data layer
            employeeServiceInterface.updateState(employeeDto.get(), stateRequest.getState());

            return new ResponseEntity<>(
                new UpdateEmployeeStateResponse("State updated.", employeeDto.get().getNextStates()),
                HttpStatus.OK
            );
        } catch (DataSetNotFound dataSetNotFound) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidState invalidState) {
            return new ResponseEntity<>(
                    new UpdateEmployeeStateResponse(
                            "The given state is not valid.",
                            invalidState.getNextStates()
                    ),
                    HttpStatus.BAD_REQUEST
            );
        } catch (StateNotAccepted stateNotAccepted) {
            return new ResponseEntity<>(
                new UpdateEmployeeStateResponse(
                    "The given state is not accepted as a possible next state.",
                    stateNotAccepted.getNextStates()
                ),
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error(
                String.format(
                    "Error while updating the employee state. Request: %s. Message: %s. Trace: %s.",
                    stateRequest,
                    e.getMessage(),
                    e.getStackTrace()
                )
            );
            return new ResponseEntity<>(
                new UpdateEmployeeStateResponse("A general error occurred.", new ArrayList<>()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable("id") long id)
    {
        try {
            Optional<EmployeeDto> employee = employeeServiceInterface.getById(id);
            if (employee.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            employeeServiceInterface.delete(employee.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error(
                String.format(
                    "Error while deleting the employee with %d. Message: %s. Trace: %s",
                    id,
                    e.getMessage(),
                    e.getStackTrace()
                )
            );
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}

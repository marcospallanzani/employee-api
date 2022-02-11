package com.workmotion.api.service;

import com.workmotion.api.exception.ActionNotAllowed;
import com.workmotion.api.exception.DataSetNotFound;
import com.workmotion.api.exception.InvalidState;
import com.workmotion.api.exception.StateNotAccepted;
import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.model.mapper.EmployeeMapper;
import com.workmotion.api.model.persistence.Employee;
import com.workmotion.api.repository.EmployeeRepository;
import com.workmotion.api.statemachine.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService implements EmployeeServiceInterface
{
    @Autowired
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository)
    {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<EmployeeDto> getById(long id)
    {
        return employeeRepository.findById(id).map(EmployeeMapper::persistenceToDto);
    }

    @Override
    public List<EmployeeDto> getAll()
    {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        for (Employee employee: employees) {
            employeeDtos.add(EmployeeMapper.persistenceToDto(employee));
        }

        return employeeDtos;
    }

    @Override
    public EmployeeDto save(EmployeeDto employeeDto) throws ActionNotAllowed
    {
        Optional<Employee> employeePersistence = employeeRepository.findById(employeeDto.getId());
        if (employeePersistence.isEmpty()) {
            employeePersistence = Optional.of(new Employee());
        }

        // Check if the current state is different than the one in the DTO object.
        // If so, check if the new state respects the state-machine logic
        if (employeeDto.getState() != null
                && employeeDto.getState().toPersistenceString() != employeePersistence.get().getState()) {
            throw new ActionNotAllowed("Impossible to update the state field.");
        }

        // Map data to the persistence object
        Employee updateEmployee = EmployeeMapper.dtoToPersistence(employeeDto, employeePersistence.get());

        // Save all changes (if id is null, a new entry will be created in the persistence data layer
        updateEmployee = employeeRepository.save(updateEmployee);

        // Map the persistence updated object to a DTO and return it
        return EmployeeMapper.persistenceToDto(updateEmployee);
    }

    @Override
    public boolean updateState(EmployeeDto employeeDto, String newState)
            throws StateNotAccepted, InvalidState, DataSetNotFound
    {
        // Check if the given employee exists
        Optional<Employee> employeePersistence = employeeRepository.findById(employeeDto.getId());
        if (employeePersistence.isEmpty()) {
            throw new DataSetNotFound();
        }

        // Check if the new state is valid
        if (newState != null && !States.isValidState(newState)) {
            throw new InvalidState(newState, employeeDto.getNextStates());
        }

        // Check if the new state is one of the possible next states for the current state machine
        if (employeeDto.getState() != null && !employeeDto.getState().isNextState(newState)) {
            throw new StateNotAccepted(newState, employeeDto.getNextStates());
        }

        // Change the state in the persistence object and save changes
        employeeRepository.save(employeePersistence.get().setState(employeeDto.getState().toPersistenceString()));

        return true;
    }


    @Override
    public boolean delete(EmployeeDto employeeDto)
    {
        //TODO consider changing the return type to void
        //TODO check if the repository fails in case of a given non-existent employee
        employeeRepository.deleteById(employeeDto.getId());

        return true;
    }
}
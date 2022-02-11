package com.workmotion.api.model.mapper;

import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.model.persistence.Employee;
import com.workmotion.api.statemachine.States;

public class EmployeeMapper
{
    public static EmployeeDto persistenceToDto(Employee employee)
    {
        return EmployeeMapper.persistenceToDto(employee, new EmployeeDto());
    }

    public static EmployeeDto persistenceToDto(Employee employee, EmployeeDto employeeDto)
    {
        return employeeDto
            .setId(employee.getId())
            .setName(employee.getName())
            .setEmail(employee.getEmail())
            .setContractDescription(employee.getContractDescription())
            .setState(States.getFromString(employee.getState()))
        ;
    }

    public static Employee dtoToPersistence(EmployeeDto employeeDto)
    {
        return EmployeeMapper.dtoToPersistence(employeeDto, new Employee());
    }

    public static Employee dtoToPersistence(EmployeeDto employeeDto, Employee employee)
    {
        if (employeeDto.getName() != null) {
            employee.setName(employeeDto.getName());
        }

        if (employeeDto.getEmail() != null) {
            employee.setEmail(employeeDto.getEmail());
        }

        if (employeeDto.getState() != null) {
            employee.setState(employeeDto.getState().toPersistenceString());
        }

        return employee.setContractDescription(employeeDto.getContractDescription());
    }
}

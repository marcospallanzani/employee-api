package com.workmotion.api;

import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.statemachine.States;

public interface EmployeeDtoTestInterface
{
    default EmployeeDto getEmployeeDto(long id, String name, String email, String description, States states)
    {
        return new EmployeeDto()
            .setId(id)
            .setName(name)
            .setEmail(email)
            .setContractDescription(description)
            .setState(states)
        ;
    }
}

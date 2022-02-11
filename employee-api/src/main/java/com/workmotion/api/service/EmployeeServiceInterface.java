package com.workmotion.api.service;

import com.workmotion.api.exception.ActionNotAllowed;
import com.workmotion.api.exception.DataSetNotFound;
import com.workmotion.api.exception.InvalidState;
import com.workmotion.api.exception.StateNotAccepted;
import com.workmotion.api.model.dto.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface EmployeeServiceInterface
{
    Optional<EmployeeDto> getById(long id);

    List<EmployeeDto> getAll();

    EmployeeDto save(EmployeeDto employeeDto) throws ActionNotAllowed;

    boolean updateState(EmployeeDto employeeDto, String newState) throws StateNotAccepted, InvalidState, DataSetNotFound;

    boolean delete(EmployeeDto employeeDto);
}

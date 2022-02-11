package com.workmotion.api.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.workmotion.api.model.dto.EmployeeDto;
import com.workmotion.api.model.persistence.Employee;
import com.workmotion.api.statemachine.States;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

class EmployeeMapperTest
{
    @ParameterizedTest(name = "{index} => name={0}, name={1}, description={2}, state={3}, expectedState={4}")
    @ArgumentsSource(CustomArgumentProvider.class)
    void persistenceToDtoTest(String name, String email, String description, String state, String expectedState)
    {
        Employee employee = (new Employee())
            .setName(name)
            .setEmail(email)
            .setContractDescription(description)
        ;

        if (state != null) {
            employee.setState(state);
        }

        EmployeeDto employeeDto = EmployeeMapper.persistenceToDto(employee);

        assertThat(employeeDto.getName()).isEqualTo(name);
        assertThat(employeeDto.getEmail()).isEqualTo(email);
        assertThat(employeeDto.getContractDescription()).isEqualTo(description);
        assertThat(employeeDto.getState().toString()).isEqualTo(expectedState);
    }

    @ParameterizedTest(name = "{index} => name={0}, email={1}, description={2}, state={3}, expectedState={4}")
    @ArgumentsSource(CustomArgumentProvider.class)
    void dtoToPersistenceTest(String name, String email, String description, String state, String expectedState)
    {
        EmployeeDto employeeDto = (new EmployeeDto())
            .setName(name)
            .setEmail(email)
            .setContractDescription(description)
            .setState(States.getFromString(state))
        ;

        Employee employee = EmployeeMapper.dtoToPersistence(employeeDto);

        assertThat(employee.getName()).isEqualTo(name);
        assertThat(employee.getEmail()).isEqualTo(email);
        assertThat(employee.getContractDescription()).isEqualTo(description);
        assertThat(employee.getState()).isEqualTo(expectedState);
    }

    static class CustomArgumentProvider implements ArgumentsProvider
    {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context)
        {
            return Stream.of(
                Arguments.of("name1", "email1", "description1", States.APPROVED.toString(), States.APPROVED.toString()),
                Arguments.of("name2", "email2", null, States.APPROVED.toString(), States.APPROVED.toString()),
                Arguments.of("name3", "email3", "description3", null, States.ADDED.toString())
            );
        }
    }
}
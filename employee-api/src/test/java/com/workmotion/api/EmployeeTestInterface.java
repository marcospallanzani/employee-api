package com.workmotion.api;

import com.workmotion.api.model.persistence.Employee;

import java.lang.reflect.Field;

public interface EmployeeTestInterface
{
    default Employee getEmployee(long id, String name, String email, String description, String state)
    {
        Employee employee = (new Employee())
                .setName(name)
                .setEmail(email)
                .setContractDescription(description);

        if (state != null) {
            employee.setState(state);
        }

        try {
            Field privateField = Employee.class.getDeclaredField("id");
            privateField.setAccessible(true);
            privateField.set(employee, id);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return employee;
    }
}

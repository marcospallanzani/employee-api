package com.workmotion.api.model.dto;

import com.workmotion.api.statemachine.States;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDto
{
    private long id = 0;

    private String name;

    private String email;

    private String contractDescription;

    private States state;

    public long getId()
    {
        return id;
    }

    public EmployeeDto setId(long id)
    {
        this.id = id;

        return this;
    }

    public String getName()
    {
        return name;
    }

    public EmployeeDto setName(String name)
    {
        this.name = name;

        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public EmployeeDto setEmail(String email)
    {
        this.email = email;

        return this;
    }

    public String getContractDescription()
    {
        return contractDescription;
    }

    public EmployeeDto setContractDescription(String contractDescription)
    {
        this.contractDescription = contractDescription;

        return this;
    }

    public States getState()
    {
        return state;
    }

    public EmployeeDto setState(States state)
    {
        this.state = state;

        return this;
    }

    public List<Enum> getNextStates()
    {
        if (this.state != null) {
            return this.state.nextStates();
        }
        return new ArrayList<>();
    }

    @Override
    public String toString()
    {
        return "EmployeeDto{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", email='" + this.email + '\'' +
                ", contractDescription='" + this.contractDescription + '\'' +
                ", state=" + this.state.toString() +
                '}';
    }
}

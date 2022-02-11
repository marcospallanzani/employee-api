package com.workmotion.api.model.persistence;

import com.workmotion.api.statemachine.States;

import javax.persistence.*;

@Entity
@Table(name = "employees")
public class Employee
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name", nullable=false)
    private String name;

    @Column(name = "email", nullable=false)
    private String email;

    @Column(name = "contractDescription")
    private String contractDescription;

    @Column(name = "state", nullable=false)
    private String state;

    public Employee()
    {
        this.state = States.ADDED.toString();
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Employee setName(String name)
    {
        this.name = name;

        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public Employee setEmail(String email)
    {
        this.email = email;

        return this;
    }

    public String getContractDescription()
    {
        return contractDescription;
    }

    public Employee setContractDescription(String contractDescription)
    {
        this.contractDescription = contractDescription;

        return this;
    }

    public String getState()
    {
        return state;
    }

    public Employee setState(String state)
    {
        this.state = state;

        return this;
    }

    @Override
    public String toString()
    {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", contractDescription='" + contractDescription + '\'' +
                ", state=" + state +
                '}';
    }
}

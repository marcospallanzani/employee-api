package com.workmotion.api.model.dto.request;

public class CreateEmployeeRequest
{
    protected String name;

    protected String email;

    protected String contractDescription;

    public String getName()
    {
        return name;
    }

    public CreateEmployeeRequest setName(String name)
    {
        this.name = name;

        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public CreateEmployeeRequest setEmail(String email)
    {
        this.email = email;

        return this;
    }

    public String getContractDescription()
    {
        return contractDescription;
    }

    public CreateEmployeeRequest setContractDescription(String contractDescription)
    {
        this.contractDescription = contractDescription;

        return this;
    }

    @Override
    public String toString()
    {
        return "CreateEmployeeRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", contractDescription='" + contractDescription + '\'' +
                '}';
    }
}

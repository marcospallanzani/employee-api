package com.workmotion.api.model.dto.request;

public class UpdateEmployeeStateRequest
{
    private float id;

    private String state;

    public float getId()
    {
        return id;
    }

    public UpdateEmployeeStateRequest setId(float id)
    {
        this.id = id;

        return this;
    }

    public String getState()
    {
        return state;
    }

    public UpdateEmployeeStateRequest setState(String state)
    {
        this.state = state;

        return this;
    }

    @Override
    public String toString()
    {
        return "UpdateEmployeeStateRequest{" +
                "id=" + id +
                ", state='" + state + '\'' +
                '}';
    }
}

package com.workmotion.api.model.dto.response;

import java.util.ArrayList;
import java.util.List;

public class UpdateEmployeeStateResponse
{
    private String message;

    private List<Enum> nextStates = new ArrayList<>();

    public UpdateEmployeeStateResponse(String message, List<Enum> nextStates)
    {
        this.message = message;
        this.nextStates = nextStates;
    }

    public String getMessage()
    {
        return message;
    }

    public UpdateEmployeeStateResponse setMessage(String message)
    {
        this.message = message;

        return this;
    }

    public List<Enum> getNextStates()
    {
        return nextStates;
    }

    public UpdateEmployeeStateResponse setNextStates(List<Enum> nextStates)
    {
        this.nextStates = nextStates;

        return this;
    }
}

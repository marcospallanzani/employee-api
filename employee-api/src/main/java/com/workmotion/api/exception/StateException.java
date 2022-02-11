package com.workmotion.api.exception;

import java.util.List;

public class StateException extends Exception
{
    private String state;

    private List<Enum> nextStates;

    public StateException(String message, String state, List<Enum> nextStates)
    {
        super(message);

        this.state = state;
        this.nextStates = nextStates;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public List<Enum> getNextStates()
    {
        return nextStates;
    }

    public void setNextStates(List<Enum> nextStates)
    {
        this.nextStates = nextStates;
    }
}

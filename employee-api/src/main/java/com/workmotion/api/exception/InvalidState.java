package com.workmotion.api.exception;

import java.util.List;

public class InvalidState extends StateException
{
    public InvalidState(String state, List<Enum> nextStates)
    {
        super("Invalid state", state, nextStates);
    }
}

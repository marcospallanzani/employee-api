package com.workmotion.api.exception;

import java.util.List;

public class StateNotAccepted extends StateException
{
    public StateNotAccepted(String state, List<Enum> nextStates)
    {
        super("State not accepted", state, nextStates);
    }
}

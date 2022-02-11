package com.workmotion.api.statemachine;

import java.util.List;

public interface NextStateMachineInterface
{
    /**
     * Return a list of all possible next states.
     *
     * @return List<Enum>
     */
    public List<Enum> nextStates();
}

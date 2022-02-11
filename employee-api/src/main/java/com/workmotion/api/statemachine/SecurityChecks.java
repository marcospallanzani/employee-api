package com.workmotion.api.statemachine;

import java.util.Arrays;
import java.util.List;

public enum SecurityChecks implements NextStateMachineInterface {

    SECURITY_CHECK_STARTED {
        @Override
        public List<Enum> nextStates() {
            return Arrays.asList(SECURITY_CHECK_FINISHED);
        }
    },
    SECURITY_CHECK_FINISHED {
        @Override
        public List<Enum> nextStates() {
            return Arrays.asList(SECURITY_CHECK_FINISHED);
        }
    };
}

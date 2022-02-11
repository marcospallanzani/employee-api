package com.workmotion.api.statemachine;

import java.util.Arrays;
import java.util.List;

public enum WorkPermitChecks implements NextStateMachineInterface {

    WORK_PERMIT_CHECK_STARTED {
        @Override
        public List<Enum> nextStates() {
            return Arrays.asList(WORK_PERMIT_CHECK_FINISHED);
        }
    },
    WORK_PERMIT_CHECK_FINISHED {
        @Override
        public List<Enum> nextStates() {
            return Arrays.asList(WORK_PERMIT_CHECK_FINISHED);
        }
    };
}

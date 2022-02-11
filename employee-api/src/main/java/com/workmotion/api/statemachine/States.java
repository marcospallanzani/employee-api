package com.workmotion.api.statemachine;

import org.apache.commons.lang3.EnumUtils;

import java.util.*;

public enum States implements NextStateMachineInterface
{
    ADDED {
        @Override
        public String toPersistenceString()
        {
            return this.toString();
        }

        @Override
        public void addChildren(List<Enum> children) {}

        @Override
        public void reset() {}

        @Override
        public List<Enum> nextStates()
        {
            return Arrays.asList(IN_CHECK);
        }
    },
    IN_CHECK {
        private SecurityChecks securityChecks;

        private WorkPermitChecks workPermitChecks;

        @Override
        public void addChildren(List<Enum> children)
        {
            reset();

            for (Enum child : children) {
                if (child instanceof SecurityChecks) {
                    securityChecks = (SecurityChecks) child;
                } else if (child instanceof WorkPermitChecks) {
                    workPermitChecks = (WorkPermitChecks) child;
                }
            }
        }

        @Override
        public List<Enum> nextStates()
        {
            if (securityChecks != null
                    && securityChecks.toString() == SecurityChecks.SECURITY_CHECK_FINISHED.toString()
                    && workPermitChecks != null
                    && workPermitChecks.toString() == WorkPermitChecks.WORK_PERMIT_CHECK_FINISHED.toString()) {
                return Arrays.asList(APPROVED);
            }

            List<Enum> enums = new ArrayList<>();
            if (securityChecks != null) {
                enums.addAll(securityChecks.nextStates());
            } else {
                enums.add(SecurityChecks.SECURITY_CHECK_STARTED);
            }
            if (workPermitChecks != null) {
                enums.addAll(workPermitChecks.nextStates());
            } else {
                enums.add(WorkPermitChecks.WORK_PERMIT_CHECK_STARTED);
            }

            return enums;
        }

        @Override
        public void reset() {
            this.securityChecks = null;
            this.workPermitChecks = null;
        }

        @Override
        public String toPersistenceString()
        {
            StringJoiner joiner = new StringJoiner(separator);
            joiner.add(IN_CHECK.toString());
            if (securityChecks != null) {
                joiner.add(securityChecks.toString());
            }
            if (workPermitChecks != null) {
                joiner.add(workPermitChecks.toString());
            }
            return joiner.toString();
        }
    },
    APPROVED {
        @Override
        public String toPersistenceString()
        {
            return this.toString();
        }

        @Override
        public void addChildren(List<Enum> children) {}

        @Override
        public void reset() {}

        @Override
        public List<Enum> nextStates()
        {
            return Arrays.asList(ACTIVE);
        }
    },
    ACTIVE {
        @Override
        public String toPersistenceString()
        {
            return this.toString();
        }

        @Override
        public void addChildren(List<Enum> children) {}

        @Override
        public void reset() {}

        @Override
        public List<Enum> nextStates()
        {
            return Arrays.asList(ACTIVE);
        }
    };

    /**
     * Return an instance of States from the given string representation. This method supports
     * children initialization too (only first-level). The accepted format is:
     * "<string representation of the main state>-<first child>-<second child>...-<Nth child>
     *
     * e.g. "IN_CHECK-SECURITY_CHECK_STARTED-WORK_PERMIT_CHECK_STARTED
     * e.g. "IN_CHECK-SECURITY_CHECK_FINISHED-WORK_PERMIT_CHECK_STARTED
     * e.g. "IN_CHECK-SECURITY_CHECK_FINISHED
     *
     * The main state and its child states should be separated by means States.separator static variable.
     *
     * @param states
     *
     * @return An instance of States with all possible children enums.
     */
    public static States getFromString(String states)
    {
        List<Enum> children = new ArrayList<>();
        States statesMachine = null;
        if (states != null) {
            for (String part: states.split(separator)) {
                if (EnumUtils.isValidEnum(States.class, part)) {
                    statesMachine = States.valueOf(part);
                } else if (EnumUtils.isValidEnum(SecurityChecks.class, part)) {
                    statesMachine = IN_CHECK;
                    children.add(SecurityChecks.valueOf(part));
                } else if (EnumUtils.isValidEnum(WorkPermitChecks.class, part)) {
                    statesMachine = IN_CHECK;
                    children.add(WorkPermitChecks.valueOf(part));
                }
            }
        }

        if (statesMachine != null) {
            statesMachine.addChildren(children);
        }

        return statesMachine;
    }

    /**
     * Return true if the given state is one of the possible next states.
     *
     * @param nextState
     *
     * @return
     */
    public boolean isNextState(String nextState)
    {
        for (Enum state : this.nextStates()) {
            if (Objects.equals(state.toString(), nextState)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the given state string is a valid possible state in the States machine.
     * This method, unlike Enum.valueOf(), also checks if the given state string is a
     * possible child state.
     *
     * @param state The state to be checked.
     *
     * @return boolean Whether the given state is a valid possible state.
     */
    public static boolean isValidState(String state)
    {
        return
            EnumUtils.isValidEnum(States.class, state)
            || EnumUtils.isValidEnum(SecurityChecks.class, state)
            || EnumUtils.isValidEnum(WorkPermitChecks.class, state)
        ;
    }

    /**
     * Return a string representation of this state machine instance. This string might be
     * different from the one returned by the toString() method, and it can be used to store
     * a detailed representation of this state machine with its children, if any.
     *
     * @return
     */
    public abstract String toPersistenceString();

    /**
     * Default implementation to add child enums to this enum instance;
     * @param children
     */
    public abstract void addChildren(List<Enum> children);

    public abstract void reset();

    public static final String separator = "-";
}

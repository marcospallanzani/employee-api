package com.workmotion.api.statemachine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StatesTest
{
    @ParameterizedTest(name = "{index} => stateAsString={0}, nextStates={1}")
    @ArgumentsSource(StatesTest.CustomArgumentProvider.class)
    public void getFromStringTest(String stateAsString, List<Enum> nextStates)
    {
        States states = States.getFromString(stateAsString);
        assertThat(states).isNotNull();
        states.nextStates();
        assertThat(states.nextStates()).isEqualTo(nextStates);
    }

    @Test
    public void isValidStateTest()
    {
        assertThat(States.isValidState("ADDED")).isTrue();
        assertThat(States.isValidState("IN_CHECK")).isTrue();
        assertThat(States.isValidState("APPROVED")).isTrue();
        assertThat(States.isValidState("ACTIVE")).isTrue();
        assertThat(States.isValidState("SECURITY_CHECK_STARTED")).isTrue();
        assertThat(States.isValidState("SECURITY_CHECK_FINISHED")).isTrue();
        assertThat(States.isValidState("WORK_PERMIT_CHECK_STARTED")).isTrue();
        assertThat(States.isValidState("WORK_PERMIT_CHECK_FINISHED")).isTrue();
        assertThat(States.isValidState("PENDING")).isFalse();
        assertThat(States.isValidState("SECURITY_CHECK")).isFalse();
        assertThat(States.isValidState("WORK_PERMIT")).isFalse();
    }

    static class CustomArgumentProvider implements ArgumentsProvider
    {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context)
        {
            return Stream.of(
                Arguments.of("ADDED", Arrays.asList(States.IN_CHECK)),
                Arguments.of("IN_CHECK", Arrays.asList(SecurityChecks.SECURITY_CHECK_STARTED, WorkPermitChecks.WORK_PERMIT_CHECK_STARTED)),
                Arguments.of("IN_CHECK-SECURITY_CHECK_STARTED", Arrays.asList(SecurityChecks.SECURITY_CHECK_FINISHED, WorkPermitChecks.WORK_PERMIT_CHECK_STARTED)),
                Arguments.of("IN_CHECK-SECURITY_CHECK_STARTED-WORK_PERMIT_CHECK_STARTED", Arrays.asList(SecurityChecks.SECURITY_CHECK_FINISHED, WorkPermitChecks.WORK_PERMIT_CHECK_FINISHED)),
                Arguments.of("IN_CHECK-SECURITY_CHECK_FINISHED-WORK_PERMIT_CHECK_STARTED", Arrays.asList(SecurityChecks.SECURITY_CHECK_FINISHED, WorkPermitChecks.WORK_PERMIT_CHECK_FINISHED)),
                Arguments.of("IN_CHECK-SECURITY_CHECK_FINISHED-WORK_PERMIT_CHECK_FINISHED", Arrays.asList(States.APPROVED)),
                Arguments.of("APPROVED", Arrays.asList(States.ACTIVE)),
                Arguments.of("ACTIVE", Arrays.asList(States.ACTIVE))
            );
        }
    }
}

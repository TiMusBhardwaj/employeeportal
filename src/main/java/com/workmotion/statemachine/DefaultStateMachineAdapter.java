package com.workmotion.statemachine;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author sumit.b
 *
 * @param <S> StateMachine State
 * @param <E>StateMachine Event
 * @param <T> Context Id to identify State Machine
 */
@RequiredArgsConstructor
public class DefaultStateMachineAdapter<S, E, T> {

    final StateMachineFactory<S, E> stateMachineFactory;

    final StateMachinePersister<S, E, T> persister;

    @SneakyThrows
    public StateMachine<S, E> restore(T contextObject) {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine();
        return persister.restore(stateMachine, contextObject);
    }

    @SneakyThrows
    public void persist(StateMachine<S, E> stateMachine, T id) {
        persister.persist(stateMachine, id);
    }

    @SuppressWarnings("deprecation")
	public StateMachine<S, E> create(String id) {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine(id);
        stateMachine.start();
        return stateMachine;
    }

}

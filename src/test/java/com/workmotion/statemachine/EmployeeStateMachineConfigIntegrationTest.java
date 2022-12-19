package com.workmotion.statemachine;

import static com.workmotion.statemachine.EmployeeState.ACTIVE;
import static com.workmotion.statemachine.EmployeeState.APPROVED;
import static com.workmotion.statemachine.EmployeeState.IN_CHECK;
import static com.workmotion.statemachine.EmployeeState.SECURITY_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.SECURITY_CHECK_STARTED;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

@SpringBootTest(classes = { 
		StateMachineConfig.class})
@EnableAutoConfiguration
@SuppressWarnings("deprecation")
public class EmployeeStateMachineConfigIntegrationTest {
	
	
	
	@Autowired
	private StateMachineFactory<EmployeeState, EmpEvents> factory;
	
	@BeforeEach
    public void setUp() {
    	
    }
	
	@Test
	public void testCaseScenario1() {
		
		StateMachine<EmployeeState, EmpEvents> sm = factory.getStateMachine("test1");
		assertTrue( sm.sendEvent(EmpEvents.BEGIN_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED), Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_SECURITY_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_PENDING_VERIFICATION),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_WORK_PERMIT_CHECK));
		assertEquals(Set.of(APPROVED),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.ACTIVATE));
		assertEquals(Set.of(ACTIVE),  Set.of(sm.getState().getIds().toArray()));

		
		sm.stop();
	}
	
	
	@Test
	public void testCaseScenario2() {
		
		StateMachine<EmployeeState, EmpEvents> sm = factory.getStateMachine("test2");
		
		assertTrue(sm.sendEvent(EmpEvents.BEGIN_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED), Set.of(sm.getState().getIds().toArray()));
		
		
		assertTrue( sm.sendEvent(EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_PENDING_VERIFICATION),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_WORK_PERMIT_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED),  Set.of(sm.getState().getIds().toArray()));
		
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_SECURITY_CHECK));
		
		assertEquals(Set.of(APPROVED),  Set.of(sm.getState().getIds().toArray()));
		assertTrue( sm.sendEvent(EmpEvents.ACTIVATE));
		assertEquals(Set.of(ACTIVE),  Set.of(sm.getState().getIds().toArray()));

		sm.stop();
		
	}
	
	@Test
	public void testCaseScenario3() {
		
		StateMachine<EmployeeState, EmpEvents> sm = factory.getStateMachine("test3");
		
		assertTrue(sm.sendEvent(EmpEvents.BEGIN_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED), Set.of(sm.getState().getIds().toArray()));
		
		
		assertTrue( sm.sendEvent(EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_PENDING_VERIFICATION),  Set.of(sm.getState().getIds().toArray()));
		

		assertTrue( sm.sendEvent(EmpEvents.FINISH_SECURITY_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_PENDING_VERIFICATION),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_WORK_PERMIT_CHECK));
		
		assertEquals(Set.of(APPROVED),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.ACTIVATE));
		assertEquals(Set.of(ACTIVE),  Set.of(sm.getState().getIds().toArray()));

		sm.stop();
		
	}

	
	
	@Test
	public void testCaseScenario4() {
		
		StateMachine<EmployeeState, EmpEvents> sm = factory.getStateMachine("test4");
		assertTrue( sm.sendEvent(EmpEvents.BEGIN_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED), Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_SECURITY_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue(! sm.sendEvent(EmpEvents.ACTIVATE));
		sm.stop();
		
	}
	
	
	@Test
	public void testCaseScenario5() {
		
		StateMachine<EmployeeState, EmpEvents> sm = factory.getStateMachine("test5");
		assertTrue( sm.sendEvent(EmpEvents.BEGIN_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED), Set.of(sm.getState().getIds().toArray()));
		
		assertTrue( sm.sendEvent(EmpEvents.FINISH_SECURITY_CHECK));
		assertEquals(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED),  Set.of(sm.getState().getIds().toArray()));
		
		assertTrue(! sm.sendEvent(EmpEvents.FINISH_SECURITY_CHECK));
		sm.stop();
		
	}
}

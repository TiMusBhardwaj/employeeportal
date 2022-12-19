package com.workmotion.service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

import com.workmotion.model.Employee;
import com.workmotion.repository.EmployeeRepository;
import com.workmotion.statemachine.EmpEvents;
import com.workmotion.statemachine.EmployeeState;
import com.workmotion.statemachine.UnAcceptedEventException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository empRepository;
	
	@Autowired
	private StateMachineFactory<EmployeeState, EmpEvents> employeeStateMachineFactory;
	
	@Autowired
	private StateMachineService<EmployeeState, EmpEvents> stateMachineService;


	/**
	 * 
	 * 1.Create a Employee with unique Id.
	 * 2. Use the same  id to create a stateMachine and save stateMachineContext
	 * database
	 * 
	 * @param employee
	 * @return saved Employee
	 */
	@SuppressWarnings("deprecation")
	@Transactional
	public Employee createEmployee(Employee employee) {
		
		Employee emp = empRepository.save(employee);
		StateMachine<EmployeeState, EmpEvents> sm = employeeStateMachineFactory.getStateMachine(emp.getId());
		sm.sendEvent(EmpEvents.BEGIN_CHECK);
		emp.setStates(sm.getState().getIds().stream().collect(Collectors.toSet()));
		return emp;
	}

	/**
	 * @param empId
	 * @param empEvent
	 * @return Current State of Employee
	 * @throws UnAcceptedEventException for event not accepted by statemachine
	 */
	@Transactional
	public Optional<Employee> updatState(String empId, EmpEvents empEvent) {
		
		Optional<Employee> empOp = empRepository.findById(empId);
		if (empOp.isEmpty()) {
			throw new NoResultException();
		}
		
		StateMachine<EmployeeState, EmpEvents> sm = stateMachineService.acquireStateMachine(empId, false);
		@SuppressWarnings("deprecation")
		boolean eventSuccessFul = sm.sendEvent(empEvent);
		Set<EmployeeState> empState = sm.getState().getIds().stream().collect(Collectors.toSet());
		if (!eventSuccessFul) {
			throw new UnAcceptedEventException(
					String.format("Employee State : %s, not accepting Event : %s", empState, empEvent));
		}
		empOp.filter(emp -> Objects.nonNull(emp.getId())).ifPresent(emp -> emp.setStates(empState));
		stateMachineService.releaseStateMachine(empId);
		return empOp;
	}

	/**
	 * @param employeeId
	 * @return
	 */
	public Optional<Employee> getEmployee(String employeeId) {
		
		Optional<Employee> empOp = empRepository.findById(employeeId);
		
		return empRepository.findById(employeeId);
	}

	
	
}

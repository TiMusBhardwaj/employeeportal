package com.workmotion.service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.workmotion.model.Employee;
import com.workmotion.repository.EmployeeRepository;
import com.workmotion.statemachine.DefaultStateMachineAdapter;
import com.workmotion.statemachine.EmpEvents;
import com.workmotion.statemachine.EmployeeState;
import com.workmotion.statemachine.exception.UnAcceptedEventException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository empRepository;
	
	@Autowired
    private DefaultStateMachineAdapter<EmployeeState, EmpEvents, String> empStateMachineAdapter;

	
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
		StateMachine<EmployeeState, EmpEvents> sm = empStateMachineAdapter.create(emp.getId());
		empStateMachineAdapter.persist(sm, emp.getId());
		emp.setStates(sm.getState().getIds().stream().collect(Collectors.toSet()));
		sm.stop();
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
		
		StateMachine<EmployeeState, EmpEvents> sm = empStateMachineAdapter.restore(empId);
		@SuppressWarnings("deprecation")
		boolean eventSuccessFul = sm.sendEvent(empEvent);
		Set<EmployeeState> empState = sm.getState().getIds().stream().collect(Collectors.toSet());
		if (!eventSuccessFul) {
			throw new UnAcceptedEventException(
					String.format("Employee State : %s, not accepting Event : %s", empState, empEvent));
		}
		empOp.filter(emp -> Objects.nonNull(emp.getId())).ifPresent(emp -> emp.setStates(empState));
		empStateMachineAdapter.persist(sm, empId);
		sm.stop();
		return empOp;
	}

	/**
	 * @param employeeId
	 * @return
	 */
	public Optional<Employee> getEmployee(String employeeId) {
		
		return empRepository.findById(employeeId);
	}

	
	
}

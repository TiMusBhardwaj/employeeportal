package com.workmotion.controller;

import java.util.Optional;

import javax.persistence.NoResultException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.workmotion.controller.dto.EmployeeDTO;
import com.workmotion.controller.dto.EmployeeEventDTO;
import com.workmotion.model.Employee;
import com.workmotion.service.EmployeeService;
import com.workmotion.statemachine.UnAcceptedEventException;

@RestController
public class EmployeeController {

	private static final ResponseEntity<Employee> RESOURCE_NOT_FOUND = new ResponseEntity<Employee>(
			HttpStatus.NOT_FOUND);

	@Autowired
	private EmployeeService service;
	
	private ModelMapper mappper = new ModelMapper();

	@PostMapping("/employee")
	public Employee create(@RequestBody EmployeeDTO employee) {

		return service.createEmployee(mappper.map(employee, Employee.class));
	}

	@PutMapping("employee/{employeeId}/state")
	public ResponseEntity<Employee> updateState(@PathVariable String employeeId,
			@RequestBody EmployeeEventDTO dto) {
		Optional<Employee> emp = Optional.empty();
		try {
			emp = service.updatState(employeeId, dto.getEmpEvent());
		} catch (UnAcceptedEventException exc) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getMessage(), exc);
		} catch (NoResultException e) {
			return RESOURCE_NOT_FOUND;
		}
		return new ResponseEntity<Employee>(emp.get(), HttpStatus.OK);
	}

	@GetMapping("employee/{employeeId}")
	public ResponseEntity<Employee> getEmployee(@PathVariable String employeeId) {

		Optional<Employee> employee = service.getEmployee(employeeId);

		if (employee.isEmpty()) {
			return RESOURCE_NOT_FOUND;
		}

		return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);
	}

}

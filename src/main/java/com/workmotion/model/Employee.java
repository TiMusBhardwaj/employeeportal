package com.workmotion.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.workmotion.statemachine.EmployeeState;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table

public class Employee {
	
	@Id
	@GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
	
	@Column(nullable = false)
	private String name;
	
	
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<EmployeeState> states;


	public Employee() {
		super();
		
	}
	
	public Employee(String name) {
		super();
		this.name = name;
	}
	
	

}

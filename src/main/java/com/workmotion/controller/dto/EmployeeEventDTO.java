package com.workmotion.controller.dto;

import com.workmotion.statemachine.EmpEvents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
public class EmployeeEventDTO {
	
	EmpEvents empEvent;

}

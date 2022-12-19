package com.workmotion.repository.handler;

import javax.annotation.PostConstruct;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import com.workmotion.model.Employee;

import lombok.extern.slf4j.Slf4j;

@Component
@RepositoryEventHandler(Employee.class)
@Slf4j
public class EmployeeRepoHandler extends AbstractRepositoryEventListener<Employee>{
	
	@PostConstruct
	void init(){
		log.info("init ...");
	}
	
	@HandleBeforeCreate(Employee.class)
	@HandleBeforeSave(Employee.class)
	@HandleBeforeDelete
    public void onBeforeCreate(Employee emp){
        log.info("handlEmployeeBeforeCreate ...");
    }
	
	@HandleBeforeCreate(Employee.class)
	@HandleBeforeSave(Employee.class)
	@HandleBeforeDelete
    public void onBeforeSave(Employee emp){
        log.info("handlEmployeeBeforeCreate ...");
    }

}

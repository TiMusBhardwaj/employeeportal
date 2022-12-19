package com.workmotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.workmotion.model.Employee;

@Repository
@RepositoryRestResource
public interface EmployeeRepository extends JpaRepository<Employee, String>{

}

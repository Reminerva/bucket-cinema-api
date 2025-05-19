package com.flix.flix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flix.flix.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

}

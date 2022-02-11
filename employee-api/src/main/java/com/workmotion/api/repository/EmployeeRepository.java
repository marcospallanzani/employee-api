package com.workmotion.api.repository;

import com.workmotion.api.model.persistence.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

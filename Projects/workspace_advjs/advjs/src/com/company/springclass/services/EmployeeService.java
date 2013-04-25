package com.company.springclass.services;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.company.springdemo.beans.Employee;

@WebService
public interface EmployeeService {

	@WebMethod()
	public boolean updateInfo(Employee employee);

	@WebMethod()
	public Employee getEmployee(int empId);

}
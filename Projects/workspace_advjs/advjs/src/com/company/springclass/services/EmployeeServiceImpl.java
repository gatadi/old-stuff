package com.company.springclass.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import com.company.springdemo.beans.Employee;
import com.company.springdemo.dao.EmployeeJpaDao;

@WebService(name="EmployeeService")
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeJpaDao empJpaDao = null;
	public void setEmployeeDAO(EmployeeJpaDao empJpaDao) { this.empJpaDao = empJpaDao; }
	
	@WebMethod()
	public boolean updateInfo(Employee employee) {
	    boolean result = false;
	 
	    try {
	    	System.out.println("updating..." + employee);
	    	result = true;
	    }
	    catch(Exception e) {
	    	System.err.println(e.getMessage());
	    }
	    
	    return result;
	}
	
	@WebMethod()
	public Employee getEmployee(@WebParam int empId) {
		
		Employee emp = null;
		
		try {
			emp = empJpaDao.find(empId);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return emp;
	}
}

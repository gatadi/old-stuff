package com.company.springdemo.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.company.springdemo.beans.Employee;
import com.company.springdemo.dao.EmployeeJpaDao;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired private EmployeeJpaDao employeeJpaDao;

	@RequestMapping(value="/{empId}", method=RequestMethod.GET)
	public ModelAndView getEmployee(HttpServletResponse response, @PathVariable int empId, @RequestHeader(defaultValue="xml") String format) 
			throws Exception {
		String contentType = "text/xml", view = "xmlView";
		
		if (format.equalsIgnoreCase("json")) {
			contentType = "application/json";
			view = "jsonView";
		}
		
		response.setContentType(contentType);
		Employee emp = employeeJpaDao.find(empId);
		
		return new ModelAndView(view,"emp",emp);
	}
	
	@RequestMapping(value="/{empId}", method=RequestMethod.POST)
	public ModelAndView update(@PathVariable int empId, @RequestParam String firstName,
							   @RequestParam String lastName, @RequestParam double salary) throws Exception {
		
		Employee emp = employeeJpaDao.find(empId);
		emp.setFirstName(firstName);
		emp.setLastName(lastName);
		emp.setSalary(salary);
		employeeJpaDao.update(emp);
		
		return new ModelAndView("xmlView", "emp", emp);
	}
	
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView insert(@RequestParam String firstName,
			   				   @RequestParam String lastName, @RequestParam double salary) throws Exception {
		
		Employee emp = new Employee();

		try {
			emp.setFirstName(firstName);
			emp.setLastName(lastName);
			emp.setSalary(salary);

			Employee returnedEmp = (Employee) employeeJpaDao.insert(emp); 

			emp = returnedEmp;
		}
		catch(Exception e) { 
			emp = new Employee(0, "", "", 0.0);
			System.err.println("Not inserted. Reason: " + e.getMessage());
		}
		
		return new ModelAndView("xmlView", "emp", emp);
	}
	
	
	@RequestMapping(value="/{empId}", method=RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable int empId) throws Exception {
		
		Employee emp = employeeJpaDao.find(empId);
		employeeJpaDao.remove(emp);
		
		return new ModelAndView("xmlView", "emp", emp); 
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getEmployeesJSON(HttpServletResponse resp) {
		
		resp.setContentType("application/json");
		List empList = null;
		try {
			empList = employeeJpaDao.findAll();
			System.out.println(empList);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("jsonView", "empList", empList);
	}
}



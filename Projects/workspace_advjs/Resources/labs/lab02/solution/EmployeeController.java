package com.company.springdemo.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		String contentType = "text/xml", view = "jsonView";
		
		if (format.equalsIgnoreCase("json")) {
			contentType = "application/json";
			view = "jsonView";
		}
		
		response.setContentType(contentType);
		Employee emp = employeeJpaDao.find(empId);
		
		return new ModelAndView(view,"emp",emp);
	}
}







boolean foo(<)



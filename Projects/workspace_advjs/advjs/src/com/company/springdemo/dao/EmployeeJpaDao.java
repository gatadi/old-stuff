package com.company.springdemo.dao;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.company.springdemo.beans.Employee;


@Repository
public class EmployeeJpaDao {
	
	Logger logger = Logger.getLogger(EmployeeJpaDao.class);
	private JpaTemplate jpaTemplate;
	
	@Autowired
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		jpaTemplate = new JpaTemplate(entityManagerFactory);
	}
	
	public Employee find(Object id) throws Exception {
		
		Employee emp = jpaTemplate.find(Employee.class, new Integer(id.toString()));
		if(emp == null) {
			emp = new Employee(0, "", "", 0.0);
			logger.warn("Employee ID (" + id + ") supplied was not found in the database, returning empty employee.");
		}
		
		return emp;
	}
	
	@Transactional
	public Object insert(Object o) throws Exception {
		jpaTemplate.persist(o);
		jpaTemplate.flush();
		return o;
	}

	@Transactional
	public void update(Object o) throws Exception {
		jpaTemplate.merge(o);
		jpaTemplate.flush();
	}

	@Transactional
	public void remove(Object o) throws Exception {
		if(o instanceof Employee) {
			Employee emp = (Employee) o;
			emp = jpaTemplate.find(Employee.class, emp.getEmpId());
			if (emp != null)
				jpaTemplate.remove(emp);
			jpaTemplate.flush();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List findAll() throws Exception{
		return jpaTemplate.find("from Employee");
	}
}

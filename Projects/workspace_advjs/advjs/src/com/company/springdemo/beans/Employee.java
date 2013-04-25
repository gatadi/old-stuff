package com.company.springdemo.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="Employee", schema="advjs")
@org.hibernate.annotations.Proxy(lazy=false)
@XmlRootElement(name="employee")
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int empId;
	private String firstName;
	private String lastName;
	private double salary;
	
	public Employee(){}
	
	public Employee(int empId, String firstName, String lastName, double salary) {
		this.empId = empId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
	}

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public int getEmpId() {
		return empId;
	}
	
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public double getSalary() {
		return salary;
	}
	
	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	public String toString() {
	
		return firstName + " " + lastName + "(" + salary + ")";
	}
}

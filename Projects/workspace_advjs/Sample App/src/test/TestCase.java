package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestCase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void shouldReturnSum() {
		CTest t = new CTest();
		assertEquals("Should return ", 50, t.sum(20,30));		
	}
	
	@Test
	public void shouldReturnMul(){
		CTest t = new CTest();
		assertEquals("Results:", 50, t.mult(5,10));
	}

}

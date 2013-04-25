package test;
import java.util.*;

import java.awt.List;

public class CTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello World!");
		
		
		OrderedPair<String, Integer> op = new CTest().new OrderedPair<String, Integer>("Key1", 100);
		System.out.println(op.getKey());
		System.out.println(op.getValue());
		
		 
	}
	
	public int sum(int x, int y){
		return x + y ;
	}
	
	public int mult(int x, int y){
		return x*y;
	}
	
	public interface Pair<A, B>
	{
		public A getKey();
		public B getValue();
	}
	
	public class OrderedPair<A, B> implements Pair<A,B>{
		private A key ;
		private B value ;
		
		public OrderedPair(A key, B value){
			this.key = key ;
			this.value = value ;
		}
		
		public A getKey(){
			return	 this.key ;
		}
		
		public B getValue(){
			return this.value ;
		}
	}

}

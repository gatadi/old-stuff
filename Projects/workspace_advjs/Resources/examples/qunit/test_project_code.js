test("some tests", function() {
    	  
	expect(4); 
		 
	ok(someFunc, "someFunc returns true, so the test passes");
		 
	equal("1", 1, "passes because it uses a == comparison");
	     
	strictEqual("1", 1, "fails because it uses a === comparison");
	
	var p = { name : "Rob", age : 25};
	deepEqual(person, p, "These objects are exactly the same.");
});
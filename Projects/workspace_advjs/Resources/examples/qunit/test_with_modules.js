module("First Module");
test("test case 1", 2, function() {
	ok(someFunc, "someFunc returns true, so the test passes");
	equal("1", 1, "passes because it uses a == comparison");
});

test("test case 2", 1, function() {    
	strictEqual("1", 1, "fails because it uses a === comparison");
});


module("Second Module");
test("test case 3", function() { 
	var p = { name : "Rob", age : 25};
	deepEqual(person, p, "These objects are exactly the same.");
});


test("async test", function() {
	  stop();
	  setTimeout(function(){
	    ok(true, "this test should pass fine");
	    start();
	  }, 1100);
});


<%@page contentType="text/event-stream" buffer="none"%>
<%
    //check for poll-only header
    String header = request.getHeader("X-YUIEventSource-PollOnly");
    
    //check every so often to see if there's new data
    while(true) {
        
        //sleep for 3 seconds - simulate waiting for data
        Thread.sleep(3000);
        
        //output the current time, ensure there are two trailing newlines to end a message
        out.print("data: " + (new java.util.Date()).toString() + "\n\n");
        out.flush();
        
        //if it's a poll-only request, break the loop, which ends the request - the client will reconnect
        if (header != null){
            break;
        }
    }
    
%>
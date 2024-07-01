# miniserver
Usage invoke on browser http://localhost:8080/
enter your name on text box and submit 
You will get a response 
Hello BIKASH KUMAR AGRAWALA! How can I help you ?
Used com.sun.net.httpserver.HttpServer class to create a server to open at port 8080
Used MainHandler as a single HttpHandler to which the context will route all request 
MainHandler will route the request to corresponding HttpHandler based on API 
Used Command Design Pattern and using each HttpHandler as a command 
Once each HttpHandler class loaded it will register to handlers map which will store key(String) API and value an object of handlers which will handle corresponding request to the API 
Used static block which will help registering the command 

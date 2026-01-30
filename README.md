# MyWebServer-CS1103-Unit-7-Academic-Project-
A very simple implementation of an HTTP web server with multithread handling using raw sockets that opens on LISTENING_PORT = 50500.

OVERVIEW

	MyWebServer is a simple, multi-threaded HTTP server implemented in Java.
	It demonstrates the basics of how web servers handle HTTP GET requests and serve static files, including:

		- Handling multiple client connections using threads.
		- Serving files from a specified root directory.
		- Parsing HTTP requests and responding with appropriate headers.
		- Sending HTTP error responses (400, 403, 404, 500, 501).
		- Determining MIME types based on file extension.
    
    This project is intended as an educational illustration for understanding HTTP server basics in Java, as per my
    Unit 7 assignment brief in CS1103-Programming 2 Course with University of the People (UoPeople)

FEATURES

	- Multi-threaded server for concurrent client connections.
	- Serves static files (HTML, CSS, JS, images, etc.).
	- Generates custom HTML error pages for common HTTP errors.
	- Simple manual implementation of HTTP protocol headers.

LIMITATIONS

	- Supports only GET requests.
	- Only serves static files (no dynamic content or server-side scripting).
	- No HTTPS support (plain HTTP only).
	- Very basic HTTP parsing — not suitable for production use, educational use only.

CONTRIBUTING

	- This project is intended for learning and experimentation. 
	- Users may adopt the code and develop it as they see fit.

NOTES

	Portions of this code were provided by the University of the People for educational purposes. All other code, modifications, 
	and contributions by KavemanRSA are licensed under the CC0 License.

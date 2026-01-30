
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyWebServer {
	/**
	 * The server listens on this port. Note that the port number must
	 * be greater than 1024 and lest than 65535.
	 */
	private final static int LISTENING_PORT = 50500;

	private static String rootDirectory = "/home/mcorliss/www";

	/**
	 * Holds and sends various error responses that are decided by if..else 
	 * statement as needed by exception handling.
	 * 
	 * @param errorCode An integer error code used to decide on which response to display.
	 * @param socketOut An OutputStream used to send the error response decided upon.
	 */
	private static void sendErrorResponse(int errorCode, OutputStream socketOut) {
		PrintWriter errorPrint = new PrintWriter(socketOut);

		if(errorCode == 400) {
			errorPrint.print("HTTP/1.1 400 Bad Request\r\n");
			errorPrint.print("Connection: close\r\n");
			errorPrint.print("Content-Type: text/html\r\n");
			errorPrint.print("\r\n<html><head><title>Error</title></head><body>\r\n");
			errorPrint.print("<h2>Error: 400 Bad Request</h2>\r\n");
			errorPrint.print("<p>Bad syntax request.</p>\r\n");
			errorPrint.print("</body></html>\r\n");

			errorPrint.flush();
			errorPrint.close();
		}
		else if(errorCode == 403) {
			errorPrint.print("HTTP/1.1 403 Forbidden\r\n");
			errorPrint.print("Connection: close\r\n");
			errorPrint.print("Content-Type: text/html\r\n");
			errorPrint.print("\r\n<html><head><title>Error</title></head><body>\r\n");
			errorPrint.print("<h2>Error: 403 Forbidden</h2>\r\n");
			errorPrint.print("<p>File found. Read permission denied.</p>\r\n");
			errorPrint.print("</body></html>\r\n");

			errorPrint.flush();
			errorPrint.close();
		}
		else if(errorCode == 404) {
			errorPrint.print("HTTP/1.1 404 Not Found\r\n");
			errorPrint.print("Connection: close\r\n");
			errorPrint.print("Content-Type: text/html\r\n");
			errorPrint.print("\r\n<html><head><title>Error</title></head><body>\r\n");
			errorPrint.print("<h2>Error: 404 Not Found</h2>\r\n");
			errorPrint.print("<p>The resource that you requested does not exist on this server.</p>\r\n");
			errorPrint.print("</body></html>\r\n");

			errorPrint.flush();
			errorPrint.close();
		}
		else if(errorCode == 500) {
			errorPrint.print("HTTP/1.1 500 Internal Server Error\r\n");
			errorPrint.print("Connection: close\r\n");
			errorPrint.print("Content-Type: text/html\r\n");
			errorPrint.print("\r\n<html><head><title>Error</title></head><body>\r\n");
			errorPrint.print("<h2>Error: 500 Internal Server Error</h2>\r\n");
			errorPrint.print("<p>Unexpected error in handling the connection.</p>\r\n");
			errorPrint.print("</body></html>\r\n");

			errorPrint.flush();
			errorPrint.close();
		}
		else {
			errorPrint.print("HTTP/1.1 501 Not Implemented\r\n");
			errorPrint.print("Connection: close\r\n");
			errorPrint.print("Content-Type: text/html\r\n");
			errorPrint.print("\r\n<html><head><title>Error</title></head><body>\r\n");
			errorPrint.print("<h2>Error: 501 Not Implemented</h2>\r\n");
			errorPrint.print("<p>Invalid request method.</p>\r\n");
			errorPrint.print("</body></html>\r\n");

			errorPrint.flush();
			errorPrint.close();
		}
	}

	/**
	 * return the proper content type for many kinds of files
	 * 
	 * @param fileName	The file name being requested
	 */
	private static String getMimeType(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos < 0) // no file extension in name
			return "x-application/x-unknown";
		String ext = fileName.substring(pos+1).toLowerCase();
		if (ext.equals("txt")) return "text/plain";
		else if (ext.equals("html")) return "text/html";
		else if (ext.equals("htm")) return "text/html";
		else if (ext.equals("css")) return "text/css";
		else if (ext.equals("js")) return "text/javascript";
		else if (ext.equals("java")) return "text/x-java";
		else if (ext.equals("jpeg")) return "image/jpeg";
		else if (ext.equals("jpg")) return "image/jpeg";
		else if (ext.equals("png")) return "image/png";
		else if (ext.equals("gif")) return "image/gif";
		else if (ext.equals("ico")) return "image/x-icon";
		else if (ext.equals("class")) return "application/java-vm";
		else if (ext.equals("jar")) return "application/java-archive";
		else if (ext.equals("zip")) return "application/zip";
		else if (ext.equals("xml")) return "application/xml";
		else if (ext.equals("xhtml")) return"application/xhtml+xml";
		else return "x-application/x-unknown";
		// Note: x-application/x-unknown is something made up;
		// it will probably make the browser offer to save the file.
	}
	
	/**
	 * Used to copy the content of the file to the socket's output stream by 
	 * reading each byte of the file at a time
	 * 
	 * @param file		A the successfully requested and found file.
	 * @param socketOut An OutputStream used to send the contents of the file 
	 * 					to the client.
	 */
	private static void sendFile(File file, OutputStream socketOut) throws
	IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		OutputStream out = new BufferedOutputStream(socketOut);
		while (true) {
			int x = in.read(); // read one byte from file
			if (x < 0)
				break; // end of file reached
			out.write(x); // write the byte to the socket
		}
		out.flush();
	}

	/**
	 * Handles communication with each client connection.  This method reads
	 * lines of text from the client and prints them to standard output.
	 * It continues to read until the client closes the connection or
	 * until an error occurs or until a blank line is read.  In a connection
	 * from a Web browser, the first blank line marks the end of the request.
	 * This method can run indefinitely,  waiting for the client to send a
	 * blank line.
	 * NOTE:  This method does not throw any exceptions.  Exceptions are
	 * caught and handled with the help of the sendErrorResponce method, so 
	 * that they will not shut down the server.
	 * 
	 * @param connection the connected socket that will be used to
	 *    communicate with the client.
	 */
	private static void handleConnection(Socket connection) {
		String next, pathToFile = "", http = "";		

		try {
			Scanner in = new Scanner(connection.getInputStream());
			PrintWriter outputPrint = new PrintWriter(connection.getOutputStream());
			OutputStream outStream = connection.getOutputStream();

			next = in.next();

			if (next.equalsIgnoreCase("get") && next.endsWith("/")) {
				pathToFile = in.next();

				pathToFile += "index.html";

				while (pathToFile.indexOf("/") == 0)
					pathToFile = pathToFile.substring(1);
				pathToFile = pathToFile.replace('/', File.separator.charAt(0));

				http = in.next();
			}
			else {
				sendErrorResponse(501, outStream);
				System.out.println("HTTP/1.1 501 Not Implemented: Invalid request method.");
			}

			if(http.equalsIgnoreCase("HTTP/1.1") && http.equalsIgnoreCase("HTTP/1.0")) {

				File file = new File(rootDirectory + pathToFile);

				if (file.isDirectory()) {
					outputPrint.print("The requested item is not a file. Please try again.");
					outputPrint.flush();
					outputPrint.close();
					return;
				} 
				else if (file.exists() && file.canRead()) {
					outputPrint.print(http + " 200 OK\r\n");
					outputPrint.print("Connection: close\r\n");
					outputPrint.print("Content-Type: " + getMimeType(file.getName()) + "\r\n");
					outputPrint.print("Content-Length: " + file.length() + "\r\n"+ "\r\n");

					outputPrint.flush();

					sendFile(file, outStream);
				} 
				else {
					if (file.exists() && !file.canRead()) {
						sendErrorResponse(403, outStream);
						System.out.println("HTTP/1.1 403 Forbidden: File found. Read permission denied.");
					}
					else if (!file.exists()) {
						sendErrorResponse(404, outStream);
						System.out.println("HTTP/1.1 404 Not Found: The resource that you requested "
								+ "does not exist on this server.");
					}
					outputPrint.flush();
				}
			}
			else {
				sendErrorResponse(400, outStream);
				System.out.println("HTTP/1.1 400 Bad Request: Bad syntax request.");
			}
		} 
		catch (Exception e) {
			System.out.println("Error while communicating with client: " + e);
		}
		finally {
			try {
				connection.close();
			} 
			catch (IOException e) {
			}
		}
	}
	
	/**
	 * A subclass created to handle multi-thread connections
	 * to the server. The class creates sockets and calls on handleConnection
	 * to facilitate the connection.
	 */
	private static class ConnectionThread extends Thread {
		Socket connection;
		ConnectionThread(Socket connection) {
			this.connection = connection;
		}
		public void run() {
			handleConnection(connection);
		}
	}

	/**
	 * Main program creates a ServerSocket and use it to accept connection requests. It uses 
	 * the subclass ConnectionThread's constructor to make the server a multi-thread server.
	 * 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(LISTENING_PORT);
		}
		catch (Exception e) {
			System.out.println("Failed to create listening socket.");
			return;
		}
		System.out.println("Listening on port " + LISTENING_PORT);  
	    while (true) {
	      try {
	        Socket connection =serverSocket.accept();
	        System.out.println("\nConnection from " 
					+ connection.getRemoteSocketAddress());
	        ConnectionThread thread = new ConnectionThread(connection);
	        thread.start();
	      }
	      catch (Exception e) {
	    	  System.out.println("Server socket shut down unexpectedly!");
				System.out.println("Error: " + e);
				System.out.println("Exiting.");
	      }
	    }
	}

}

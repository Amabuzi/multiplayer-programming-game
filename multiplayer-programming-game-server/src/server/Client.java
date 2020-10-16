package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Client implements Runnable {
	
	// Create variables used to store information about the client:
	private String ipAddress;
	
	
	// Create I/O stream variables used to send data to and receive data from the client:
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	// Create a socket variable to used to access the socket used to connect the client to the server:
	private Socket socket;
	
	private boolean isConnected = true; // Create a boolean used to store whether the client is connected or not.
	
	
	private Queue<String> dataQueue = new LinkedList<String>(); // Create a queue to temporarily store the data received from the client.

	public Client(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
		// Store the username, I/O stream and socket parameters as class variables:
		
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.socket = socket;
		
		ipAddress = socket.getRemoteSocketAddress().toString(); // Get the IP address of the client and store it as a class variable.
		ipAddress = ipAddress.substring(1, ipAddress.indexOf(":")); // Isolate the IP address from the port.

		new Thread(this).start();
	}
	
	// Method called when this object's thread is started:
	public void run() {
		while (isConnected) { // Loop until the client disconnects.
			try { // The following line may throw an exception which must be caught.
				dataQueue.add(inputStream.readUTF()); // Wait to receive data from the client and add it to the data queue.
			}
			
			// If there is a connection error, the client has disconnected, so change the isConnected boolean to false:
			catch (IOException e) {
				isConnected = false;
			}
		}
	}
	
	
	// Method to check if there is unhandled data in the data queue:
	public boolean hasData() {
		return !dataQueue.isEmpty();
	}
	
	
	// Method to get the "data type" part of the first element of data in the queue:
	public String getDataType() {
		return dataQueue.peek().substring(0, 3); // Peek at the first element of data and return the first 3 characters.
	}
	
	
	// Method to return the "data" part of the first element of data in the queue:
	public String getData() {
		return dataQueue.poll().substring(3); // Pop the first element of data and return all but the first 3 characters.
	}
	
	// Method to send a given string to the client:
	public void sendData(String data) {
		
		// Attempt to send the data to the client:
		try {
			outputStream.writeUTF(data);
			outputStream.flush();
		}
		
		// If there is a connection error, the client has disconnected, so change the isConnected boolean to false:
		catch (IOException e) {
			isConnected = false;
		}
	}
	
	public String getIPAddress() {
		return ipAddress;
	}
	
	// Method to get whether the client is connected or not:
	public boolean isConnected() {
		return isConnected;
	}
	
	// Method to disconnect the client:
	public void disconnect() {
		
		// Attempt to close the socket:
		try {
			socket.close();
			isConnected = false;
		}
		
		// If an exception occurred, print the stack trace:
		catch (IOException e) {
			e.printStackTrace();
		}
	}
 }

package client; 

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class CommunicationHandler implements Runnable {
	
	// Create I/O stream variables used to send data to and receive data from the server:
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private boolean isConnected = true; // Create a boolean used to store whether the program is connected to the server or not.
	
	private Queue<String> dataQueue = new LinkedList<String>(); // Create a queue to temporarily store the data received from the server.
	
	
	// Constructor which is called when this object is created:
	public CommunicationHandler(DataInputStream inputStream, DataOutputStream outputStream) {
		
		// Store the I/O stream parameters as class variables:
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		
		new Thread(this).start(); // Start this object's run() method in a new thread.
	}
	
	
	// Method called when this object's thread is started:
	public void run() {
		while (isConnected) { // Loop until the client disconnects.
			try {// The following line may throw an exception which must be caught.
				dataQueue.add(inputStream.readUTF()); // Wait to receive data from the server and add it to the data queue.
			}
			
			// If there is a connection error, the program has disconnected from the server, so change the isConnected boolean to false:
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
		return dataQueue.peek().substring(0, 3);
	}
	
	
	// Method to return the "data" part of the first element of data in the queue:
	public String getData() {
		return dataQueue.poll().substring(3);
	}
	
	
	// Method to wait until data is added to the data queue:
	public boolean waitForData() {
		while (!hasData() && isConnected) { // Loop until data is added or until the connection is broken.
			
			// Sleep for 200ms as constantly checking the while loop conditions unnecessarily uses resources:
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return isConnected; // Return whether the connection is active or not.
	}
	
	
	// Method to send data to the server:
	public void sendData(String data) {
		
		// Attempt to send the data to the server:
		try {
			outputStream.writeUTF(data);
			outputStream.flush();
		}
		
		// If there is a connection error, the connection has been lost, so change the isConnected boolean to false:
		catch (IOException e) {
			isConnected = false;
		}
	}
	
	
	// Method to get whether the client is connected or not:
	public boolean isConnected() {
		return isConnected;
	}
}

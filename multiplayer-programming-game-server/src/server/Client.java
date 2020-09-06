package server; //TODO modularise

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;

public class Client implements Runnable {
	
	// Create variables used to store information about the client:
	private String username;
	private String ipAddress;
	private String status = "Waiting";
	private int currentScore = 0;
	private int totalScore = 0;
	private int numberPlayed = 0;
	private int numberWon = 0;
	private int numberOfTests = 0;
	
	// Create I/O stream variables used to send data to and receive data from the client:
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	// Create a socket variable to used to access the socket used to connect the client to the server:
	private Socket socket;
	
	private boolean isConnected = true; // Create a boolean used to store whether the client is connected or not.
	
	private ArrayList<String[]> failedResults = new ArrayList<String[]>(); // Create an array list to store the client's failed test results.
	
	private Queue<String> dataQueue = new LinkedList<String>(); // Create a queue to temporarily store the data received from the client.
	
	
	// Constructor which is called when this object is created:
	public Client(String username, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
		
		// Store the username, I/O stream and socket parameters as class variables:
		this.username = username;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.socket = socket;
		
		ipAddress = socket.getRemoteSocketAddress().toString(); // Get the IP address of the client and store it as a class variable.
		ipAddress = ipAddress.substring(1, ipAddress.indexOf(":")); // Isolate the IP address from the port.
		
		new Thread(this).start(); // Start this object's run() method in a new thread.
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
	
	
	// Method to get the client's username:
	public String getUsername() {
		return username;
	}
	
	
	// Method to get the client's IP address:
	public String getIPAddress() {
		return ipAddress;
	}
	
	
	// Method to get the client's status:
	public String getStatus() {
		return status;
	}
	
	
	// Method to set the client's status to the given string:
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	// Method to get the client's score for the current round:
	public int getCurrentScore() {
		return currentScore;
	}
	
	
	// Method to get the client's total score:
	public int getTotalScore() {
		return totalScore;
	}
	
	
	// Method to get the client's average score:
	public int getAverageScore() {
		
		// Attempt to calculate and return the average score:
		try {
			return totalScore/numberPlayed;
		}
		
		// If numberPlayed is 0, an exception is thrown due to attempting to divide by 0. In this case, return 0:
		catch (ArithmeticException e) {
			return 0;
		}
	}
	
	
	// Method to get the number of rounds played by the client:
	public int getNumberPlayed() {
		return numberPlayed;
	}
	
	
	// Method to get the number of rounds won by the client:
	public int getNumberWon() {
		return numberWon;
	}
	
	
	// Method to add a given integer to the client's current score:
	public void addScore(int score) {
		currentScore += score;
	}
	
	
	// Method to update the client's information when a round has ended.
	public void updateStats(boolean won) {
		totalScore += currentScore; // Add the current score to the total score.
		currentScore = 0; // Reset the current score to 0.
		numberPlayed += 1; // Increment the variable counting the number of rounds played.
		
		// If the client won the round, increment the variable counting the number of rounds won:
		if (won) {
			numberWon += 1;
		}
		
		status = "Waiting"; // Reset the client's status to "Waiting".
		numberOfTests = 0;
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Create a ByteArrayOutputStream.
		try { // The following block of code could throw an exception which must be caught.
			
			// Create an ObjectOutputStream with the ByteArrayOutputStream:
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(failedResults); // Write the ArrayList failedResults to the ObjectOutputStream.
		}
		
		// If an exception was thrown:
		catch (IOException e) {
			e.printStackTrace(); // Print the stack trace for debugging purposes.
		}
		
		byte[] bytes = byteArrayOutputStream.toByteArray(); // Convert and store the ByteArrayOutputStream to a byte[] array.
		
		// Using Base64, encode the byte[] array to a String to create the serialised player data string:
		String serialisedFailedResults = Base64.getEncoder().encodeToString(bytes);
		
		sendData("TRS"+serialisedFailedResults); // Send the failed results to the client.
		
		failedResults.removeAll(failedResults); // Reset the failed results ArrayList.
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
	
	
	// Method to get whether the client is connected or not:
	public boolean isConnected() {
		return isConnected;
	}
	
	
	// Method to set the failedResults class variable to the given ArrayList:
	public void setFailedResults(ArrayList<String[]> failedResults) {
		this.failedResults = failedResults;
	}
	
	
	// Method to set the number of custom tests that the client has done in the current round:
	public void setNumberOfTests(int number) {
		numberOfTests = number;
	}
	
	
	// Method to get the number of custom tests that the client has done in the current round:
	public int getNumberOfTests() {
		return numberOfTests;
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

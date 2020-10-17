package server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

public class Player {
	
	private Client client;

	private String username;
	private String status = "Waiting";

	private int currentScore = 0;
	private int totalScore = 0;
	private int numberPlayed = 0;
	private int numberWon = 0;
	private int numberOfTests = 0;
	
	private ArrayList<String[]> failedResults = new ArrayList<String[]>(); // Create an array list to store the client's failed test results.
	
	
	// Constructor which is called when this object is created:
	public Player(String username, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
		this.username = username;
		this.client = new Client(socket, inputStream, outputStream);
	}
	
	public Client getClient() {
		return client;
	}
	
	// Method to get the client's username:
	public String getUsername() {
		return username;
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
		
		client.sendData("TRS"+serialisedFailedResults); // Send the failed results to the client.
		
		failedResults.removeAll(failedResults); // Reset the failed results ArrayList.
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
	
	
}

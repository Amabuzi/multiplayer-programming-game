package server; //TODO modularise

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class ClientAccepter implements Runnable {
	
	private ServerSocket serverSocket; // Create a ServerSocket variable used to accept clients.
	private ArrayList<Client> newClients = new ArrayList<Client>(); // Create an ArrayList to temporarily store created Client objects.
	private ArrayList<String> usernames = new ArrayList<String>(); // Create an ArrayList to store the username of each client.
	private String serverName; // Create a variable used to store the server's name so that it can send it to the clients who join.
	
	private String bannedPlayersFilePath; // Create a variable used to store the path of the banned players file.
	private ArrayList<String[]> bannedPlayers = new ArrayList<String[]>(); // Create an ArrayList to store the IP addresses and usernames of banned players.
	private boolean bannedPlayersFileLoadSucceeded; // Create a boolean used to check whether the banned players file was successfully loaded or not.
	
	// Constructor which is called when this object is created:
	public ClientAccepter(ServerSocket serverSocket, String serverName) {
		
		// Store the parameters as class variables:
		this.serverSocket = serverSocket;
		this.serverName = serverName;
		
		loadBannedPlayers(); // Load the banned players file.
		
		new Thread(this).start(); // Start this object's run() method in a new thread.
	}
	
	
	// Method called when this object's thread is started:
	public void run() {
		while (true) { // Loop forever.
			
			// Create variables for the client's socket and I/O streams:
			Socket clientSocket;
			DataInputStream inputStream;
			DataOutputStream outputStream;
			
			try { // The following block of code may throw exceptions which must be caught.
				clientSocket = serverSocket.accept(); // Wait for a client to attempt to connect to the server, then accept them and store their socket.
				inputStream = new DataInputStream(clientSocket.getInputStream()); // Get the client's input stream and store it as a DataInputStream.
				outputStream = new DataOutputStream(clientSocket.getOutputStream()); // Get the client's output stream and store it as a DataOutputStream.
			}
			
			// If an exception was caught, meaning the client failed to connect:
			catch (IOException e) {
				e.printStackTrace(); // Print the stack trace for debugging purposes.
				continue; // Skip to the next iteration of the while loop.
			}
			
			try { // The following block of code may throw exceptions which must be caught.
				
				// Set the socket timeout to 500ms, so that reading from the input stream will timeout after 500ms (to make
				// sure that a bugged client program that won't send their username won't cause the ClientAccepter to hang):
				clientSocket.setSoTimeout(500);
				
				String username = inputStream.readUTF().substring(3); // Read from the input stream, take away the "data type" part, and store the data as the String username.
				
				// Change the socket timeout back to 0 (no timeout), as any further reading from the input stream will be in its own thread so won't cause the program to hang:
				clientSocket.setSoTimeout(0);
				
				String ipAddress = clientSocket.getRemoteSocketAddress().toString(); // Get the IP address of the client.
				ipAddress = ipAddress.substring(1, ipAddress.indexOf(":")); // Isolate the IP address from the port.
				
				boolean banned = false; // Create a variable to store whether the player is banned or not.
				
				// Iterate through each banned player's IP address:
				for (int i = 0; i < bannedPlayers.size(); i++) {
					
					// If the IP addresses match, set banned to true:
					if (bannedPlayers.get(i)[0].equals(ipAddress)) {
						banned = true;
					}
				}
				
				// If the player is banned:
				if (banned) {
					
					// Tell the client that they are banned:
					outputStream.writeUTF("BND");
					outputStream.flush();
					
					clientSocket.close(); // Disconnect the client.
					
					continue; // Skip to the next iteration of the while loop.
				}
				
				// If the username is taken, empty or more than 10 characters long, tell the client: 
				if (usernames.contains(username) || username.length() > 10 || username.length() == 0) {
					outputStream.writeUTF("USVfalse");
					outputStream.flush();
				}
				
				// If the username is valid:
				else {
					
					// Tell the client that their username was valid:
					outputStream.writeUTF("USVtrue");
					outputStream.flush();
					
					usernames.add(username); // Add the username to the ArrayList of usernames.
					
					Client client = new Client(username, clientSocket, inputStream, outputStream); // Create a Client object for the client.
					
					newClients.add(client); // Add the client to the ArrayList of temporary Client objects.
					
					// Send the server's name to the client:
					outputStream.writeUTF("SVN"+serverName);
					outputStream.flush();
				}
			}
			
			// If an exception is caught (the client didn't send their username in time, or they have disconnected):
			catch (IOException e) {
				System.out.println("Client "+clientSocket.getRemoteSocketAddress().toString()+" disconnected."); // Print the IP address of the client for debugging purposes.
				e.printStackTrace(); // Print the stack trace for debugging purposes.
			}
		}
	}
	
	
	// Method to check if there are Client object which have not yet been handled by Main.java:
	public boolean hasNewClients() {
		return !newClients.isEmpty();
	}
	
	
	// Method to get the Client objects which have not yet been handled by Main.java:
	public ArrayList<Client> getNewClients() {
		ArrayList<Client> tempNewClients = new ArrayList<Client>(newClients); // Copy the newClients ArrayList to a temporary variable.
		newClients.removeAll(newClients); // Remove all of the elements from newClients.
		return tempNewClients; // Return the temporary variable.
	}
	
	
	// Method to remove a username from the ArrayList of usernames:
	public void removeUsername(String username) {
		usernames.remove(username);
	}
	
	
	// Method to load the banned player file:
	public void loadBannedPlayers() {
		try { // The following code may throw an exception which must be caught.
			
			// If the program is being run from a .jar file, set the file path to the absolute path outside of the .jar file:
			if (getClass().getResource("Main.class").toString().contains("jar!")) {
				String externalDirectory = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").toURI().toString().replaceFirst("file:", ""), "UTF-8");
				bannedPlayersFilePath = externalDirectory+"/PythonGame/bannedPlayers.txt";
			}
			
			// If the program is being run from inside Eclipse, Set the file path to the relative path within the Eclipse project:
			else {
				bannedPlayersFilePath = "bannedPlayers.txt"; 
			}
			
			// Create a BufferedReader to read the file:
			BufferedReader bufferedReader = new BufferedReader(new FileReader(bannedPlayersFilePath));
			
			// Get the IP address and username of the first banned player:
			String ipAddress = bufferedReader.readLine();
			String username = bufferedReader.readLine();
			
			// While there is an IP address and username of the current banned player:
			while (ipAddress != null && username != null) {
				bannedPlayers.add(new String[] {ipAddress, username}); // Add the IP address and username to an arraylist.
				
				// Get the IP address and username of the next banned player:
				ipAddress = bufferedReader.readLine();
				username = bufferedReader.readLine();
			}
			
			bufferedReader.close(); // Close the BufferedReader to free resources.
			
			bannedPlayersFileLoadSucceeded = true; // Set bannedPlayersFileLoadSucceeded to true as the file was successfully loaded.
		}
		
		// If an exception was caught:
		catch (IOException | URISyntaxException e) {
			e.printStackTrace(); // Print the stack trace for debugging purposes.
			bannedPlayersFileLoadSucceeded = false; // Set bannedPlayersFileLoadSucceeded to false as the file was not successfully loaded.
		}
	}
	
	
	// Method to check if the banned players file was successfully loaded:
	public boolean bannedPlayersFileLoadSucceeded() {
		return bannedPlayersFileLoadSucceeded;
	}
	
	
	// Method to return the IP addresses and usernames of banned players:
	public ArrayList<String[]> getBannedPlayers() {
		return bannedPlayers;
	}
	
	
	// Method to ban a player:
	public boolean banPlayer(String[] playerDetails) {
		bannedPlayers.add(playerDetails); // Add the player's details to the arraylist.
		
		try { // The following code may throw an exception which must be caught.
			
			FileWriter fileWriter = new FileWriter(bannedPlayersFilePath, true); // Create a FileWriter to write to the file.
			fileWriter.write(playerDetails[0] + "\r\n" + playerDetails[1] + "\r\n"); // Write the players details to the file.
			fileWriter.close(); // Close the FileWriter to free resources.
			
			return true; // Return true as the method has succeeded.
		}
		
		// If an exception was caught, print the stack trace for debugging purposes and return false as the method has failed:
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	// Method to unban a player:
	public boolean unbanPlayer(String ipAddress) {
		
		// Iterate through each banned player:
		for (int i = 0; i < bannedPlayers.size(); i++) {
			
			// If the IP address of the banned player matched that of the player which is being unbanned, remove it from the arraylist:
			if (bannedPlayers.get(i)[0].equals(ipAddress)) {
				bannedPlayers.remove(i);
			}
		}
		
		try { // The following code may throw an exception which must be caught.
			
			// Create a BufferedReader to read the banned players file:
			BufferedReader bufferedReader = new BufferedReader(new FileReader(bannedPlayersFilePath));
			
			String line = ""; // Create a string used to store a line of the file.
			String newFileContents = ""; // Create a string used to store the new contents of the file.
			
			line = bufferedReader.readLine(); // Read the first line from the file.
			
			// While the last read line is not null:
			while (line != null) {
				
				// If the line does not match the IP address of the player to be banned:
				if (!line.equals(ipAddress)) {
					newFileContents += line + "\r\n"; // Add the IP address to the new file contents.
					newFileContents += bufferedReader.readLine() + "\r\n"; // Add the username to the new file contents.
				}
				
				// If the line matches the IP address of the player to be banned:
				else {
					bufferedReader.readLine(); // Read the username but do not store it, effectively skipping this player.
				}
				
				line = bufferedReader.readLine(); // Read the next line from the file.
			}
			
			bufferedReader.close(); // Close the BufferedReader to free resources.
			
			FileWriter fileWriter = new FileWriter(bannedPlayersFilePath, false); // Create a FileWriter to write to the banned players file.
			fileWriter.write(newFileContents); // Write the new file contents to the file.
			fileWriter.close(); // Close the FileWriter to free resources.
			
			return true; // Return true as the method has succeeded.
		}
		
		// If an exception was caught, print the stack trace for debugging purposes and return false as the method has failed:
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}

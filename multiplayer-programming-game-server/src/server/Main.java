package server; //TODO modularise

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main implements ActionListener, Runnable {
	
	private GUI gui; // Needed to be able to interact with the GUI.
	private ClientAccepter clientAccepter; // Needed to be able to interact with the ClientAccepter.
	
	// Store all of the Client objects, which are needed to interact with the clients:
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	private Problem problem; // Needed to load the problem details and to score solutions.

	private SolutionRunner solutionRunner;
	
	// Needed to pass to Problem when it is created, so that it can execute python code:
	private PythonExecutor pythonExecutor;
	
	private long roundEndTime; // Stores the time that the round will end, in order to check whether it has ended or not.
	private long roundStartTime; // Stores the time that the round started, in order to calculate the time taken to submit.
	private boolean inRound = false; // Stores whether a round is currently occurring or not.
	private int gameMode; // Stores the game mode of the current round.
	
	// Constructor which is called when this object is created:
	public Main() {
		gui = new GUI(); // Initialise the GUI object. This displays a GUI.
		gui.setActionListener(this); // Pass the current Main object to the GUI object, so that its buttons can make this object their action listener.
		
		loadProblems(); // Call loadProblems().
	}
	
	
	// Method called when this object's thread is started:
	public void run() {
		
		// Loop forever:
		while (true) {
			
			// If the client accepter has clients which have not yet been handled:
			if (clientAccepter.hasNewClients()) {
				ArrayList<Client> newClients = clientAccepter.getNewClients(); // Get the new Client objects in the form of an ArrayList.
				
				// Loop through all of the new clients and update the player table with their information:
				for (int i = 0; i < newClients.size(); i++) {
					Client client = newClients.get(i);
					updatePlayerTables(client);
				}
				
				// Get and store all of the player data from the player table in the form of an Object[][]:
				Object[][] allPlayerData = gui.getAllPlayerData(); 
				
				// Create a new Object[][] which will store all of the player data except IP addresses, which will be sent to the new clients:
				Object[][] clientAllPlayerData = new Object[allPlayerData.length][7];
				
				// Loop through all of the player data 'rows':
				for (int j = 0; j < allPlayerData.length; j++) {
					Object[] playerData = allPlayerData[j]; // Get and store the Object[] for the row at position j.
					
					// Create a new Object[], clientPlayerData, which will be the player data that is sent to the clients.
					Object[] clientPlayerData = new Object[7];
					
					// Make the first element of clientPlayerData equal to the first element of playerData.
					clientPlayerData[0] = playerData[0];
					
					// Loop through clientPlayerData and add the elements of playerData, leaving out the second element (the IP address).
					for (int k = 1; k <= 6; k++) {
						clientPlayerData[k] = playerData[k+1];
					}
					
					clientAllPlayerData[j] = clientPlayerData; // Add the Object[] clientPlayerData to the Object[][] allClientPlayerData.
				}
				
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Create a ByteArrayOutputStream.
				try { // The following block of code could throw an exception which must be caught.
					
					// Create an ObjectOutputStream with the ByteArrayOutputStream:
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
					
					objectOutputStream.writeObject(clientAllPlayerData); // Write the Object[][] allClientPlayerData to the ObjectOutputStream.
				}
				
				// If an exception was thrown:
				catch (IOException e) {
					e.printStackTrace(); // Print the stack trace for debugging purposes.
				}
				
				byte[] bytes = byteArrayOutputStream.toByteArray(); // Convert and store the ByteArrayOutputStream to a byte[] array.
				
				// Using Base64, encode the byte[] array to a String to create the serialised player data string:
				String serialisedPlayerData = Base64.getEncoder().encodeToString(bytes); 
				
				// Loop through the new clients and send the serialised player data to each one:
				for (int i = 0; i < newClients.size(); i++) {
					Client client = newClients.get(i);
					client.sendData("APD"+serialisedPlayerData);
				}
				
				// Add all of the new Client objects to the ArrayList of clients.
				clients.addAll(newClients);
			}
			
			// If a round is currently occurring and the current time is after the round's end time, end the round by calling endRound():
			if (inRound && System.currentTimeMillis() > roundEndTime) {
				endRound();
			}
			
			// Loop through all of the clients:
			for (int i = 0; i < clients.size(); i++) {
				Client client = clients.get(i); // Get the client at position i of the ArrayList.
				
				// If the client is connected:
				if (client.isConnected()) {
					
					// If the client has sent data that has not yet been handled:
					if (client.hasData()) {
						String dataType = client.getDataType(); // Get the "data type" portion of the data.
						String data = client.getData(); // Get the data portion of the data.
						
						// If the data type is a submission:
						if (dataType.equals("SMS")) {
							
							// If the client's status is "Coding" (if they are in a round have haven't yet submitted):
							if (client.getStatus().equals("Coding")) {
								
								long submissionTime = System.currentTimeMillis(); // Store the current time as the submission time.
								
								// Score the solution and store the results:
								int[] results = solutionRunner.scoreSolution(data, gameMode, roundStartTime, roundEndTime, submissionTime);
								
								client.addScore(results[0]); // Pass the score to the Client's addScore() method.
								ArrayList<String[]> failedResults = solutionRunner.getFailedResults(); // Get the client's failed test results.
								client.setFailedResults(failedResults); // Pass these failed test results to the Client's setFailedResults() method.
								client.setStatus("Waiting"); // Set the client's status to "Waiting".
								
								updatePlayerTables(client); // Update the player table to add the changes to the client's score and status.
								
								client.sendData("EFP"+Integer.toString(results[1])); // Send the client's efficiency points to the client.
								client.sendData("NOP"+Integer.toString(results[2])); // Send the client's number of passes to the client.
							}
						} 
						
						// If the data type is a hint request:
						else if (dataType.equals("HRQ")) {
							
							// If the client's status is "Coding" (if they are in a round have haven't yet submitted):
							if (client.getStatus().equals("Coding")) {
								client.sendData("HNT"+problem.getHint()); // Send a hint to the client.
								client.addScore(-20); // Take 20 score away from the client.
								
								updatePlayerTables(client); // Update the player table to add the changes to the client's score.
							}
						}
						
						// If the data type is a list of custom test inputs:
						else if (dataType.equals("CTI")) {
							
							// If the client hasn't used up their 5 tests for this round:
							if (client.getNumberOfTests() < 5) {
								String inputsString = data; // Store the inputs (data from Client.java) in a new variable.
								String[] inputs = inputsString.split(","); // Split the single string storing the inputs into a new array.
								
								long timeBefore = System.currentTimeMillis(); // Store the current time.
								
								// Loop until more data is received or until it has been more than a second since the loop started:
								while (!client.hasData() && System.currentTimeMillis() < timeBefore+1000) {
									try {
										Thread.sleep(100); // Wait 100ms before checking again to reduce CPU usage.
									} catch (InterruptedException e) {}
								}
								
								// If it has been more than a second since the loop started, print an error message:
								if (System.currentTimeMillis() > timeBefore+1000) {
									System.out.println("Client "+client.getUsername()+" (IP: "+client.getIPAddress()+") "
											+ "didn't send their solution after a test request.");
								}
								
								// If the client sent a solution:
								else if (client.getDataType().equals("TSM")) {
									
									// Create a python file with the player's solution:
									pythonExecutor.createFile("solution.py", problem.getSetupCode(), client.getData());
									
									// Execute the created with the given inputs and store the output:
									String result = pythonExecutor.executeFile("solution.py", inputs);
									
									client.sendData("TIP"+inputsString); // Send the inputs (as a single string) back to the client.
									client.sendData("TOP"+result); // Send the output of the execution to the client.
									client.setNumberOfTests(client.getNumberOfTests() + 1); // Increment the client's number of tests.
								}
								
								// If the client sent something else, print an error message:
								else {
									System.out.println("ERROR: Client "+client.getUsername()+" (IP: "+client.getIPAddress()+") "
											+"sent "+client.getDataType()+", expected TSM.");
								}
							}
							
							// If the client has used up all of their tests:
							else {
								client.sendData("TIP"+data); // Send the inputs back to the client.
								client.sendData("TOP"+"Only 5 tests are allowed per round."); // Send a message to the client in place of the output.
							}
						}
					}
				}
				
				// If the client is not connected:
				else {
					client.setStatus("Disconnected"); // Change the client's status to "Disconnected".
					updatePlayerTables(client); // Update the player tables so the change above is added.
					
					// Remove the client's username from the ArrayList of usernames in the ClientAccepter, so that it can be used again:
					clientAccepter.removeUsername(client.getUsername());
					
					clients.remove(client); // Remove the client from the ArrayList of clients.
				}
			}
			
			// If a round is currently occurring:
			if (inRound) {
				boolean allSubmitted = true; // Boolean to store whether all client have submitted. For now, assume every client has submitted.
				for (int i = 0; i < clients.size(); i++) { // Loop through each client.
					Client client = clients.get(i); // Get the client at position i.
					
					// If the client's status is "Coding" (if they are in a round have haven't yet submitted):
					if (client.getStatus().equals("Coding")) {
						allSubmitted = false; // Change 'allSubmitted' to false as there is a client who has not submitted.
						break; // Break out of the loop as there is no point checking the rest of the clients.
					}
				}
				
				// If all clients have submitted, end the round by calling endRound().
				if (allSubmitted) {
					endRound();
				}
			}
			
			// Constantly checking the if statements' conditions uses power and slows down the PC, so the thread is paused for 20 milliseconds:
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// Method to handle what happens when a round has ended:
	private void endRound() {
		gui.endRound(); // Call the GUI object's endRound() so that the GUI can do its changes.
		
		int highScore = 0; // Create a variable for the high-score. Give it a temporary value of 0.
		for (int i = 0; i < clients.size(); i++) { // Loop through each client.
			Client client = clients.get(i); // Get the client at position i of the ArrayList.
			int score = client.getCurrentScore(); // Get the score of that client.
			
			// If this score is greater than the high-score, make this the new high-score:
			if (score > highScore) {
				highScore = score;
			}
		}
		
		for (int i = 0; i < clients.size(); i++) { // Loop through each client.
			Client client = clients.get(i); // Get the client at position i of the ArrayList.
			int score = client.getCurrentScore(); // Get the score of that client.
			
			// If the score of the client matches the high-score, call updateStats() on the client, passing true to indicate that they won:
			if (score == highScore) {
				client.updateStats(true);
			}
			
			// If the score of the client doesn't match the high-score, call updateStats() on the client, passing false to indicate that they didn't win:
			else {
				client.updateStats(false);
			}
			
			updatePlayerTables(client); // Update the player table so that the change to the client's stats is added.
			client.sendData("RED"); // Send to the client an indicator that tells them that the round has ended.
		}
		
		inRound = false; // Update the boolean 'inRound' as a round is no longer occurring.
	}
	
	
	// Method to update the GUI's player table and send the update to each client:
	private void updatePlayerTables(Client client) {
		
		// Create an Object[] array, playerData, which stores data about the given Client object:
		Object[] playerData = new Object[8];
		playerData[0] = client.getUsername();
		playerData[1] = client.getIPAddress();
		playerData[2] = client.getStatus();
		playerData[3] = client.getCurrentScore();
		playerData[4] = client.getTotalScore();
		playerData[5] = client.getAverageScore();
		playerData[6] = client.getNumberPlayed();
		playerData[7] = client.getNumberWon();
		
		gui.updatePlayerTable(playerData); // Pass this player data to the GUI object so it can be displayed on the GUI.
		
		Object[] clientPlayerData = new Object[7]; // Create a new Object[], clientPlayerData, which will be the player data that is sent to the clients.
		clientPlayerData[0] = playerData[0]; // Make the first element of clientPlayerData equal to the first element of playerData.
		
		//Loop through clientPlayerData and add the elements of playerData, leaving out the second element (the IP address).
		for (int i = 1; i <= 6; i++) {
			clientPlayerData[i] = playerData[i+1];
		}
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Create a ByteArrayOutputStream.
		try { // The following block of code could throw an exception which must be caught.
			
			// Create an ObjectOutputStream with the ByteArrayOutputStream:
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			
			objectOutputStream.writeObject(clientPlayerData); // Write the Object[] clientPlayerData to the ObjectOutputStream.
		}
		
		// If an exception was thrown:
		catch (IOException e) {
			e.printStackTrace(); // Print the stack trace for debugging purposes.
		}
		
		byte[] bytes = byteArrayOutputStream.toByteArray(); // Convert and store the ByteArrayOutputStream to a byte[] array.
		
		// Using Base64, encode the byte[] array to a String to create the serialised player data string:
		String serialisedPlayerData = Base64.getEncoder().encodeToString(bytes); 
		
		// Loop through each client and send the serialised player data to them:
		for (int i = 0; i < clients.size(); i++) {
			client = clients.get(i);
			client.sendData("PYD"+serialisedPlayerData);
		}
	}
	
	
	// Method which is called when an action occurs in the GUI:
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand(); // Get and store the action command used to determine what the action was.
		
		// If the action was pressing the button to start the server:
		if (command.equals("startserver")) {
			String[] serverStartInformation = gui.getServerStartInformation(); // Get the information that the user entered from the GUI.
			
			// The code in this block may throw exceptions that I want to catch:
			try {
				// Show on the GUI that the server is initialising:
				gui.setServerStatus("Initialising server");
				gui.setServerStatusIndicatorColour(255, 255, 0);
				
				String serverName = serverStartInformation[0]; // Get the server name that the user entered.
				
				// If the name's length is 0 (empty) or more than 20 (too long), show an error on the GUI and stop trying to start the server:
				if (serverName.length() == 0 || serverName.length() > 20) {
					gui.setServerStatus("Invalid server name");
					gui.setServerStatusIndicatorColour(255, 0, 0);
					return;
				}
				
				String pythonLocation = serverStartInformation[2]; // Get the python location that the user entered.
				
				// If the python location doesn't contain a python.exe file, show an error on the GUI and stop trying to start the server:
				if (!(new File(pythonLocation+"\\python.exe").exists())) {
					gui.setServerStatus("Invalid python location");
					gui.setServerStatusIndicatorColour(255, 0, 0);
					return;
				}
				
				// Attempt to create a socket using the port entered by the user:
				ServerSocket serverSocket = new ServerSocket(Integer.parseInt(serverStartInformation[1]));
				
				// Create a ClientAccepter in order to accept clients:
				clientAccepter = new ClientAccepter(serverSocket, serverName);
				
				// If the banned players file was successfully loaded:
				if (clientAccepter.bannedPlayersFileLoadSucceeded()) {
					
					ArrayList<String[]> bannedPlayers = clientAccepter.getBannedPlayers(); // Get the banned players.
					
					// Add each banned player to the banned player table:
					for (String[] playerDetails: bannedPlayers) {
						gui.addBannedPlayer(playerDetails);
					}
				}
				
				// Create a PythonExecutor object with an execution time limit of 500ms:
				pythonExecutor = new PythonExecutor(pythonLocation, 500);
				
				// Show on the GUI that the server has started:
				gui.setServerStatus("Server is online");
				gui.setServerStatusIndicatorColour(0, 255, 0);
				
				// Disable the elements used to start the server as they should no longer be used:
				gui.disableServerStartControls();
				
				new Thread(this).start(); // Start this object's run() method in its own thread.
			}
			
			// If the socket cannot be created, show this on the GUI:
			catch (IOException e) {
				gui.setServerStatus("Unable to start server");
				gui.setServerStatusIndicatorColour(255, 0, 0);
			}
			
			// If the port was not an integer, show this on the GUI:
			catch (NumberFormatException e) {
				gui.setServerStatus("Invalid port");
				gui.setServerStatusIndicatorColour(255, 0, 0);
			}
		}
		
		// If the action was pressing the button to start the round:
		else if (command.equals("startround")) {
			String id = gui.getFirstProblemID(); // Get the ID of the problem at the top of the problem queue table.

			problem = ProblemLoader.loadProblem(id);

			solutionRunner = new SolutionRunner(problem, pythonExecutor);
			
			// Create a variable which stores the time that the round will start, and set this to 5 seconds from the current time:
			roundStartTime = System.currentTimeMillis() + 5000;
			
			// If the user didn't enter a time limit for the round:
			if (gui.getTime() == 0) {
				
				// Calculate the time that the round will end by adding the time limit stated in the problem file to round's start time.
				roundEndTime = roundStartTime + problem.getTimeLimit() * 1000;
				
				gui.setTime(roundEndTime-roundStartTime); // Show the time limit on the GUI.
			}
			
			// If the user entered a time which isn't invalid, calculate the time that the round will end using their entered time:
			else if (gui.getTime() != -1) {
				roundEndTime = roundStartTime + gui.getTime();
			}
			
			// If the user entered an invalid time, tell them using the GUI and stop attempting to start the round.
			else {
				gui.setRoundStatus("Invalid time entered.");
				return;
			}
			
			// Show the countdown for the round on the GUI:
			gui.startRoundCountdown(problem.getTitle(), problem.getDescription(), roundStartTime, roundEndTime);
			
			gameMode = gui.getChosenMode(); // Get and store the chosen game mode from the GUI.
			
			// Loop through the ArrayList of Client objects:
			for (int i = 0; i < clients.size(); i++) {
				Client client = clients.get(i); // Get the Client object in index i of the ArrayList.
				
				// If the client is connected:
				if (client.isConnected()) {
					client.sendData("NRS"); // Tell the client that a new round is starting.
					
					// Send the problem's details to the client:
					client.sendData("PLT"+problem.getTitle());
					client.sendData("PLD"+problem.getDescription());
					client.sendData("RST"+roundStartTime);
					client.sendData("RET"+roundEndTime);
					client.sendData("VBN"+problem.getVariableNamesAsString());
					client.sendData("GMD"+Integer.toString(gameMode));
					
					client.setStatus("Coding"); // Change the client's status to "Coding".
					updatePlayerTables(client); // Update the player tables so the change above is added.
				}
				
				// If the client is not connected:
				else {
					client.setStatus("Disconnected"); // Change the client's status to "Disconnected".
					updatePlayerTables(client); // Update the player tables so the change above is added.
					
					// Remove the client's username from the ArrayList of usernames in the ClientAccepter, so that it can be used again:
					clientAccepter.removeUsername(client.getUsername());
					
					clients.remove(client); // Remove the client from the ArrayList of clients.
				}
				
				inRound = true; // Change the boolean 'inRound' to true as a round has currently started.
				gui.removeFirstProblem(); // Move the problem at the top of the problem queue table to the bottom.
			}
		}
		
		// If the action was pressing the button to default the time:
		else if (command.equals("default")) {
			String problemID = gui.getFirstProblemID(); // Get the ID of the problem at the top of the problem queue table.
			
			try { // The following block of code throws exceptions which must be caught.
				
				// Create and pass the problem's file path to a new FileReader, and pass this to a new BufferedReader, in order to read the file:
				BufferedReader bufferedReader = new BufferedReader(new FileReader("Problems\\problem#"+problemID+".txt"));
				
				// Read the first two lines, but as they are not used, do not store them:
				bufferedReader.readLine();
				bufferedReader.readLine();
				
				// Read the next line, which is the time limit of the problem in seconds, and calculate and store the millisecond value:
				int timeLimit = Integer.parseInt(bufferedReader.readLine()) * 1000;
				
				bufferedReader.close(); // Close the BufferedReader to free memory.
				gui.setTime(timeLimit); // Show the time limit on the GUI.
			}
			
			// If the file couldn't be read, show an error to the user on the GUI.
			catch (IOException e) {
				e.printStackTrace();
				gui.setRoundStatus("Unable to get default time");
			}
		}
		
		// If the action was pressing the button to save a problem:
		else if (command.equals("saveedit")) {
			String[][] newProblemDetails = gui.getNewProblemDetails(); // Get the inputs from the edit problem dialog.
			
			// If the inputs weren't all inputed, show an error message and return:
			if (newProblemDetails == null) {
				gui.showMessage("All fields are required.");
				return;
			}
			
			// Store each input (except the variables table) into strings:
			String id = newProblemDetails[0][0];
			String title = newProblemDetails[0][1];
			String description = newProblemDetails[0][2];
			String hint = newProblemDetails[0][3];
			String timeLimit = newProblemDetails[0][4];
			String solution = newProblemDetails[0][5];
			
			// If the title, description or hint were too long, show an error message and return:
			if (title.length() > 30 || description.length() > 200 || hint.length() > 100) {
				gui.showMessage("The title, description and hint must be less than 30, 200 and 100 characters respectively.");
				return;
			}
			
			// If the time limit isn't an integer, show an error messagge and return:
			try {
				Integer.parseInt(timeLimit);
			} catch (NumberFormatException e) {
				gui.showMessage("The time limit must be an integer.");
				return;
			}
			
			int numberOfVariables = 1; // Create a variable to store the number of variables that the user entered:
			
			// If the "# of tests" cell in the first row is empty, show an error message and return:
			if (newProblemDetails[1][0] == null || newProblemDetails[1][0] == "") {
				gui.showMessage("Variables have been entered incorrectly. (1)");
				return;
			}
			
			// Iterate through the second row onwards:
			for (int i = 2; i < newProblemDetails.length; i++) {
				
				// If the "# of tests" cell is not empty:
				if (!(newProblemDetails[i][0] == null || newProblemDetails[i][0].equals(""))) {
					numberOfVariables = i-1; // Calculate and store the number of variables.
					break; // Break out of the loop.
				}
				
				// If all of the "# of tests" cells are empty (except the first):
				else if (i == newProblemDetails.length - 1) {
					numberOfVariables = i; // Set the number of variables to the number of rows in the table.
				}
			}
			
			int lastEntry = numberOfVariables+1; // Create a variable to store the last found non-empty "# of tests" cell.
			
			// Iterate through the rows:
			for (int i = lastEntry + 1; i < newProblemDetails.length; i++) {
				
				// If the "# of tests" cell is not empty:
				if (!(newProblemDetails[i][0] == null || newProblemDetails[i][0].equals(""))) {
					
					System.out.println(i);
					System.out.println(lastEntry);
					System.out.println(numberOfVariables);
					
					// If the space between the current row and the last entry is not the same as the first time, show an error message and return:
					if (!(i - lastEntry == numberOfVariables)) {
						gui.showMessage("Variables have been entered incorrectly. (2)");
						return;
					}
					
					lastEntry = i; // Update the last entry.
				}
			}
			
			
			String[] variableNames = new String[numberOfVariables]; // Create a string to store the variable names.
			
			// Iterate through the rows of the first test and store each variable name cell in the array:
			for (int i = 1; i < numberOfVariables + 1; i++) {
				variableNames[i-1] = newProblemDetails[i][1];
			}
			
			
			// Iterate through the rest of the rows:
			for (int i = numberOfVariables + 1; i < newProblemDetails.length; i++) {
				
				// If the variable name for this test is not the same as the last tests', show an error message and return:
				if (!(newProblemDetails[i][1].equals(newProblemDetails[((i - 1) % numberOfVariables) + 1][1]))) {
					gui.showMessage("Variable names have been entered incorrectly.");
					return;
				}
			}
			
			
			// Iterate through each row:
			for (int i = 1; i < newProblemDetails.length; i++) {
				
				// Calculate the range and if this is negative then show an error message and return:
				if (Integer.parseInt(newProblemDetails[i][3]) - Integer.parseInt(newProblemDetails[i][2]) < 0) {
					gui.showMessage("Variable ranges have been entered incorrectly.");
					return;
				}
			}
			
			
			String generationInstructions = ""; // Create a string to store the input generation instructions.
			
			generationInstructions += numberOfVariables + "|"; // Add the number of variables to the generation instructions.
			
			// Add each variable name to the generation instructions:
			for (String variableName: variableNames) {
				generationInstructions += variableName + "|";
			}
			
			int totalNumberOfTests = 0; // Create an integer to count the total number of tests specified.
			
			// Iterate through each row, skipping the spaces in "# of tests":
			for (int i = 1; i < newProblemDetails.length; i += numberOfVariables) {
				
				generationInstructions += newProblemDetails[i][0] + "|"; // Add the "# of tests" cell to the generation instructions.
				
				// Attempt to add the number of tests to the total number of tests:
				try {
					totalNumberOfTests += Integer.parseInt(newProblemDetails[i][0]);
				}
				
				// If the number of tests was not an integer, show an error message and return:
				catch (NumberFormatException e) {
					gui.showMessage("Non-integer detected in \"# of tests\".");
					return;
				}
				
				// Iterate through the ranges of each variable and add them to the generation instructions:
				for (int j = i; j < i + numberOfVariables; j++) {
					generationInstructions += newProblemDetails[j][2] + "|";
					generationInstructions += newProblemDetails[j][3] + "|";
				}
			}
			
			// If the total number of tests was not 20, show an error message and return:
			if (totalNumberOfTests != 20) {
				gui.showMessage("Exactly 20 tests must be specified.");
				return;
			}
			
			String setupCode = ""; // Create a string to store the setup code for the solution.
			
			// Add imports to the setup code:
			setupCode += "import sys \n";
			setupCode += "import math \n";
			
			// Loop through each variable and set them to the argument given in the command-line:
			for (int i = 0; i < variableNames.length; i++) {
				setupCode += variableNames[i] + " = int(sys.argv[" + (i+1) + "]) \n";
			}
			
			// Delete the system import for security
			setupCode += "del sys\n";
			
			
			//The following code may throw an exception which must be caught:
			try {
				
				String problemDirectory = "Problems\\problem#"+id+".txt"; // Create a variable to store the path of the problem within the project folder.
				
				// If the program is being executed in a .jar file:
				if (getClass().getResource("Main.class").toString().contains("jar!")) {
				
					// Attempt to store the directory of the .jar file and use it to find the path of the problem outside of the .jar:
					try {
						String externalDirectory = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").toURI().toString().replaceFirst("file:", ""), "UTF-8");
						problemDirectory = externalDirectory+"/PythonGame/problem#"+id+".txt";
					}
					
					// If an exception is caught, print the stack trace (for debugging purposes), display an error message on the GUI and return:
					catch (UnsupportedEncodingException | URISyntaxException e) {
						e.printStackTrace();
						gui.showMessage("Unable to locate external directory.");
						return;
					}
				}
				
				// Create a FileWriter to create a file with the problem ID:
				FileWriter fileWriter = new FileWriter(problemDirectory, false);
				PrintWriter printWriter = new PrintWriter(fileWriter); // Create a PrintWriter with the FileWriter.
				
				// Write the title, description, time limit, hint and generation instructions to the file:
				printWriter.println(title);
				printWriter.println(description);
				printWriter.println(timeLimit);
				printWriter.println(hint);
				printWriter.println(generationInstructions);
				
				// Iterate through each line in the setup code and write them to the file:
				for(String line: setupCode.split("\n")) {
					printWriter.println(line);
				}
				
				// Iterate through each line in the solution and write them to the file:
				for(String line: solution.split("\n")) {
					printWriter.println(line);
				}
				
				// Close the PrintWriter and FileWriter:
				printWriter.close();
				fileWriter.close();
				
				
				gui.closeEditProblemDialog(); // Close the edit problem dialog.
				gui.showMessage("Problem saved successfully."); // Show a message on the GUI to tell the user that the problem was saved.
				
				// Pass the ID, title and description to the GUI to add it to the problem table:
				gui.addToProblemTable(new Object[] {id, title, description}); 
			}
			
			// If an exception was caught, show an error message:
			catch (IOException e) {
				gui.showMessage("Error saving file.");
			}
		}
		
		// If the action was pressing the "edit problem" button:
		else if (command.equals("editproblem")) {
			String id = gui.getSelectedProblemID(); // Get and store the ID of the selected problem.
			
			// If no problem is selected, show an error message and return:
			if (id == null) {
				gui.showMessage("Select a problem first.");
				return;
			}
			
			// Create variables for the problem's relevant data.
			String title, description, timeLimit, hint, generationInstructions, solution;
			
			try { // The following block of code may throw an exception which must be caught:
				
				String problemDirectory = "Problems\\problem#"+id+".txt"; // Create a variable to store the path of the problem within the project folder.
				
				// If the program is being executed in a .jar file:
				if (getClass().getResource("Main.class").toString().contains("jar!")) {
				
					// Attempt to store the directory of the .jar file and use it to find the path of the problem outside of the .jar:
					try {
						String externalDirectory = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").toURI().toString().replaceFirst("file:", ""), "UTF-8");
						problemDirectory = externalDirectory+"/PythonGame/problem#"+id+".txt";
					}
					
					// If an exception is caught, print the stack trace (for debugging purposes), display an error message on the GUI and return:
					catch (UnsupportedEncodingException | URISyntaxException e) {
						e.printStackTrace();
						gui.showMessage("Unable to locate external directory.");
						return;
					}
				}
				
				// Create and pass the problem's file path to a new FileReader, and pass this to a new BufferedReader, in order to read the file:
				BufferedReader bufferedReader = new BufferedReader(new FileReader(problemDirectory));
				
				// Read the title, description, time limit, hint and generation instructions from the first five lines of the file, and store them:
				title = bufferedReader.readLine();
				description = bufferedReader.readLine();
				timeLimit = bufferedReader.readLine();
				hint = bufferedReader.readLine();
				generationInstructions = bufferedReader.readLine().replaceAll(" ", "");
				
				solution = ""; // Initialise the solution as an empty string.
				String line = bufferedReader.readLine(); // Read and store the next line of the file.
				
				// While the last read line isn't null, add it to the solution and read the next line:
				while (line != null) { 
					solution += line + "\n";
					line = bufferedReader.readLine();
				}
				
				bufferedReader.close(); // Close the BufferedReader to free memory.
			}
			
			// If an exception was caught, show an error message and return.
			catch (IOException e) {
				gui.showMessage("Unable to read problem file.");
				return;
			}
			
			
			String[] parts = generationInstructions.split("[|]"); // Split the generation instructions into an array of parts.
			
			int numberOfVariables = Integer.parseInt(parts[0]); // Store the first part as the number of variables.
			
			// Calculate the number of rows that should be in the variables table using the number of parts and the number of variables:
			int numberOfRows = numberOfVariables * ((parts.length - numberOfVariables - 1) / (2 * numberOfVariables + 1));
			
			String[][] variables = new String[numberOfRows][4]; // Create a 2D array to store the cells of the variables table.
			
			String[] variableNames = new String[numberOfVariables]; // Create an array to store the names of the variables.
			
			// Iterate through the parts which store variable names and store them in the variable names array.
			for (int i = 0; i < numberOfVariables; i++) {
				variableNames[i] = parts[i+1];
			}
			
			
			int n = numberOfVariables + 1; // Create a variable to count how many parts have been read.
			
			// Iterate through each 'row' in the 2D array:
			for (int i = 0; i < numberOfRows; i += numberOfVariables) {
				
				// Store the next part in the "# of tests" column and increment the counter variable:
				variables[i][0] = parts[n];
				n++;
				
				// Iterate through each 'row' within the current "# of tests" entry:
				for (int j = 0; j < numberOfVariables; j++) {
					variables[i + j][1] = variableNames[j]; // Store the name of the variable in the "variable name" column.
					
					// Store the next part in the "lower limit" column and increment the counter variable:
					variables[i + j][2] = parts[n];
					n++;
					
					// Store the next part in the "upper limit" column and increment the counter variable:
					variables[i + j][3] = parts[n];
					n++;
				}
			}
			
			
			// Remove the setup code from the solution by finding the last newline in the setup code and using substring():
			int lastNewline = 0;
			for (int i = 0; i < 2 + numberOfVariables; i++) {
				lastNewline = solution.indexOf("\n", lastNewline + 1);
			}
			solution = solution.substring(lastNewline + 1);
			
			
			// Pass the problem's data to the GUI object so that it can open the edit problem dialog and display them:
			gui.openEditProblemDialog(id, title, description, hint, timeLimit, variables, solution);
		}
		
		// If the action was pressing the "kick" button:
		else if (command.equals("kick")) {
			String[][] selectedPlayers = gui.getSelectedPlayers(); // Get the selected players from the GUI object.
			
			// If no players were selected, show a message on the GUI and return:
			if (selectedPlayers == null) {
				gui.showMessage("Select one or more players first.");
				return;
			}
			
			// Iterate through each client:
			for (int i = 0; i < clients.size(); i++) {
				
				Client client = clients.get(i); // Get the client at index i.
				
				// Iterate through the selected players' details:
				for (String[] playerDetails: selectedPlayers) {
					
					// If the IP addresses match:
					if (client.getIPAddress().equals(playerDetails[0])) {
						
						client.sendData("KCD"); // Send an indication that the player has been kicked to the client.
						client.setStatus("Disconnected."); // Set the client's status to "Disconnected".
						updatePlayerTables(client); // Update the player tables with the new status.
						clientAccepter.removeUsername(client.getUsername()); // Remove the client's username from the arraylist of taken usernames.
						clients.remove(client); // Remove the client from the arraylist of clients.
						client.disconnect(); // Disconnect the client from the server.
					}
				}
			}
		}
		
		// If the action was pressing the "ban" button:
		else if (command.equals("ban")) {
			String[][] selectedPlayers = gui.getSelectedPlayers(); // Get the selected players from the GUI object.
			
			// If no players were selected, show a message on the GUI and return:
			if (selectedPlayers == null) {
				gui.showMessage("Select one or more players first.");
				return;
			}
			
			// Iterate through each client:
			for (int i = 0; i < clients.size(); i++) {
				
				Client client = clients.get(i); // Get the client at index i.
				
				// Iterate through the selected players' details:
				for (String[] playerDetails: selectedPlayers) {
					
					// If the IP addresses match:
					if (client.getIPAddress().equals(playerDetails[0])) {
						client.sendData("BND"); // Send an indication that the player has been banned to the client.
						
						client.setStatus("Disconnected."); // Set the client's status to "Disconnected".
						updatePlayerTables(client); // Update the player tables with the new status.
						
						clientAccepter.removeUsername(client.getUsername()); // Remove the client's username from the arraylist of taken usernames.
						clients.remove(client); // Remove the client from the arraylist of clients.
						
						client.disconnect(); // Disconnect the client from the server.
						
						clientAccepter.banPlayer(playerDetails); // Ban the player.
						
						gui.addBannedPlayer(playerDetails); // Show the player's details in the banned players table in the GUI.
					}
				}
			}
		}
		
		// If the action was pressing the "unban" button:
		else if (command.equals("unban")) {
			String[] selectedPlayers = gui.getSelectedBannedPlayers(); // Get the selected banned players from the GUI object.
			
			// If no players were selected, show a message on the GUI and return:
			if (selectedPlayers == null) {
				gui.showMessage("Select one or more players first.");
				return;
			}
			
			// Iterate through each selected player:
			for (int i = 0; i < selectedPlayers.length; i++) {
				
				// Unban the player and remove them from the banned players table:
				clientAccepter.unbanPlayer(selectedPlayers[i]);
				gui.removeBannedPlayer(selectedPlayers[i]);
			}
		}
	}
	
	
	// Method to read the title and description from each problem file and pass these details to the GUI object:
	private void loadProblems() {
		
		String externalDirectory = ""; // Create a string used to store the directory of the .jar file.
		
		// If the program is being executed in a .jar file:
		if (getClass().getResource("Main.class").toString().contains("jar!")) {
		
			// Attempt to store the directory of the .jar file:
			try {
				externalDirectory = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").toURI().toString().replaceFirst("file:/", ""),
						"UTF-8");
			}
			
			// If an exception is caught, print the stack trace (for debugging purposes), display an error message on the GUI and return:
			catch (URISyntaxException | UnsupportedEncodingException e) {
				e.printStackTrace();
				gui.showMessage("Unable to locate external directory.");
				return;
			}
		}
		
		// Attempt to create a folder in the same directory as the .jar file with the name "PythonGame" if it doesn't already exist:
		File file = new File(externalDirectory+"/PythonGame/");
		if (!file.exists()) {
			file.mkdirs();
		}
		
		
		int id = 0; // Counter variable to determine which is the current iteration.
		while (true) { // Loop until forcefully broken.
			id++; // Increase the counter variable.
			
			// Create a string which is 'id' with 3 digits, including leading 0s. This is the problem's ID:
			String problemID = String.format("%03d", id);
			
			String problemDirectory = "Problems\\problem#"+problemID+".txt"; // Create a variable to store the location of the problem file.
			
			// If the program is being executed in a .jar file:
			if (getClass().getResource("Main.class").toString().contains("jar!")) {
			
				InputStream inputStream = getClass().getResourceAsStream("/problem#"+problemID+".txt"); // Create an input stream for the problem file.
				
				// Create the directory of the problem file using the external directory:
				problemDirectory = externalDirectory+"PythonGame/problem#"+problemID+".txt";
						
				Path path = Paths.get(problemDirectory); // Create a path object for the problem directory.
				
				// Attempt to copy the file from within the .jar file to the external directory:
				if (inputStream != null) {
					try {
						Files.copy(inputStream, path);
					}
					
					// If the file already exists, return as the problems have already been copied:
					catch (FileAlreadyExistsException e) {
						return;
					}
					
					// If the file cannot be copied, print the stacktrace (for debugging purposes) but continue trying to read the problems:
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			BufferedReader bufferedReader; // Create a BufferedReader to read the file.
			
			try { // The following code throws exceptions which must be caught.
				
				// Pass the path of the problem file to a new FileReader, and initialise the BufferedReader with it:
				bufferedReader = new BufferedReader(new FileReader(problemDirectory));
				
				// Read and store the title and description of the problem:
				String title = bufferedReader.readLine();
				String description = bufferedReader.readLine();
				
				// Pass the ID, title and description of the problem, in the form of an Object[] array, to the GUI object:
				gui.addToProblemTable(new Object[] {problemID, title, description});
			}
			
			// If the file doesn't exist, this indicates there are no more problems so the loop is broken.
			catch (FileNotFoundException e) {
				break;
			}
			
			// If the file cannot be read for another reason, print the stacktrace (for debugging purposes) but continue trying to read the problems:
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// Method called when the program is first executed:
	public static void main(String[] args) {
		
		// Attempt to change the LookAndFeel of the program:
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new Main(); // Create a new object of this class.
	}
}

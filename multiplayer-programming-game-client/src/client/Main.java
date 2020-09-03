package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
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

public class Main implements Runnable, ActionListener {
	
	private GUI gui; // Needed to be able to interact with the GUI.
	private CommunicationHandler communicationHandler; // Needed to be able to interact with the CommunicationHandler.
	
	// Constructor which is called when this object is created:
	public Main() {
		gui = new GUI(); // Initialise the GUI object. This displays a GUI.
		gui.setActionListener(this); // Pass the current Main object to the GUI object, so that its buttons can make this object their action listener.
		gui.applySettings(readSettings()); // Read the settings file and pass the settings to the GUI object.
		
		copyHelpFile(); // Copy the help file to outside of the .jar file (if the program is running in a .jar file).
	}
	
	
	// Method called when this object's thread is started:
	public void run() {
		while (communicationHandler.isConnected() || communicationHandler.hasData()) { // Loop until the client disconnects.
			
			// If the server has sent data that has not yet been handled:
			if (communicationHandler.hasData()) {
				String dataType = communicationHandler.getDataType(); // Get the "data type" portion of the sent data.
				String data = communicationHandler.getData(); // Get the "data" portion of the sent data.
				
				// If the data type is the server's name, display it on the GUI:
				if (dataType.equals("SVN")) {
					gui.setServerName(data);
				}
				
				// If the data type is an indication that the round is starting:
				else if (dataType.equals("NRS")) {
					boolean success = newRound(); // Call newRound() and store whether it succeeded or not.
					
					// If newRound() didn't succeed, return from this method to end the program:
					if (!success) {
						return;
					}
				}
				
				// If the data type is the data of a player:
				else if (dataType.equals("PYD")) {
					byte[] bytes = Base64.getDecoder().decode(data); // Decode the string into a byte[] array using Base64.
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); // Create a ByteArrayInputStream to read the bytes.
					
					try { // The following code may throw an exception which must be caught.
						ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream); // Create an ObjectInputStream with the ByteArrayInputStream.
						Object[] playerData = (Object[]) objectInputStream.readObject(); // Read the player data from the stream.
						gui.updatePlayerTable(playerData); // Pass the player data to the GUI object to update the GUI's table.
					}
					
					// If an exception was caught, print the stack trace for debugging purposes:
					catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				// If the data type is the data of all players:
				else if (dataType.equals("APD")) {
					byte[] bytes = Base64.getDecoder().decode(data); // Decode the string into a byte[] array using Base64.
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); // Create a ByteArrayInputStream to read the bytes.
					
					try { // The following code may throw an exception which must be caught.
						ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream); // Create an ObjectInputStream with the ByteArrayInputStream.
						Object[][] allPlayerData = (Object[][]) objectInputStream.readObject(); // Read the players' data from the stream.
						
						// Loop through each player's data, passing each to the GUI object to update the GUI's table:
						for (int i = 0; i < allPlayerData.length; i++) {
							gui.updatePlayerTable(allPlayerData[i]);
						}
					}
					
					// If an exception was caught, print the stack trace for debugging purposes:
					catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				// If the data type is a hint, pass it to the GUI object so it can display it on the GUI:
				else if (dataType.equals("HNT")) {
					gui.showHint(data);
				}
				
				// If the data type is the client's failed test results:
				else if (dataType.equals("TRS")) {
					byte[] bytes = Base64.getDecoder().decode(data); // Decode the string into a byte[] array using Base64.
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); // Create a ByteArrayInputStream to read the bytes.
					
					try { // The following code may throw an exception which must be caught.
						ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream); // Create an ObjectInputStream with the ByteArrayInputStream.
						@SuppressWarnings("unchecked")
						ArrayList<String[]> failedResults = (ArrayList<String[]>) objectInputStream.readObject(); // Read the failed test results from the stream.
						
						gui.showFailedResults(failedResults); // Pass the failed test results to the GUI object so it can display it on the GUI.
					}
					
					// If an exception was caught, print the stack trace for debugging purposes:
					catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				// If the data type is an indication that a round has ended, call gui.endRound() so it can update the GUI:
				else if (dataType.equals("RED")) {
					gui.endRound();
				}
				
				// If the data type is the inputs from a custom test:
				else if (dataType.equals("TIP")) {
					String[] inputs = data.split(","); // Create an array of the inputs.
					
					communicationHandler.waitForData(); // Wait for more data from the server.
					
					// If the new data type is the output from a custom test:
					if (communicationHandler.getDataType().equals("TOP")) {
						gui.showCustomTestResults(inputs, communicationHandler.getData()); // Pass the inputs and output to the GUI object to display them.
					}
				}
				
				// If the data type is the efficiency points of the player's solution:
				else if (dataType.equals("EFP")) {
					int efficiencyPoints = Integer.parseInt(data); // Store the data from the server as an integer.
					
					communicationHandler.waitForData(); // Wait for more data from the server.
					
					// If the new data type is the number of passes:
					if (communicationHandler.getDataType().equals("NOP")) {
						int numberOfPasses = Integer.parseInt(communicationHandler.getData()); // Store the data from the server as an integer.
						gui.displayScoreInformation(efficiencyPoints, numberOfPasses); // Pass the received data to the GUI object to display them.
					}
				}
				
				// If the data type is an indication that this client has been kicked from the server:
				else if (dataType.equals("KCD")) {
					gui.showMessage("You have been kicked from the server.");
				}
				
				// If the data type is an indication that this client has been banned from the server:
				else if (dataType.equals("BND")) {
					gui.showMessage("You have been banned from the server.");
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
	
	
	// Method to start a new round:
	public boolean newRound() {
		
		// Wait until the communication handler has data. If it fails during this time:
		if (!communicationHandler.waitForData()) {
			System.out.println("ERROR: disconnected."); // Show an error message in the console.
			return false; // Return false as the method has failed.
		}
		
		// Create variables for the problem's title, description, start time and end time:
		String problemTitle, problemDescription;
		long roundStartTime, roundEndTime;
		int gameMode;
		
		// If the data type is a problem title, store it:
		if (communicationHandler.getDataType().equals("PLT")) {
			problemTitle = communicationHandler.getData();
		}
		
		// If the data type wasn't a problem title, show an error message in the console and return false as the method has failed:
		else {
			System.out.println("ERROR: server sent "+communicationHandler.getDataType()+", expected PLT.");
			return false;
		}
		
		// Wait until the communication handler has data. If it fails during this time:
		if (!communicationHandler.waitForData()) {
			System.out.println("ERROR: disconnected."); // Show an error message in the console.
			return false; // Return false as the method has failed.
		}
		
		// If the data type is a problem description, store it:
		if (communicationHandler.getDataType().equals("PLD")) {
			problemDescription = communicationHandler.getData();
		}
		
		// If the data type wasn't a problem description, show an error message in the console and return false as the method has failed:
		else {
			System.out.println("ERROR: server sent "+communicationHandler.getDataType()+", expected PLD.");
			return false;
		}
		
		// Wait until the communication handler has data. If it fails during this time:
		if (!communicationHandler.waitForData()) {
			System.out.println("ERROR: disconnected."); // Show an error message in the console.
			return false; // Return false as the method has failed.
		}
		
		// If the data type is a round start time, store it:
		if (communicationHandler.getDataType().equals("RST")) {
			roundStartTime = Long.parseLong(communicationHandler.getData());
		}
		
		// If the data type wasn't a round start time, show an error message in the console and return false as the method has failed:
		else {
			System.out.println("ERROR: server sent "+communicationHandler.getDataType()+", expected RST.");
			return false;
		}
		
		// Wait until the communication handler has data. If it fails during this time:
		if (!communicationHandler.waitForData()) {
			System.out.println("ERROR: disconnected."); // Show an error message in the console.
			return false; // Return false as the method has failed.
		}
		
		// If the data type is a round end time, store it:
		if (communicationHandler.getDataType().equals("RET")) {
			roundEndTime = Long.parseLong(communicationHandler.getData());
		}
		
		// If the data type wasn't a round end time, show an error message in the console and return false as the method has failed:
		else {
			System.out.println("ERROR: server sent "+communicationHandler.getDataType()+", expected RET.");
			return false;
		}
		
		// Wait until the communication handler has data. If it fails during this time:
		if (!communicationHandler.waitForData()) {
			System.out.println("ERROR: disconnected."); // Show an error message in the console.
			return false; // Return false as the method has failed.
		}
		
		// If the data type is a list of variable names:
		if (communicationHandler.getDataType().equals("VBN")) {
			String variableNamesString = communicationHandler.getData(); // Store the variable names.
			String[] variableNames = variableNamesString.split(","); // Split the variable names into a String[] array.
			gui.setCustomTestDialogLayout(variableNames); // Call customTestDialogLayout() and pass the variable names array to it.
		}
		
		// If the data type is a list of variable names, show an error message in the console and return false as the method has failed:
		else {
			System.out.println("ERROR: server sent "+communicationHandler.getDataType()+", expected VBN.");
			return false;
		}
		
		// Wait until the communication handler has data. If it fails during this time:
		if (!communicationHandler.waitForData()) {
			System.out.println("ERROR: disconnected."); // Show an error message in the console.
			return false; // Return false as the method has failed.
		}
		
		// If the data type is the game mode:
		if (communicationHandler.getDataType().equals("GMD")) {
			gameMode = Integer.parseInt(communicationHandler.getData()); // Store the game mode.
		}
		
		// If the data type is a not the game mode, show an error message in the console and return false as the method has failed:
		else {
			System.out.println("ERROR: server sent "+communicationHandler.getDataType()+", expected GMD.");
			return false;
		}
		
		// Pass the problem's details to the GUI object so it can display them on the GUI:
		gui.startRoundCountdown(problemTitle, problemDescription, roundStartTime, roundEndTime, gameMode);
		
		return true; // Return true as the method has succeeded.
	}
	
	
	// Method which is called when an action occurs in the GUI:
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand(); // Get and store the action command used to determine what the action was.
		
		// If the action was pressing the button to connect to a server:
		if (command.equals("connect")) {
			gui.setConnectionStatus("Connecting..."); // Show on the GUI that the program is attempting to connect to the server.
			
			Main main = this; // Create a variable which points to this (Main) object.
			
			// Create a new thread to connect to the server without blocking the GUI:
			Thread connectThread = new Thread() {
				public void run() {
					String[] connectInfo = gui.getConnectDialogInputs(); // Get the data that the user inputted.
					
					try { // The following block of code may throw an exception which must be caught.
						@SuppressWarnings("resource")
						Socket socket = new Socket(connectInfo[0], Integer.parseInt(connectInfo[1])); // Open a socket using the information entered in the GUI.
						DataInputStream inputStream = new DataInputStream(socket.getInputStream()); // Create a DataInputStream from the socket's input stream.
						DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream()); // Create a DataOutputStream from the socket's output strema.
						
						communicationHandler = new CommunicationHandler(inputStream, outputStream); // Create a CommunicationHandler object.
						communicationHandler.sendData("USN"+connectInfo[2]); // Send the client's desired username to the server.
						
						communicationHandler.waitForData(); // Wait until the server responds.
						
						String dataType = communicationHandler.getDataType(); // Get the "data type" part of the data that the server has sent.
						
						// If the data type is an indication of whether the username is valid or not:
						if (dataType.equals("USV")) {
							String data = communicationHandler.getData(); // Get the "data" part of the data.
							
							// If the username was invalid, show this on the GUI:
							if (data.equals("false")) {
								gui.setConnectionStatus("Invalid username.");
							}
							
							// If the username was valid, close the dialog to connect to the server and start the Main object's thread.
							else {
								gui.closeConnectDialog();
								new Thread(main).start();
							}
						}
						
						// If the data type is an indication that the client has been banned:
						else if (dataType.equals("BND")) {
							gui.showMessage("You are banned from this server.");
							gui.setConnectionStatus("Banned");
						}
						
						// If the data type is something else, print and error to the console and show an error message on the GUI:
						else {
							System.out.println("ERROR: server sent "+dataType+", expected USV.");
							gui.setConnectionStatus("Unknown server error");
						}
					}
					
					// If an exception related to I/O occurred, show an error message on the GUI:
					catch (IOException e) {
						gui.setConnectionStatus("Unable to connect");
					}
					
					// If an exception occurred due to the port not being an integer, show a message on the GUI:
					catch (NumberFormatException e) {
						gui.setConnectionStatus("Invalid port.");
					}
				}
			};
			
			connectThread.start(); // Start the thread to connect to the server.
		}
		
		// If the action was pressing the hint button:
		else if (command.equals("hint")) {
			communicationHandler.sendData("HRQ"); // Send a hint request to the server.
			gui.disableHintButton(); // Disable the hint button.
		}
		
		// If the action was pressing the submit button:
		else if (command.equals("submit")) {
			communicationHandler.sendData("SMS"+gui.getSolution()); // Send the submission to the server.
			gui.disableControls(); // Disable the submit button.
		}
		
		// If the action was submitting a custom test:
		else if (command.equals("test")) {
			String[] testInputsArray = gui.getCustomTestInputs(); // Get and store the custom test inputs.
			gui.closeConnectDialog();
			
			String testInputs = ""; // Create a variable to store the inputs as a single string.
			
			// Iterate through the test inputs array and add each input the the test inputs string, separated by commas:
			for (String testInput: testInputsArray) {
				testInputs = testInputs + testInput + ",";
			}
			
			communicationHandler.sendData("CTI"+testInputs); // Send the test inputs string to the server.
			communicationHandler.sendData("TSM"+gui.getSolution()); // Get and send the player's solution to the server.
		}
		
		// If the action was pressing the "save" button in the settings dialog:
		else if (command.equals("savesettings")) {
			int[] newSettings = gui.getNewSettings(); // Get the new settings.
			
			String filePath; // Create a string used to store the file path of the settings file.
			
			try { // The following code may throw an exception which must be caught.
				
				// If the program is being run from a .jar file, set the file path to the absolute path outside of the .jar file:
				if (getClass().getResource("Main.class").toString().contains("jar!")) {
					String externalDirectory = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").toURI().toString().replaceFirst("file:", ""), "UTF-8");
					filePath = externalDirectory+"/PythonGame/settings.txt";
				}
				
				// If the program is being run from inside Eclipse, Set the file path to the relative path within the Eclipse project:
				else {
					filePath = "settings.txt";
				}
				
				FileWriter fileWriter = new FileWriter(filePath, false); // Create a FileWriter used to write to the file.
				
				// Write the settings to the file:
				fileWriter.write("line highlighting:"+newSettings[0]);
				fileWriter.write("\nkeyword highlighting:"+newSettings[1]);
				fileWriter.write("\noccurrence highlighting:"+newSettings[2]);
				fileWriter.write("\nauto indentation:"+newSettings[3]);
				fileWriter.write("\nauto bracket closing:"+newSettings[4]);
				fileWriter.write("\nauto string closing:"+newSettings[5]);
				fileWriter.write("\ncolour mode:"+newSettings[6]);
				fileWriter.write("\nbutton colour:"+newSettings[7]);
				fileWriter.write("\nmain text colour:"+newSettings[8]);
				fileWriter.write("\ncode colour:"+newSettings[9]);
				fileWriter.write("\nkeyword colour:"+newSettings[10]);
				fileWriter.write("\nmain background colour:"+newSettings[11]);
				fileWriter.write("\nsecondary background colour:"+newSettings[12]);
				fileWriter.write("\ntext editor background colour:"+newSettings[13]);
				fileWriter.write("\nline highlighting colour:"+newSettings[14]);
				fileWriter.write("\noccurrence highlighting colour:"+newSettings[15]);
				
				fileWriter.close(); // Close the FileWriter to save resources.
				
			}
			
			// If an exception was caught, print the stack trace and display a message on the GUI:
			catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				gui.showMessage("Unable to save settings.");
			}
			
			gui.applySettings(newSettings); // Pass the new settings to the GUI object so that it can apply the settings.
		}
	}
	
	
	// Method to read the settings file:
	public int[] readSettings() {
		int[] settings = new int[16]; // Create an array to store the settings.
		
		String filePath; // Create a string used to store the file path of the settings file.
		
		try { // The following code may throw an exception which must be caught.
			
			// If the program is being run from a .jar file, set the file path to the absolute path outside of the .jar file:
			if (getClass().getResource("Main.class").toString().contains("jar!")) {
				String externalDirectory = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").toURI().toString().replaceFirst("file:", ""), "UTF-8");
				filePath = externalDirectory+"/PythonGame/settings.txt";
			}
			
			// If the program is being run from inside Eclipse, Set the file path to the relative path within the Eclipse project:
			else {
				filePath = "settings.txt";
			}
			
			// Create a BufferedReader to read the settings file;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			
			String line = bufferedReader.readLine(); // Read the first line of the file.
			
			// Repeat until there are no more lines:
			for (int i = 0; line != null; i++) {
				
				// Get the settings value from the line (the substring after the colon), convert it to an integer and store it in the settings array:
				settings[i] = Integer.parseInt(line.split(":")[1]);
				
				line = bufferedReader.readLine(); // Read the next line from the file.
			}
			
			bufferedReader.close(); // Close the BufferedReader to free resources.
		}
		
		// If an exception was caught, print the stack trace for debugging purposes and return null.
		catch (IOException | URISyntaxException | NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		
		return settings; // Return the settings array.
	}
	
	
	// Method to copy the help file outside of the .jar file:
	private void copyHelpFile() {
		
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
			
			// Attempt to create a folder in the same directory as the .jar file with the name "PythonGame" if it doesn't already exist:
			File file = new File(externalDirectory+"/PythonGame/");
			if (!file.exists()) {
				file.mkdirs();
			}
			
			InputStream inputStream = getClass().getResourceAsStream("/help.html"); // Create an input stream for the help file.
			
			// Create the directory of the help file using the external directory:
			String helpDirectory = externalDirectory+"PythonGame/help.html";
					
			Path path = Paths.get(helpDirectory); // Create a path object for the help directory.
			
			// Attempt to copy the file from within the .jar file to the external directory:
			if (inputStream != null) {
				try {
					Files.copy(inputStream, path);
				}
				
				// If the file already exists, return as the help file has already been copied:
				catch (FileAlreadyExistsException e) {
					return;
				}

				// If the file cannot be copied, print the stacktrace (for debugging purposes) but continue trying to read the problems:
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	// Method called when the program is first executed:
	public static void main(String[] args) {
		
		// Attempt to change the LookAndFeel of the program:
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new Main(); // Create a new object of this class.
	}
}

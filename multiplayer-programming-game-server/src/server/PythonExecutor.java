package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PythonExecutor {
	
	private String pythonLocation; // Stores the location of a python executable so that python files can be executed.
	private long timeTaken = 0; // Stores the last execution's execution time.
	private boolean succeeded; // Stores whether the last execution executed successfully or not.
	private int timeLimit; // Stores the time limit for an execution, after which it should forcefully stop.
	
	// Create a set to store the allowed characters for a submission:
	private Set<String> allowedCharacters = new HashSet<String>(Arrays.asList(
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-+*\\/%.()[],\"'&^:=<>!\n\r\t\\ ".split("")
		));
	
	// Constructor which is called when this object is created:
	public PythonExecutor(String pythonLocation, int timeLimit) {
		
		// Store the parameters as class variables:
		this.pythonLocation = pythonLocation;
		this.timeLimit = timeLimit;
	}
	
	
	// Method to create a file with a given name and write a given string to it:
	public boolean createFile(String fileName, String setupCode, String code) {
		
		code = failsafeCode(setupCode + sanitiseCode(code)); // Add the setup code and make the code sanitised and fail-safe.

		File file = new File(System.getProperty("java.io.tmpdir")+"PythonCode\\"+fileName); // Create a File object for the file.
		
		// If the file doesn't exist:
		if (!file.exists()) {
			
			// Attempt to create the file:
			try {
				file.getParentFile().mkdirs(); // Create any missing folder(s) that make up the file's path.
				file.createNewFile(); // Create the file.
			}
			
			// If the file can't be created:
			catch (IOException e) {
				e.printStackTrace(); // Print the stack trace for debugging purposes.
				return false; // Return false to indicate that the method failed.
			}
		}
		
		// Attempt to write to the file:
		try {
			FileWriter fileWriter = new FileWriter(file); // Create a FileWriter object using the File object.
			PrintWriter printWriter = new PrintWriter(fileWriter); // Create a PrintWriter object using the FileWriter object.
			printWriter.println(code); // Print the given string to the file.
			printWriter.close(); // Close the PrintWriter to free memory.
			fileWriter.close(); // Close the FileWriter to free memory.
			return true; // Return true to indicate that the method was successful.
		}
		
		// If the file could not be written to:
		catch (IOException e) {
			e.printStackTrace(); // Print the stack trace for debugging purposes.
			return false; // Return false to indicate that the method was unsuccessful.
		}
	}


	// Method to sanitise code to make sure that harmful code cannot be exectuted:
	private String sanitiseCode(String code) {

		// Loop through each character:
		for (String character: code.split("")) {
			
			// If the character is not allowed, replace the code with a print statement which prints the error:
			if (!allowedCharacters.contains(character)) {
				code = "print('Unsupported character detected: "+character+". See help for details.')\n";
				
				break;
			}
		}
		
		// If the code contains a blacklisted substring, replace the code with a print statement which prints the error:
		if (code.contains("import")) {
			code = "print('Unsupported code detected: import. See help for details.')\n";
		}
		if (code.contains("eval")) {
			code = "print('Unsupported code detected: eval. See help for details.')\n";
		}
		if (code.contains("exec")) {
			code = "print('Unsupported code detected: exec. See help for details.')\n";
		}
		
		return code;
	}


	// Method to make code fail-safe by wrapping it in a try-except block:
	private String failsafeCode(String code) {
		String[] lines = code.split("\n"); // Split the code into an array which stores each line.
		code = ""; // Reset the code string.
		
		// Iterate through each line, and add them back to the code string with a tab character added to the front of each of them:
		for (String line: lines) {
			code = code + "\t" + line + "\n";
		}
		
		// Put the code in a try/except block which prints any caught exception and then raises it:
		code = "try:\n" +
			code +
			"except Exception as e:\n" + 
			"\tprint(e)\n" + 
			"\traise";

		return code;
	}
	
	
	// Method to execute a given file with the given arguments:
	public String executeFile(String fileName, String[] arguments) {
		
		// Change the array of arguments to a string of arguments separated by a whitespace.
		String args = "";
		for (String argument: arguments) {
			args = args + argument + " ";
		}
		
		// Create a String[] of the command which will be executed by the command line:
		String[] commands = {"cmd", "/c", "cd \""+pythonLocation+"\" && python.exe "+System.getProperty("java.io.tmpdir")+"PythonCode\\"+fileName+" "+args};
		
		ProcessBuilder processBuilder = new ProcessBuilder(commands); // Create a ProcessBuilder object which takes in the commands.
		
		try { // The following block of code may throw an exception which must be caught.
			Process process = processBuilder.start(); // Create a Process object by starting the ProcessBuilder, therefore executing the python file.
			
			// Store the current time as the time before the process started (it is actually immediately after, but
			// creating the Process object above takes time which I do not want to affect the calculated execution time):
			long timeBefore = System.currentTimeMillis(); 
			
			timeTaken = timeBefore; // Set timeTaken equal to timeBefore.
			
			// Loop until the process is finished or until the execution time (timeTaken - timeBefore) is over the time limit:
			while (process.isAlive() && timeTaken - timeBefore <= timeLimit) {
				Thread.sleep(1); // Sleep the thread for 1ms, as constantly checking the current time is unnecessarily resource intensive.
				
				// Set timeTaken equal to the current time (do not take timeBefore away as that would be inefficient):
				timeTaken = System.currentTimeMillis();
			}
			
			timeTaken = timeTaken - timeBefore; // Take timeBefore away from timeTaken to get the actual execution time.
			
			// If the time taken was over the time limit:
			if (timeTaken > timeLimit) {
				process.destroy(); // Forcefully stop the process (if it is still running).
				succeeded = false; // Set succeeded to false as the execution has failed.
				return "Error: Execution time exceeded "+timeLimit+"ms."; // Return an error message.
			}
			
			Scanner scanner = new Scanner(process.getInputStream()); // Create a scanner to read the process's InputStream (the output of the execution).
			
			// Store the output as a string called result:
			String result = "";
			while (scanner.hasNextLine()) {
				result = result + scanner.nextLine() + "\n";
			}
			
			// If the output is more than a character long, remove the last character to remove the extra new line character:
			if (result.length() > 0) {
				result = result.substring(0, result.length()-1);
			}
			
			scanner.close(); // Close the scanner to free memory.
			
			// If the process failed to execute:
			if (process.waitFor() != 0) {
				process.destroy(); // Destroy the process to free memory.
				succeeded = false; // Set succeeded to false as the execution has failed.
				
				// If there was no output for the program, an unknown error has occurred:
				if (result.equals("")) {
					result = "Unknown error.";
				}
				
				return "Error: "+result; // Return the error message.
			}
			
			process.destroy(); // Destroy the process to free memory.
			succeeded = true; // Set succeeded to true as the execution has succeeded.
			return result; // Return the output of the execution.
		}
		
		// If an exception occurred while handling the process:
		catch (IOException | InterruptedException e) {
			succeeded = false; // Set succeeded to false as the execution has failed.
			return "Error: Unknown error."; // Return an error message.
		}
	}
	
	
	// Method to get the execution time of the last execution:
	public long getTimeTaken() {
		return timeTaken;
	}
	
	
	// Method to get whether or not the last execution succeeded:
	public boolean succeeded() {
		return succeeded;
	}
}

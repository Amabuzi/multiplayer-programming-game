package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ProblemLoader {
    
    public static Problem loadProblem(String problemID) {
		try {
            
            // Load the problem file:
            String fileName = "problem#" + problemID + ".txt";
            ResourceLoader resourceLoader = new ResourceLoader();
            resourceLoader.loadFile(fileName);

            // If the resource loader was unable to load the file, return null as the loading has failed.
            if (!resourceLoader.isLoaded(fileName)) return null;
        			
			// Pass the problem's file path to a new FileReader, and pass this to a new BufferedReader, in order to read the file:
			BufferedReader bufferedReader = new BufferedReader(new FileReader(resourceLoader.getFile(fileName)));
			
			// Read the title, description, time limit, hint and generation instructions from the first five lines of the file, and store them:
			String title = bufferedReader.readLine();
			String description = bufferedReader.readLine();
			int timeLimit = Integer.parseInt(bufferedReader.readLine());
			String hint = bufferedReader.readLine();
			String generationInstructions = bufferedReader.readLine();
			
            TestData testData = generateTestData(generationInstructions); // Pass the generation instructions to generateTestData().
			
			String correctSolution = ""; // Initialise correctSolution as an empty string.
			String line = bufferedReader.readLine(); // Read and store the next line of the file.
			while (line != null) { // While the last read line isn't null.
				correctSolution += line + "\n"; // Add the last read line to correctSolution.
				line = bufferedReader.readLine(); // Read and store the next line of the file.
			}
			
			bufferedReader.close(); // Close the BufferedReader to free up memory.
			
			String setupCode = ""; // Initialise setupCode as an empty string.
			String[] splitSolution = correctSolution.split("\n"); // Split correctSolution into an array with each line being a separate element.
			
			// Loop through the lines of the correct solution, ending after the sys module is deleted:
			for (int i = 0; i < 3 + testData.getVariableNames().size(); i++) {
				setupCode += splitSolution[i] + "\n"; // Add each line to setupCode.
			}
			
			String code = ""; // Initialse an empty string to store the correct solution without the setup code.
			
			// Loop through the lines of the correct solution, starting from the line after the end of the setup code:
			for (int i = 3 + testData.getVariableNames().size(); i < splitSolution.length; i++) {
				code += splitSolution[i] + "\n"; // Add each line to the new code string.
			}
			correctSolution = code; // Store the new code string as the correct solution.
            
            return new Problem(title, description, timeLimit, hint, correctSolution, setupCode, generationInstructions, testData);
		}
		
		// If the problem file could not be read:
		catch (IOException e) {
			e.printStackTrace(); // Print the stack trace (for debugging purposes).
			return null; // Return null to indicate that the problem creation failed.
		}
	}
	
	
	// Method to generate the test data for the problem when given the generation instructions:
	private static TestData generateTestData(String generationInstructions) {

        ArrayList<String> testDataVariableNames = new ArrayList<String>();
	    ArrayList<String[]> testDataValues = new ArrayList<String[]>();

		Random random = new Random(); // Create an instance of a Random object, which is used to generate random numbers.
		
		// Remove all spaces from the generation instruction, and split it into parts separated by a "|" character, then store the parts in an array:
		String[] parts = generationInstructions.replaceAll(" ", "").split("[|]");
		
		int n = 0; // Create a counter variable to count how many parts have been handled. Initialise it to 0.
		
		// Create a variable which stores the number of input variables for the problem, indicated by the value of the first part:
		int numberOfVariables = Integer.parseInt(parts[0]);
		
		// Loop through the next numberOfVariables parts:
		for (n = 1; n <= numberOfVariables; n++) {
			testDataVariableNames.add(parts[n]); // Store the name of the variable.
			testDataValues.add(new String[20]); // Create and store a String[] array which will store 20 input for tests.
		}
		
		int[] nextIndex = new int[numberOfVariables]; // Create an array which stores the next index of each testDataValues array.
		
		// Set the initial value of each next index to 0 by loop through the array:
		for (int i = 0; i < nextIndex.length; i++) {
			nextIndex[i] = 0;
		}
		
		while (n < parts.length) { // Loop through all of the parts.
			int numberOfTests = Integer.parseInt(parts[n]); // Get and store the next part as the number of tests that should be in the range.
			
			for (int i = 0; i < numberOfVariables; i++) { // Loop as many times as the number of variables.
				n++; // Increment n to get the index of the next part.
				int lowerLimit = Integer.parseInt(parts[n]); // Get and store the next part as the lower limit of the range.
				n++; // Increment n to get the index of the next part.
				int upperLimit = Integer.parseInt(parts[n]); // Get and store the next part as the lower limit of the range.
				
				String[] singularTestData = testDataValues.get(i); // Get and store the String[] array of the ith variable from testDataValues.
				for (int j = 0; j < numberOfTests; j++) { // Loop as many times as the number of tests which will be generated.
					int randomValue = random.nextInt(upperLimit - lowerLimit + 1) + lowerLimit; // Generate and store a random number within the limit.
					
					// Store the random number in the singular test data, in the index stored in the nextIndex array.
					singularTestData[nextIndex[i]] = Integer.toString(randomValue);
					
					nextIndex[i]++; // Increment the next index for this input variable's test data.
				}
				
				testDataValues.set(i, singularTestData); // Store the generated test data in the class variable testDataValues.
			}
			
			n++; // Increment n to get the index of next the parts.
        }

        return new TestData(testDataVariableNames, testDataValues);
	}

}

package server;

import java.util.ArrayList;

// Runs both correct and user submitted solutions
// Generates test data and results and times for correct solution
// Marks user solutions

public class SolutionRunner {

    Problem problem;
    PythonExecutor pythonExecutor;

    private int averageTimeTaken;
	private String[] expectedOutcomes = new String[20];
	
	private ArrayList<String[]> failedResults = new ArrayList<String[]>(); // Stores the failed results of the last test.
    
    public SolutionRunner(Problem problem, PythonExecutor pythonExecutor) {
        this.problem = problem;
        this.pythonExecutor = pythonExecutor;

        createExpectedOutcomes();
    }

    	// Method to create the expected outcomes for each test:
	public boolean createExpectedOutcomes() {
		pythonExecutor.createFile("solution.py", problem.getSetupCode(), problem.getSolution()); // Using PythonExecutor's createFile() create a python file for the correct solution.

		ArrayList<String> testDataVariableNames = problem.getTestData().getVariableNames();
		ArrayList<String[]> testDataValues = problem.getTestData().getValues();
		
		String[] inputs = new String[testDataVariableNames.size()]; // Create a String[] array for the set of inputs for a single test.
		int totalTimeTaken = 0; // Create an integer variable to store the total execution time for all of the tests.
		
		for (int i = 0; i <= 19; i++) { // Loop 20 times (as there are 20 tests).
			for (int j = 0; j < testDataVariableNames.size(); j++) { // Loop as many times as the number of input variables.
				inputs[j] = testDataValues.get(j)[i]; // Get the ith set of inputs for the jth variable from testDataValues.
			}
			
			String result = pythonExecutor.executeFile("solution.py", inputs); // Execute the correct solution with the inputs and store the result.
			
			// If the execution wasn't successful, return false to indicate that the expected outputs creation has failed:
			if (!pythonExecutor.succeeded()) {
				System.out.println(result);
				return false;
			}
			
			totalTimeTaken += pythonExecutor.getTimeTaken(); // Add the execution time to the total execution time.
			expectedOutcomes[i] = result; // Store the result in the class variable expectedOutcomes.
		}
		
		averageTimeTaken = totalTimeTaken/20; // Calculate the average execution time by dividing by 20, and store this in a class variable.
		return true; // Return true to indicate that the expected outputs were successfully created.
	}
	
	
	// Method to score a given solution:
	public int[] scoreSolution(String solution, int gameMode, long roundStartTime, long roundEndTime, long submissionTime) {
		pythonExecutor.createFile("solution.py", problem.getSetupCode(), solution); // Using PythonExecutor's createFile() create a python file for the solution.

		ArrayList<String> testDataVariableNames = problem.getTestData().getVariableNames();
		ArrayList<String[]> testDataValues = problem.getTestData().getValues();
		
		String[] inputs = new String[testDataVariableNames.size()]; // Create a String[] array for the set of inputs for a single test.
		
		int totalTimeTaken = 0; // Create an integer variable to store the total execution time for all of the tests.
		int fails = 0; // Create an integer variable to store the amount of failed tests.
		
		for (int i = 0; i <= 19; i++) { // Loop 20 times (as there are 20 tests).
			for (int j = 0; j < testDataVariableNames.size(); j++) { // Loop as many times as the number of input variables.
				inputs[j] = testDataValues.get(j)[i]; // Get the ith set of inputs for the jth variable from testDataValues.
			}
			
			String result = pythonExecutor.executeFile("solution.py", inputs); // Execute the solution with the inputs and store the result.
			
			// If the execution failed, or if the output doesn't match the expected output:
			if (!pythonExecutor.succeeded() || !result.equals(expectedOutcomes[i])) {
				fails++; // Increment the fails counter.
				
				String[] failedResult = new String[inputs.length + 2]; // Create an array, failedResult, to store the details of the failed test.
				failedResult[0] = result; // Store the output of the test in the first index.
				failedResult[1] = expectedOutcomes[i]; // Store the expected output in the second index.
				
				// Loop through the set of inputs, adding each input to the failedResult array:
				for (int k = 0; k < inputs.length; k++) {
					failedResult[k + 2] = inputs[k];
				}
				
				failedResults.add(failedResult); // Add the failedResult array to the ArrayList class variable.
				
				// Increase the total time taken variable by the correct solution's average time (as the actual time taken of a failed test is unreliable):
				totalTimeTaken += averageTimeTaken;
			}
			
			// If the output matched the expected output, add the execution time to the total execution time variable:
			else {
				totalTimeTaken += pythonExecutor.getTimeTaken();
			}
		}
		
		int score = 0; // Create a variable to store the score of the solution.
		
		// If the game mode is efficiency:
		if (gameMode == 0) {
			int efficiencyPoints = 1600 * averageTimeTaken / totalTimeTaken; // Calculate the efficiency points of the solution.
			int passes = 20 - fails; // Calculate the number of passes.
			
			// Calculate the score of the solution, so that failing or inefficiency reduces the score, but efficiency increases the score:
			score = efficiencyPoints * passes / 20;
			
			return new int[] {score, efficiencyPoints, passes}; // Return the score, efficiency points and the number of passes.
		}
		
		// If the game mode is length:
		else if (gameMode == 1) {
			
			// Calculate the length points of the solution.
			int lengthPoints;
			try {
				lengthPoints = 80 * problem.getSolution().length() / solution.length(); 
			} catch (ArithmeticException e) {
				lengthPoints = 0;
			}
			
			int passes = 20 - fails; // Calculate the number of passes.
			
			// Calculate the score of the solution, so that failing or longer code reduces the score, but shorter code increases the score:
			score = lengthPoints * passes / 20;
			
			return new int[] {score, lengthPoints, passes}; // Return the score, length points and the number of passes.
		}
		
		// If the game mode is time:
		else if (gameMode == 2) {
			int timePoints = (int) (80 * (roundEndTime - submissionTime) / (roundEndTime - roundStartTime)); // Calculate the time points of the solution.
			int passes = 20 - fails; // Calculate the number of passes.
			
			// Calculate the score of the solution, so that failing or a longer submission time reduces the score, but a shorter submission time increases the score:
			score = timePoints * passes / 20;
			
			return new int[] {score, timePoints, passes}; // Return the score, time points and the number of passes.
		}
		
		// If an unknown game mode was entered:
		else {
			System.out.println("ERROR: Unknown mode.");
			return null;
		}
	}

    public ArrayList<String[]> getFailedResults() {
        ArrayList<String[]> temp = new ArrayList<String[]>(failedResults); // Copy the failedResults ArrayList to a temporary variable.
		failedResults.removeAll(failedResults); // Remove all elements from the failedResults ArrayList.
		return temp; // Return the temporary variable.
    }

}

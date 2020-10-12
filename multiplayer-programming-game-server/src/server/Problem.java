package server;

public class Problem {
	
	// Create variables used to store information about the problem:
	private String title;
	private String description;
	private int timeLimit;
	private String hint;
	private String correctSolution;
	private String setupCode;
	private String generationInstructions;
	
	// Create variable used to store information for testing and scoring:
	private TestData testData;
	
	// Constructor which is called when this object is created:
	public Problem(String title, String description, int timeLimit, String hint, String correctSolution, String setupCode, String generationInstructions, TestData testData) {
		this.title = title;
		this.description = description;
		this.timeLimit = timeLimit;
		this.hint = hint;
		this.correctSolution = correctSolution;
		this.setupCode = setupCode;
		this.generationInstructions = generationInstructions;
		this.testData = testData;
	}
	
	
	// Method to get the loaded problem's title:
	public String getTitle() {
		return title;
	}
	
	
	// Method to get the loaded problem's description:
	public String getDescription() {
		return description;
	}
	
	
	// Method to get the loaded problem's default time limit:
	public int getTimeLimit() {
		return timeLimit;
	}
	
	
	// Method to get the loaded problem's hint:
	public String getHint() {
		return hint;
	}
	
	
	// Method to convert the test data variable names to a single string and return them:
	public String getVariableNamesAsString() {
		String variableNames = ""; // Create a variable to store the variable names as a string.
		
		// Iterate through the ArrayList of variable names and add them, separated by whitespaces, to the string.
		for (String variableName: testData.getVariableNames()) {
			variableNames += variableName + ",";
		}
		
		return variableNames; // Return the variable names.
	}
	
	
	// Method to return the setup code for the solution to run:
	public String getSetupCode() {
		return setupCode;
	}
	
	
	// Method to return the solution:
	public String getSolution() {
		return correctSolution;
	}
	
	
	// Method to return the generation instructions:
	public String getGenerationInstructions() {
		return generationInstructions;
	}


	public TestData getTestData() {
		return testData;
	}
}

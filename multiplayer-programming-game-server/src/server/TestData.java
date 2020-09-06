package server;

import java.util.ArrayList;

public class TestData {
    
    private ArrayList<String> variableNames = new ArrayList<String>();
    private ArrayList<String[]> values = new ArrayList<String[]>();
    
    public TestData(ArrayList<String> variableNames, ArrayList<String[]> values) {
        this.variableNames = variableNames;
        this.values = values;
    }

    public ArrayList<String> getVariableNames() {
        return variableNames;
    }

    public ArrayList<String[]> getValues() {
        return values;
    }

}

package client;

//TODO use json or another cleaner way to store settings
//TODO move some file handling code to a new class so that it can also used by the help file

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsHandler {

    private int[] settings = new int[16];

    private File settingsFile;

    public SettingsHandler(File settingsFile) {

        try {
            loadSettings(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadSettings(File settingsFile) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(settingsFile));
			
        String line = bufferedReader.readLine();
        
        for (int i = 0; line != null; i++) {
            
            // Get the settings value from the line (the substring after the colon), convert it to an integer and store it in the settings array:
            settings[i] = Integer.parseInt(line.split(":")[1]);
            
            line = bufferedReader.readLine();
        }
        
        bufferedReader.close();
    }

    public boolean writeSettings(int[] newSettings) {

        try {
            
            FileWriter fileWriter = new FileWriter(settingsFile, false); // Create a FileWriter used to write to the file.
            
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
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int[] getSettings() {
        return settings;
    }

}

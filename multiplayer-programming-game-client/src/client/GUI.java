package client; //TODO modularise

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {
	
	// Create variables for the GUI elements that are used in multiple methods:
	private JDialog connectDialog;
	private JTextField ipAddressTextField;
	private JTextField portTextField;
	private JTextField usernameTextField;
	private JLabel connectionStatusLabel;
	private JButton connectButton;
	
	private JLabel serverNameLabel;
	private JLabel statusLabel;
	private JLabel titleLabel;
	private JLabel timerLabel;
	
	private JButton hintButton;
	private JButton submitButton;
	private JButton customTestButton;
	private JButton helpButton;
	private JButton settingsButton;
	
	private JTextPane solutionTextPane;
	private JTextArea descriptionTextArea;
	private JTextArea outputTextArea;
	
	private JDialog customTestDialog;
	private JLabel variable1Label;
	private JTextField variable1TextField;
	private JLabel variable2Label;
	private JTextField variable2TextField;
	private JLabel variable3Label;
	private JTextField variable3TextField;
	private JLabel variable4Label;
	private JTextField variable4TextField;
	private JLabel variable5Label;
	private JTextField variable5TextField;
	private JLabel variable6Label;
	private JTextField variable6TextField;
	private JButton submitTestButton;
	
	private JDialog settingsDialog;
	private JCheckBox autoIndentationCheckBox;
	private JCheckBox autoBracketClosingCheckBox;
	private JCheckBox keywordHighlightingCheckBox;
	private JCheckBox lineHighlightingCheckBox;
	private JCheckBox occurenceHighlightingCheckBox;
	private JCheckBox autoStringClosingCheckBox;
	private JRadioButton defaultColourRadioButton;
	private JRadioButton darkColourRadioButton;
	private JRadioButton customColourRadioButton;
	private JButton saveSettingsButton;
	
	public JDialog colourPickerDialog;
	public JColorChooser colourChooser;
	
	
	// Create a DefaultTableModels for the table, so that it can be read and changed:
	private DefaultTableModel tableModel;
	
	// Create Swing Timers to allow counting down until a round starts/ends without blocking the thread:
	private Timer startRoundTimer;
	private Timer endRoundTimer;
	
	private String gameModeString; // Create a string used to store the name of the current game mode.
	
	// Create arrays to group up GUI elements:
	private JButton[] buttons;
	private JComponent[] mainTextComponents;
	private JComponent[] mainBackgroundComponents;
	private JComponent[] secondaryBackgroundComponents;
	
	// Create Color objects for the solution text pane's background and line hightlighting colours:
	private Color textEditorBackgroundColour = Color.white;
	private Color lineHighlightingColour = Color.lightGray;
	
	// Create an IDEDocumentFilter:
	private IDEDocumentFilter ideDocumentFilter;
	
	private int[] newSettings; // Create an array to store the new settings.
	private int lastOpenedColourSetting; // Create an integer to store the index in the settings array of the last opened colour setting.
	
	// Constructor which is called when this object is created:
	public GUI() {

		// Create the GUI elements:
		connectDialog = new JDialog((Dialog) null);
		JLabel connectHeadingLabel = new JLabel();
		JLabel ipAddressLabel = new JLabel();
		ipAddressTextField = new JTextField();
		JLabel portLabel = new JLabel();
		portTextField = new JTextField();
		JLabel usernameLabel = new JLabel();
		usernameTextField = new JTextField();
		connectButton = new JButton();
		JPanel connectionStatusPanel = new JPanel();
		connectionStatusLabel = new JLabel();
		
		JPanel problemPanel = new JPanel();
		titleLabel = new JLabel();
		JScrollPane descriptionScrollPane = new JScrollPane();
		descriptionTextArea = new JTextArea();
		JLabel timerHeadingLabel = new JLabel();
		timerLabel = new JLabel();
		serverNameLabel = new JLabel();
		
		JPanel playersPanel = new JPanel();
		JScrollPane playersScrollPane = new JScrollPane();
		JTable playersTable = new JTable();
		JLabel playersHeadingLabel = new JLabel();
		
		JPanel solutionPanel = new JPanel();
		JLabel solutionHeadingLabel = new JLabel();
		JScrollPane solutionScrollPane = new JScrollPane();
		
		solutionTextPane = new JTextPane() {
			
			// Method to paint the text pane:
			@Override
			protected void paintComponent(Graphics g) {
				setOpaque(false); // Make the text pane non-opaque.
				
				// Fill the text pane with a white colour:
				g.setColor(textEditorBackgroundColour);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				try { // The following code may throw an exception which must be caught.
					
					// Get the rectangle of the current line:
					Rectangle2D rectangle = modelToView2D(getCaretPosition());
					
					// Fill the rectangle with a grey colour:
					g.setColor(lineHighlightingColour);
					g.fillRect(0, (int) rectangle.getY(), getWidth(), (int) rectangle.getHeight());
				}
				
				// If a bad location exception is caught, print the stack trace:
				catch (BadLocationException e) {
					e.printStackTrace();
				}
				
				super.paintComponent(g); // Paint the text pane.
			}
			
			
			// Method to repaint the text pane:
			@Override
			public void repaint(long tm, int x, int y, int width, int height) {
				super.repaint(tm, 0, 0, getWidth(), getHeight()); // Repaint the whole text pane.
			}
		};
		
		JScrollPane outputScrollPane = new JScrollPane();
		outputTextArea = new JTextArea();
		JLabel outputHeadingLabel = new JLabel();
		
		customTestDialog = new JDialog((Dialog) null);
        JLabel customTestHeadingLabel = new JLabel();
        variable1Label = new JLabel();
        variable1TextField = new JTextField();
        variable2Label = new JLabel();
        variable2TextField = new JTextField();
        variable3Label = new JLabel();
        variable3TextField = new JTextField();
        variable4Label = new JLabel();
        variable4TextField = new JTextField();
        variable5Label = new JLabel();
        variable5TextField = new JTextField();
        variable6Label = new JLabel();
        variable6TextField = new JTextField();
        submitTestButton = new JButton();
        
        settingsDialog = new JDialog((Dialog) null);
        autoIndentationCheckBox = new JCheckBox();
        autoBracketClosingCheckBox = new JCheckBox();
        keywordHighlightingCheckBox = new JCheckBox();
        lineHighlightingCheckBox = new JCheckBox();
        occurenceHighlightingCheckBox = new JCheckBox();
        autoStringClosingCheckBox = new JCheckBox();
        defaultColourRadioButton = new JRadioButton();
        darkColourRadioButton = new JRadioButton();
        customColourRadioButton = new JRadioButton();
        JLabel colourSettingsLabel = new JLabel();
        JLabel textEditorSettingsLabel = new JLabel();
        JLabel customColourSettingsLabel = new JLabel();
        JButton buttonsColourButton = new JButton();
        JButton mainTextColourButton = new JButton();
        JButton codeTextColourButton = new JButton();
        JButton mainBackgroundColourButton = new JButton();
        JButton secondaryBackgroundColourButton = new JButton();
        JButton keywordsColourButton = new JButton();
        JButton textEditorBackgroundColourButton = new JButton();
        JButton lineHighlightingColourButton = new JButton();
        JButton occurrenceHighlightingColourButton = new JButton();
        saveSettingsButton = new JButton();
        JButton cancelSettingsButton = new JButton();
        
        colourPickerDialog = new JDialog((Dialog) null);
        colourChooser = new JColorChooser();
        JButton saveColourButton = new JButton();
        JButton cancelColourButton = new JButton();
		
		submitButton = new JButton();
		helpButton = new JButton();
		hintButton = new JButton();
		customTestButton = new JButton();
		settingsButton = new JButton();
		
		statusLabel = new JLabel();
		

		// NetBeans generated code starts:
		connectHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
		connectHeadingLabel.setText("Connect to server");

		ipAddressLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		ipAddressLabel.setText("IP Address:");

		ipAddressTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

		portLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		portLabel.setText("Port:");

		portTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

		usernameLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		usernameLabel.setText("Username:");

		usernameTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

		connectButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		connectButton.setText("Connect");

		connectionStatusLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		connectionStatusPanel.add(connectionStatusLabel);

		javax.swing.GroupLayout connectDialogLayout = new javax.swing.GroupLayout(connectDialog.getContentPane());
		connectDialog.getContentPane().setLayout(connectDialogLayout);
		connectDialogLayout.setHorizontalGroup(connectDialogLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(connectionStatusPanel, javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(connectDialogLayout.createSequentialGroup().addGap(82, 82, 82).addComponent(connectButton)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connectDialogLayout.createSequentialGroup()
						.addContainerGap(27, Short.MAX_VALUE)
						.addGroup(connectDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(connectHeadingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 193,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(connectDialogLayout.createSequentialGroup().addComponent(portLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(62, 62, 62))
								.addGroup(connectDialogLayout.createSequentialGroup().addComponent(ipAddressLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(ipAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(connectDialogLayout.createSequentialGroup().addComponent(usernameLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(30, 30, 30)));
		connectDialogLayout.setVerticalGroup(connectDialogLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(connectDialogLayout.createSequentialGroup().addContainerGap()
						.addComponent(connectHeadingLabel).addGap(18, 18, 18)
						.addGroup(connectDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(ipAddressLabel)
								.addComponent(ipAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(connectDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(portLabel).addComponent(portTextField,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(connectDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(usernameLabel).addComponent(usernameTextField,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(connectButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
								connectionStatusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)));
		
		customTestHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        customTestHeadingLabel.setText("Custom Test");

        variable1Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        variable1Label.setText("a:");

        variable1TextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        variable2Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        variable2Label.setText("b:");

        variable2TextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        variable3Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        variable3Label.setText("c:");

        variable3TextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        variable4Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        variable4Label.setText("d:");

        variable4TextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        variable5Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        variable5Label.setText("e:");

        variable5TextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        variable6Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        variable6Label.setText("f:");

        variable6TextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        submitTestButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        submitTestButton.setText("Test");

        javax.swing.GroupLayout customTestDialogLayout = new javax.swing.GroupLayout(customTestDialog.getContentPane());
        customTestDialog.getContentPane().setLayout(customTestDialogLayout);
        customTestDialogLayout.setHorizontalGroup(
            customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customTestDialogLayout.createSequentialGroup()
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(customTestDialogLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(customTestDialogLayout.createSequentialGroup()
                                .addComponent(variable6Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variable6TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(customTestDialogLayout.createSequentialGroup()
                                .addComponent(variable5Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variable5TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(customTestDialogLayout.createSequentialGroup()
                                .addComponent(variable4Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variable4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(customTestDialogLayout.createSequentialGroup()
                                .addComponent(variable3Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variable3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(customTestDialogLayout.createSequentialGroup()
                                .addComponent(variable2Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variable2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(customTestDialogLayout.createSequentialGroup()
                                .addComponent(variable1Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variable1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(submitTestButton)))
                    .addGroup(customTestDialogLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(customTestHeadingLabel)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        customTestDialogLayout.setVerticalGroup(
            customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customTestDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(customTestHeadingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variable1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(variable1Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variable2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(variable2Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variable3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(variable3Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variable4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(variable4Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variable5TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(variable5Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customTestDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variable6TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(variable6Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitTestButton)
                .addContainerGap())
        );
        
        settingsDialog.setTitle("Settings");

        textEditorSettingsLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        textEditorSettingsLabel.setText("Text editor settings:");

        autoIndentationCheckBox.setText("Auto indentation");

        autoBracketClosingCheckBox.setText("Auto bracket closing");

        keywordHighlightingCheckBox.setText("Keyword highlighting");

        lineHighlightingCheckBox.setText("Line highlighting");

        occurenceHighlightingCheckBox.setText("Occurrence highlighting");

        autoStringClosingCheckBox.setText("Auto string closing");

        colourSettingsLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        colourSettingsLabel.setText("Colour settings:");

        defaultColourRadioButton.setText("Default");

        darkColourRadioButton.setText("Dark");

        customColourRadioButton.setText("Custom");
        
        buttonsColourButton.setText("Buttons");

        mainTextColourButton.setText("Main text");

        codeTextColourButton.setText("Code text");

        mainBackgroundColourButton.setText("Main background");

        secondaryBackgroundColourButton.setText("Secondary background");

        keywordsColourButton.setBackground(new java.awt.Color(204, 51, 255));
        keywordsColourButton.setText("Keywords");
        keywordsColourButton.setOpaque(false);

        textEditorBackgroundColourButton.setText("Text editor background");

        lineHighlightingColourButton.setText("Line highlighting");

        occurrenceHighlightingColourButton.setText("Occurrence highlighting");

        saveSettingsButton.setText("Save");

        cancelSettingsButton.setText("Cancel");

        customColourSettingsLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        customColourSettingsLabel.setText("Custom colour settings:");

        javax.swing.GroupLayout settingsDialogLayout = new javax.swing.GroupLayout(settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(settingsDialogLayout.createSequentialGroup()
                                .addComponent(colourSettingsLabel)
                                .addGap(18, 18, 18)
                                .addComponent(defaultColourRadioButton)
                                .addGap(6, 6, 6)
                                .addComponent(darkColourRadioButton))
                            .addComponent(customColourSettingsLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(settingsDialogLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                                        .addComponent(textEditorBackgroundColourButton, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(160, 160, 160))
                                    .addGroup(settingsDialogLayout.createSequentialGroup()
                                        .addComponent(mainBackgroundColourButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(secondaryBackgroundColourButton))
                                    .addGroup(settingsDialogLayout.createSequentialGroup()
                                        .addComponent(lineHighlightingColourButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(occurrenceHighlightingColourButton))
                                    .addGroup(settingsDialogLayout.createSequentialGroup()
                                        .addComponent(buttonsColourButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mainTextColourButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(codeTextColourButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(keywordsColourButton)
                                            .addComponent(customColourRadioButton)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                                        .addComponent(cancelSettingsButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(saveSettingsButton))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(autoBracketClosingCheckBox)
                                        .addComponent(autoIndentationCheckBox)
                                        .addComponent(autoStringClosingCheckBox))))
                            .addComponent(textEditorSettingsLabel)
                            .addGroup(settingsDialogLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lineHighlightingCheckBox)
                                    .addComponent(occurenceHighlightingCheckBox)
                                    .addComponent(keywordHighlightingCheckBox))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        settingsDialogLayout.setVerticalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textEditorSettingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addComponent(lineHighlightingCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(keywordHighlightingCheckBox)
                        .addGap(3, 3, 3)
                        .addComponent(occurenceHighlightingCheckBox))
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addComponent(autoIndentationCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(autoBracketClosingCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(autoStringClosingCheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colourSettingsLabel)
                    .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(defaultColourRadioButton)
                        .addComponent(darkColourRadioButton)
                        .addComponent(customColourRadioButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(customColourSettingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainTextColourButton)
                    .addComponent(codeTextColourButton)
                    .addComponent(buttonsColourButton)
                    .addComponent(keywordsColourButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secondaryBackgroundColourButton)
                    .addComponent(mainBackgroundColourButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textEditorBackgroundColourButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineHighlightingColourButton)
                    .addComponent(occurrenceHighlightingColourButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveSettingsButton)
                    .addComponent(cancelSettingsButton))
                .addContainerGap())
        );

        colourPickerDialog.setTitle("Choose your colour:");

        saveColourButton.setText("Save");

        cancelColourButton.setText("Cancel");

        javax.swing.GroupLayout colourPickerDialogLayout = new javax.swing.GroupLayout(colourPickerDialog.getContentPane());
        colourPickerDialog.getContentPane().setLayout(colourPickerDialogLayout);
        colourPickerDialogLayout.setHorizontalGroup(
            colourPickerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colourPickerDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(colourPickerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colourChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colourPickerDialogLayout.createSequentialGroup()
                        .addComponent(cancelColourButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveColourButton)))
                .addContainerGap())
        );
        colourPickerDialogLayout.setVerticalGroup(
            colourPickerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colourPickerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(colourChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colourPickerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveColourButton)
                    .addComponent(cancelColourButton))
                .addContainerGap(15, Short.MAX_VALUE))
        );
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		titleLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		titleLabel.setText("Your problem:");

		descriptionTextArea.setEditable(false);
		descriptionTextArea.setColumns(20);
		descriptionTextArea.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setRows(5);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setAutoscrolls(false);
		descriptionScrollPane.setViewportView(descriptionTextArea);

		timerHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		timerHeadingLabel.setText("Time remaining: ");

		timerLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		timerLabel.setText("00 : 00 : 00");

		serverNameLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		serverNameLabel.setToolTipText("");
		serverNameLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		javax.swing.GroupLayout problemPanelLayout = new javax.swing.GroupLayout(problemPanel);
		problemPanel.setLayout(problemPanelLayout);
		problemPanelLayout.setHorizontalGroup(problemPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(problemPanelLayout.createSequentialGroup().addContainerGap().addGroup(problemPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(descriptionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
						.addGroup(problemPanelLayout.createSequentialGroup().addGroup(problemPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(titleLabel)
								.addGroup(problemPanelLayout.createSequentialGroup().addComponent(timerHeadingLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(timerLabel))
								.addComponent(serverNameLabel)).addGap(0, 0, Short.MAX_VALUE)))
						.addContainerGap()));
		problemPanelLayout.setVerticalGroup(problemPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(problemPanelLayout.createSequentialGroup().addContainerGap()
						.addComponent(serverNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(titleLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(descriptionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(problemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(timerHeadingLabel).addComponent(timerLabel))
						.addContainerGap()));

		tableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "Username", "Status", "Score", "Total", "Average", "Played", "Won" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		playersTable.setModel(tableModel);

		playersScrollPane.setViewportView(playersTable);
		if (playersTable.getColumnModel().getColumnCount() > 0) {
			playersTable.getColumnModel().getColumn(0).setPreferredWidth(110);
			playersTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		}

		playersHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		playersHeadingLabel.setText("Connected players:");

		helpButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		helpButton.setText("Help");

		statusLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

		settingsButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		settingsButton.setText("Settings");

		javax.swing.GroupLayout playersPanelLayout = new javax.swing.GroupLayout(playersPanel);
		playersPanel.setLayout(playersPanelLayout);
		playersPanelLayout.setHorizontalGroup(playersPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(playersPanelLayout.createSequentialGroup().addContainerGap().addGroup(playersPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(playersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
						.addGroup(playersPanelLayout.createSequentialGroup().addComponent(playersHeadingLabel).addGap(0,
								0, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, playersPanelLayout.createSequentialGroup()
								.addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(settingsButton).addGap(18, 18, 18).addComponent(helpButton)))
						.addContainerGap()));
		playersPanelLayout.setVerticalGroup(playersPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, playersPanelLayout.createSequentialGroup()
						.addContainerGap().addComponent(playersHeadingLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(playersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 170,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
						.addGroup(playersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(helpButton)
								.addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(settingsButton))
						.addContainerGap()));
		
		solutionHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		solutionHeadingLabel.setText("Your solution:");
		
        customTestButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        customTestButton.setText("Create a custom test");
        customTestButton.addActionListener(this);
		
		submitButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		submitButton.setText("Submit");
		
		solutionTextPane.setFont(new Font(Font.MONOSPACED, 0, 15));

		solutionScrollPane.setViewportView(solutionTextPane);

		hintButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		hintButton.setText("Request a hint");

		outputTextArea.setColumns(20);
		outputTextArea.setRows(5);
		outputScrollPane.setViewportView(outputTextArea);

		outputHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		outputHeadingLabel.setText("Output:");

		javax.swing.GroupLayout solutionPanelLayout = new javax.swing.GroupLayout(solutionPanel);
        solutionPanel.setLayout(solutionPanelLayout);
        solutionPanelLayout.setHorizontalGroup(
            solutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(solutionPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(solutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(solutionScrollPane)
                    .addGroup(solutionPanelLayout.createSequentialGroup()
                        .addComponent(customTestButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(hintButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(submitButton))
                    .addComponent(outputScrollPane)
                    .addGroup(solutionPanelLayout.createSequentialGroup()
                        .addGroup(solutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(solutionHeadingLabel)
                            .addComponent(outputHeadingLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        solutionPanelLayout.setVerticalGroup(
            solutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(solutionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(solutionHeadingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(solutionScrollPane)
                .addGap(18, 18, 18)
                .addComponent(outputHeadingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(solutionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customTestButton)
                    .addComponent(submitButton)
                    .addComponent(hintButton))
                .addContainerGap())
        );

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(problemPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(playersPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(solutionPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(problemPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(playersPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE))
				.addComponent(solutionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));

		pack();

		// NetBeans generated code ends.
		
		// Add action listeners to buttons:
		settingsButton.addActionListener(this);
		cancelSettingsButton.addActionListener(this);
		saveColourButton.addActionListener(this);
		cancelColourButton.addActionListener(this);
		
		buttonsColourButton.addActionListener(this);
		mainTextColourButton.addActionListener(this);
		codeTextColourButton.addActionListener(this);
		mainBackgroundColourButton.addActionListener(this);
		secondaryBackgroundColourButton.addActionListener(this);
		keywordsColourButton.addActionListener(this);
		textEditorBackgroundColourButton.addActionListener(this);
		lineHighlightingColourButton.addActionListener(this);
		occurrenceHighlightingColourButton.addActionListener(this);

		// Add the action commands to the buttons:
		connectButton.setActionCommand("connect");
		submitButton.setActionCommand("submit");
		hintButton.setActionCommand("hint");
		helpButton.setActionCommand("help");
		customTestButton.setActionCommand("testdialog");
		submitTestButton.setActionCommand("test");
		settingsButton.setActionCommand("settings");
		saveSettingsButton.setActionCommand("savesettings");
		cancelSettingsButton.setActionCommand("cancelsettings");
		saveColourButton.setActionCommand("savecolour");
		cancelColourButton.setActionCommand("cancelcolour");
		
		buttonsColourButton.setActionCommand("colourButtons");
		mainTextColourButton.setActionCommand("colourMainText");
		codeTextColourButton.setActionCommand("colourCode");
		mainBackgroundColourButton.setActionCommand("colourMainBackground");
		secondaryBackgroundColourButton.setActionCommand("colourSecondaryBackground");
		keywordsColourButton.setActionCommand("colourKeywords");
		textEditorBackgroundColourButton.setActionCommand("colourTextEditorBackground");
		lineHighlightingColourButton.setActionCommand("colourLineHighlighting");
		occurrenceHighlightingColourButton.setActionCommand("colourOccurrenceHighlighting");

		// Disable the elements that should be disabled when a round isn't occurring:
		submitButton.setEnabled(false);
		hintButton.setEnabled(false);
		solutionTextPane.setEditable(false);
		outputTextArea.setEditable(false);
		descriptionTextArea.setEditable(false);
		customTestButton.setEnabled(false);

		connectDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Make the connect dialog dispose itself when the user closes it.
		connectDialog.setSize(new Dimension(270, 320)); // Set the connect dialog's size to 270 pixels wide and 320 pixels tall.
		connectDialog.setVisible(true); // Make the connect dialog automatically visible when the program starts.
		
		settingsDialog.setSize(370, 380); // Set the settings dialog's size to 370 pixels wide and 380 pixels tall.
		colourPickerDialog.setSize(650, 415); // Set the colour picker dialog's size to 650 pixels wide and 415 pixels tall.
		
		// Group the colour radio buttons:
		ButtonGroup colourButtonGroup = new ButtonGroup();
		colourButtonGroup.add(defaultColourRadioButton);
		colourButtonGroup.add(darkColourRadioButton);
		colourButtonGroup.add(customColourRadioButton);
		
		// Make the players table fill its height and make it opague:
		playersTable.setFillsViewportHeight(true);
		playersTable.getTableHeader().setOpaque(false);
		
		// Set the solution text area's document filter to the custom IDEDocumentFilter:
		ideDocumentFilter = new IDEDocumentFilter(solutionTextPane);
		((AbstractDocument) solutionTextPane.getDocument()).setDocumentFilter(ideDocumentFilter);
		
		// Create an array to group the buttons:
		buttons = new JButton[] {
			connectButton, hintButton, submitButton, customTestButton, settingsButton, submitTestButton, saveSettingsButton,
			buttonsColourButton, mainTextColourButton, codeTextColourButton, mainBackgroundColourButton, secondaryBackgroundColourButton,
			keywordsColourButton, textEditorBackgroundColourButton, lineHighlightingColourButton, occurrenceHighlightingColourButton,
			cancelSettingsButton, saveColourButton, cancelColourButton, helpButton
		};
		
		// Create an array to group the main text components:
		mainTextComponents = new JComponent[] {
			connectionStatusLabel, serverNameLabel, statusLabel, titleLabel, timerLabel, variable1Label, variable2Label,
			variable3Label, variable4Label, variable5Label, variable6Label, connectHeadingLabel, ipAddressLabel, portLabel,
			usernameLabel, timerHeadingLabel, playersHeadingLabel, solutionHeadingLabel, outputHeadingLabel, customTestHeadingLabel,
			colourSettingsLabel, textEditorSettingsLabel, customColourSettingsLabel, autoIndentationCheckBox, autoBracketClosingCheckBox,
			keywordHighlightingCheckBox, lineHighlightingCheckBox, occurenceHighlightingCheckBox, autoStringClosingCheckBox,
			defaultColourRadioButton, darkColourRadioButton, customColourRadioButton
		};
		
		// Create an array to group the main background components:
		mainBackgroundComponents = new JComponent[] {
				problemPanel, playersPanel, solutionPanel, connectionStatusPanel, (JComponent) this.getContentPane(),
				(JComponent) connectDialog.getContentPane(), (JComponent) customTestDialog.getContentPane(),
				(JComponent) settingsDialog.getContentPane(), (JComponent) colourPickerDialog.getContentPane()
		};
		
		// Create an array to group the secondary background components:
		secondaryBackgroundComponents = new JComponent[] {
			ipAddressTextField, portTextField, usernameTextField, variable1TextField, variable2TextField, variable3TextField,
			variable4TextField, variable5TextField, variable6TextField, descriptionTextArea, outputTextArea, playersTable,
			playersTable.getTableHeader()
		};
		
		
		ipAddressTextField.setText("127.0.0.1");
		portTextField.setText("80");
		usernameTextField.setText("p");
		
	}
	
	
	// Method to get the information entered by the user in the connect dialog:
	public String[] getConnectDialogInputs() {
		return new String[] {ipAddressTextField.getText(), portTextField.getText(), usernameTextField.getText()};
	}
	
	
	// Method to change the text in the connection status:
	public void setConnectionStatus(String text) {
		connectionStatusLabel.setText(text);
	}
	
	
	// Method to close the connect dialog:
	public void closeConnectDialog() {
		connectDialog.dispose(); // Dispose of the JDialog.
		setVisible(true); // Make the JFrame visible.
	}
	
	
	// Method to display the name of the server:
	public void setServerName(String serverName) {
		serverNameLabel.setText("Playing on \""+serverName+"\"");
	}
	
	
	// Method to add the Main object as an action listener of three of the buttons:
	public void setActionListener(Main main) {
		connectButton.addActionListener(main);
		submitButton.addActionListener(main);
		hintButton.addActionListener(main);
		submitTestButton.addActionListener(main);
		helpButton.addActionListener(main);
		saveSettingsButton.addActionListener(main);
	}
	
	
	// Method which is called when an action occurs in the GUI:
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand(); // Get and store the action command used to determine what the action was.
		
		// If the action was pressing the custom test button, enable and show the custom test dialog:
		if (command.equals("testdialog")) {
			customTestDialog.setEnabled(true);
			customTestDialog.setVisible(true);
		}
		
		// If the action was pressing the setting button, open the settings dialog:
		else if (command.equals("settings")) {
			settingsDialog.setVisible(true);
		}
		
		// If the action was pressing a colour option, pass the name of the option to openColourPickerDialog:
		else if (command.substring(0,6).equals("colour")) {
			openColourPickerDialog(command.substring(6));
		}
		
		// If the action was pressing the save colour button, change newSettings to the colour chosen and close the colour picker dialog:
		else if (command.equals("savecolour")) {
			newSettings[lastOpenedColourSetting] = colourChooser.getColor().getRGB();
			colourPickerDialog.setVisible(false);
		}
		
		// If the action was pressing the cancel colour button, close the colour picker dialog:
		else if (command.equals("cancelcolour")) {
			colourPickerDialog.setVisible(false);
		}
		
		// If the action was pressing the cancel settings button, close the settings dialog:
		else if (command.equals("cancelsettings")) {
			settingsDialog.setVisible(false);
		}
	}
	
	
	// Method to start a countdown for the start of the round:
	public void startRoundCountdown(String problemTitle, String problemDescription, long startTime, long endTime, int gameMode) {
		startRoundTimer = new Timer(250, null); // Create a timer which activates every 250 milliseconds.
		
		// Create an ActionListener and add it to the timer:
		startRoundTimer.addActionListener(new ActionListener() {
			
			// Method which is called when the timer activates:
			public void actionPerformed(ActionEvent event) {
				long timeLeft = startTime - System.currentTimeMillis(); // Calculate and store the time left until the round should start.
				
				// If there is no time left:
				if (timeLeft <= 0) {
					startRoundTimer.stop(); // Stop the timer.
					startRound(problemTitle, problemDescription, endTime, gameMode); // Call startRound() to update the GUI to an "in a round" state.
				}
				
				// If there is time left, calculate the minutes and seconds left and show this on the GUI using the status label:
				else {
					long minsLeft = timeLeft / 60000;
					long secsLeft = (timeLeft % 60000) / 1000;
					statusLabel.setText("<html>New round in <b>" + minsLeft + " : " + secsLeft + "</b></hmtl>");
				}
			}
		});
		
		startRoundTimer.start(); // Start the timer.
	}
	
	
	// Method to start a countdown for the end of the round:
	private void startRound(String problemTitle, String problemDescription, long endTime, int gameMode) {
		
		// Display the title and description:
		titleLabel.setText("Your problem: "+problemTitle);
		descriptionTextArea.setText(problemDescription);
		
		outputTextArea.setText(""); // Reset the output text area.
		
		// Create a string which hold the name of the game mode:
		gameModeString = "unknown";
		switch (gameMode) {
			case 0:
				gameModeString = "efficiency";
				break;
			case 1:
				gameModeString = "length";
				break;
			case 2:
				gameModeString = "time";
		}
		
		statusLabel.setText("<html>Playing <b>"+gameModeString+"</b> mode</html>"); // Update the status label to display the game mode.
		
		// Enable the submit and hint buttons, and the solution text area:
		submitButton.setEnabled(true);
		hintButton.setEnabled(true);
		solutionTextPane.setEditable(true);
		customTestButton.setEnabled(true);
		
		solutionTextPane.getCaret().setVisible(true); // Show the caret on the solution text area.
		solutionTextPane.setText(""); // Clear the solution text area's text.
		
		endRoundTimer = new Timer(250, null); // Create a timer which activates every 250 milliseconds.
		
		// Create an ActionListener and add it to the timer:
		endRoundTimer.addActionListener(new ActionListener() {
			
			//Method which is called when the timer activates:
			public void actionPerformed(ActionEvent event) {
				long timeLeft = endTime - System.currentTimeMillis(); // Calculate and store the time left until the round should end.
				
				// If there is no time left:
				if (timeLeft <= 0) {
					endRoundTimer.stop(); // Stop the timer.
					endRound(); // Call endRound() to update the GUI to the "not in a round" state.
				}
				
				// If there is time left, calculate the minutes and seconds left and show this on the GUI using the timer label:
				else {
					long minsLeft = timeLeft / 60000;
					long secsLeft = (timeLeft % 60000) / 1000;
					timerLabel.setText(minsLeft+" : "+secsLeft);
				}
			}
		});
		
		endRoundTimer.start(); // Start the timer.
	}
	
	
	// Method to change the GUI's elements when the round ends:
	public void endRound() {
		
		// Stop the timers if they are running (in case the round stops before they have finished):
		startRoundTimer.stop();
		endRoundTimer.stop();
		
		// Reset the JLabels:
		timerLabel.setText("00 : 00");
		statusLabel.setText("Not in game");
		
		// Disable the submit and hint buttons and the solution text area:
		submitButton.setEnabled(false);
		hintButton.setEnabled(false);
		solutionTextPane.setEditable(false);
		customTestButton.setEnabled(false);
	}
	
	
	// Method to update the player table when given the data of a single client (in the form of an Object[] array):
	public void updatePlayerTable(Object[] playerData) {
		for (int i = 0; i < tableModel.getRowCount(); i++) { // Loop through the table's rows.
			
			// If the first field of the row at index i is equal to the first element of the array (the usernames match):
			if (tableModel.getValueAt(i, 0).equals(playerData[0])) {
				
				// Loop through the fields of the row at index i, making the jth field equal to the jth element of the array:
				for (int j = 0; j < tableModel.getColumnCount(); j++) {
					tableModel.setValueAt(playerData[j], i, j);
				}
				
				return;// The table has been updated, so return.
			}
		}
		
		tableModel.addRow(playerData); // If the method hasn't returned yet, the player isn't in the table, so add a new row to it.
	}
	
	
	// Method to display a hint:
	public void showHint(String hint) {
		descriptionTextArea.setText(descriptionTextArea.getText() + "\n\nHint: " + hint);
	}
	
	
	// Method to display the failed test results:
	public void showFailedResults(ArrayList<String[]> failedResults) {
		System.out.println(failedResults.size());
		
		int fails = failedResults.size(); // Get and store the number of failed tests.
		
		String outputText = outputTextArea.getText(); // Get and store the text in the output text area.
		outputText += fails + " fails. \n\n"; // Add the number of fails to the output text.
		 
		for (int i = 0; i < fails; i++) { // Loop through as many times as the numbers of failed.
			String[] failedResult = failedResults.get(i); // Get the details of the ith failed test.
			
			String output = failedResult[0]; // Get and store the first element, which is the output of the test.
			String expectedOutput = failedResult[1]; // Get and store the second element, which is the expected output of the test.
			
			// Loop through the next elements and add them to a String, formatting them as elements separated by a comma:
			String inputs = "";
			for (int j = 2; j < failedResult.length; j++) {
				inputs += failedResult[j] + ", ";
			}
			inputs = inputs.substring(0, inputs.length() - 2);
			
			// Add the details of the failed test to the variable which stores the text that should be outputted:
			outputText += "Inputs: " + inputs + "\n";
			outputText += "Output: " + output + "\n";
			outputText += "Expected output: " + expectedOutput + "\n\n";
			
		}
		
		outputTextArea.setText(outputText); // Show the text which should be outputted in the output text area.
	}
	
	
	// Method to get the solution that has been entered in the solution text area:
	public String getSolution() {
		return solutionTextPane.getText();
	}
	
	
	// Method to disable the hint button:
	public void disableHintButton() {
		hintButton.setEnabled(false);
	}
	
	
	// Method to disable the controls which shouldn't be used when not in a round:
	public void disableControls() {
		hintButton.setEnabled(false);
		submitButton.setEnabled(false);
		solutionTextPane.setEditable(false);
		customTestButton.setEnabled(false);
	}
	
	
	// Method to make the custom test dialog's layout change according to the problem's input variables:
	public void setCustomTestDialogLayout(String[] variableNames) {
		int numberOfVariables = variableNames.length; // Store the parameter's length as the number of variables.
		
		// Make the label and text field of each variable except the first invisible:
		variable2Label.setVisible(false);
		variable2TextField.setVisible(false);
		variable3Label.setVisible(false);
		variable3TextField.setVisible(false);
		variable4Label.setVisible(false);
		variable4TextField.setVisible(false);
		variable5Label.setVisible(false);
		variable5TextField.setVisible(false);
		variable6Label.setVisible(false);
		variable6TextField.setVisible(false);
		
		variable1Label.setText(variableNames[0]); // Make the label for the first variable display the first variable's name.
		
		// Switch-case block to change the elements depending on the number of input variables:
		switch (numberOfVariables) {
			
			// If there are 6 variables, make the text fields and labels of the 6th and below variables visible, and make the label display the variable's name:
			case 6:
				variable6Label.setVisible(true);
				variable6Label.setText(variableNames[5]);
				variable6TextField.setVisible(true);
			
			// If there are 5 variables, make the text fields and labels of the 5th and below variables visible, and make the label display the variable's name:
			case 5:
				variable5Label.setVisible(true);
				variable5Label.setText(variableNames[4]);
				variable5TextField.setVisible(true);
			
			// If there are 4 variables, make the text fields and labels of the 4th and below variables visible, and make the label display the variable's name:
			case 4:
				variable4Label.setVisible(true);
				variable4Label.setText(variableNames[3]);
				variable4TextField.setVisible(true);
			
			// If there are 3 variables, make the text fields and labels of the 3th and below variables visible, and make the label display the variable's name:
			case 3:
				variable3Label.setVisible(true);
				variable3Label.setText(variableNames[2]);
				variable3TextField.setVisible(true);
			
			// If there are 2 variables, make the text fields and labels of the 2th and below variables visible, and make the label display the variable's name:
			case 2:
				variable2Label.setVisible(true);
				variable2Label.setText(variableNames[1]);
				variable2TextField.setVisible(true);
		}
		
		int height = 300 - (6 - numberOfVariables) * 29; // Calculate the new height of the dialog which depends on how many variables are displayed.
		
		customTestDialog.setBounds(customTestDialog.getX(), customTestDialog.getY(), 180, height); // Change the size of the custom test dialog.
	}
	
	
	// Method to get the inputs in the custom test dialog as an array:
	public String[] getCustomTestInputs() {
		int numberOfVariables; // Create a variable to store the number of variables.
		String[] inputsTemp = new String[6]; // Create a String[] array to temporarily store up to 6 inputs.
		
		inputsTemp[0] = variable1TextField.getText(); // Store the text in the first variable's text field in the temporary array.
		numberOfVariables = 1; // Set the number of variables to 1 as there are always at least 1 variable.
		
		// Store the text in any other visible text fields in the temporary array:
		if (variable2TextField.isVisible()) {
			if (variable3TextField.isVisible()) {
				if (variable4TextField.isVisible()) {
					if (variable5TextField.isVisible()) {
						if (variable6TextField.isVisible()) {
							numberOfVariables++;
							inputsTemp[5] = variable6TextField.getText();
						}
						numberOfVariables++;
						inputsTemp[4] = variable5TextField.getText();
					}
					numberOfVariables++;
					inputsTemp[3] = variable4TextField.getText();
				}
				numberOfVariables++;
				inputsTemp[2] = variable3TextField.getText();
			}
			numberOfVariables++;
			inputsTemp[1] = variable2TextField.getText();
		}
		
		String[] inputs = new String[numberOfVariables]; // Create a String[] array to store as many inputs as there actually are.
		
		// Copy each desired element in the temporary array to the new array:
		for (int i = 0; i < numberOfVariables; i++) {
			inputs[i] = inputsTemp[i];
		}
		
		return inputs; // Return the array of inputs.
	}
	
	
	// Method to programmatically close the custom test dialog:
	public void closeCustomTestDialog() {
		customTestDialog.setEnabled(false); // Disable the dialog.
		customTestDialog.setVisible(false); // Make the dialog invisible.
	}
	
	
	// Method to display the results of a custom test:
	public void showCustomTestResults(String[] inputs, String output) {
		String outputText = outputTextArea.getText(); // Get and store the text that is currently being displayed in the output text area.
		
		outputText += "Inputs: "; // Add a label to the output text to indicate that the following text will be the inputs.
		
		// Add each input to the output text, each with a comma and space following it:
		for (String input: inputs) {
			outputText += input + ", ";
		}
		
		outputText = outputText.substring(0, outputText.length() - 2); // Remove the last extra comma and space from the output text.
		
		outputText += "\nOutput: " + output + "\n\n"; // Add a label and the output to the output text.
		
		outputTextArea.setText(outputText); // Update the output text area with the new output text.
		outputTextArea.setCaretPosition(outputTextArea.getDocument().getLength()); // Automatically scroll to the bottom of the text area.
	}
	
	
	// Method to display the information about the player's score:
	public void displayScoreInformation(int points, int numberOfPasses) {
		int score = points * numberOfPasses / 20; // Calculate the player's score.
		
		int percentageCorrect = 5 * numberOfPasses; // Calculate the percentage of passed tests.
		
		// Display the points, the percentage correct and the score:
		outputTextArea.setText("You scored "+points+"/80 points for "+gameModeString+".\n"+
				"You passed "+percentageCorrect+"% of the tests\n"+
				"Your total score is "+score+".\n\n");
	}
	
	
	// Method to show a message in a message dialog:
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
	
	
	// Method to change the GUI according to the settings:
	public void applySettings(int[] settings) {
		
		// If there are no settings:
		if (settings == null) {
			
			// Set the settings to the default settings:
			autoIndentationCheckBox.setSelected(true);
			autoBracketClosingCheckBox.setSelected(true);
			keywordHighlightingCheckBox.setSelected(true);
			lineHighlightingCheckBox.setSelected(true);
			occurenceHighlightingCheckBox.setSelected(true);
			autoStringClosingCheckBox.setSelected(true);
			defaultColourRadioButton.setSelected(true);
			setButtonColour(-986896);
			setMainTextColour(-16777216);
			setCodeColour(-16777216);
			setKeywordColour(-16776961);
			setMainBackgroundColour(-986896);
			setSecondaryBackgroundColour(-1);
			setTextEditorBackgroundColour(-1);
			setLineHighlightingColour(-1644826);
			setOccurrenceHighlightingColour(-256);
			
			// Set newSettings to the default settings:
			newSettings = new int[] {1, 1, 1, 1, 1, 1, 0, -986896, -16777216, -16777216, 16776961, -986896, -1, -1, -1644826, -256};
		}
		
		
		// If there are settings:
		else {
			newSettings = settings.clone(); // Set newSettings to a clone of settings.
			
			// Select the check boxes depending on the settings:
			lineHighlightingCheckBox.setSelected(settings[0] == 1);
			keywordHighlightingCheckBox.setSelected(settings[1] == 1);
			occurenceHighlightingCheckBox.setSelected(settings[2] == 1);
			autoIndentationCheckBox.setSelected(settings[3] == 1);
			autoBracketClosingCheckBox.setSelected(settings[4] == 1);
			autoStringClosingCheckBox.setSelected(settings[5] == 1);
		
			// If the default option is selected:
			if (settings[6] == 0) {
				
				defaultColourRadioButton.setSelected(true); // Make the default radio button selected.
				
				// Set the colours to the default colours:
				setButtonColour(-986896);
				setMainTextColour(-16777216);
				setCodeColour(-16777216);
				setKeywordColour(-16776961);
				setMainBackgroundColour(-986896);
				setSecondaryBackgroundColour(-1);
				setTextEditorBackgroundColour(-1);
				setLineHighlightingColour(-1644826);
				setOccurrenceHighlightingColour(-256);
			}
			
			// If the dark option is selected:
			else if (settings[6] == 1) {
				
				darkColourRadioButton.setSelected(true); // Make the dark radio button selected.
				
				// Set the colours to the dark colours:
				setButtonColour(-13421773);
				setMainTextColour(-1);
				setCodeColour(-1);
				setKeywordColour(-26317);
				setMainBackgroundColour(-16777216);
				setSecondaryBackgroundColour(-13421773);
				setTextEditorBackgroundColour(-13421773);
				setLineHighlightingColour(-10066330);
				setOccurrenceHighlightingColour(-6710887);
			}
			
			// If the custom option is selected:
			else {
				
				customColourRadioButton.setSelected(true); // Make the custom radio button selected.
				
				// Set the colours to the colours specified in the settings:
				setButtonColour(settings[7]);
				setMainTextColour(settings[8]);
				setCodeColour(settings[9]);
				setKeywordColour(settings[10]);
				setMainBackgroundColour(settings[11]);
				setSecondaryBackgroundColour(settings[12]);
				setTextEditorBackgroundColour(settings[13]);
				setLineHighlightingColour(settings[14]);
				setOccurrenceHighlightingColour(settings[15]);
			}
			
			
			// If line highlighting is disabled, make the line highlighting colour the same as the text editor's background colour:
			if (settings[0] == 0) {
				setLineHighlightingColour(settings[13]);
			}
			
			// If keyword highlighting is disabled, make the keyword highlighting colour the same as the code colour:
			if (settings[1] == 0) {
				setKeywordColour(settings[9]);
			}
			
			// If occurrence highlighting is disabled, make the occurrence highlighting colour the same as the text editor's background colour:
			if (settings[2] == 0) {
				setOccurrenceHighlightingColour(settings[13]);
			}
			
			// Toggle the automatic indentation, bracket closing and string closing depending on the settings:
			ideDocumentFilter.doAutoIndentation(settings[3] == 1);
			ideDocumentFilter.doAutoBracketClosing(settings[4] == 1);
			ideDocumentFilter.doAutoStringClosing(settings[5] == 1);
		}
	}
	
	
	// Method to set the colour of each button:
	public void setButtonColour(int rgb) {
		
		// If the colour is not the default colour:
		if (rgb != -986896) {
			
			// Iterate through each button:
			for (JButton button: buttons) {
				
				// Change the background colour of the button and make the background colour visible within the button:
				button.setBackground(new Color(rgb));
				button.setContentAreaFilled(false);
				button.setOpaque(true);
			}
		}
		
		// If the colour is the default colour:
		else {
			
			// Iterate through each button:
			for (JButton button: buttons) {
				
				// Change the background colour of the button but give it its default look:
				button.setBackground(new Color(rgb));
				button.setContentAreaFilled(true);
				button.setOpaque(false);
			}
		}
	}
	
	
	// Method to change the colour of the main text:
	public void setMainTextColour(int rgb) {
		
		// Iterate through each main text component and change its foreground colour:
		for (JComponent component: mainTextComponents) {
			component.setForeground(new Color(rgb));
		}
		
		// Iterate through each button and change its foreground colour:
		for (JButton button: buttons) {
			button.setForeground(new Color(rgb));
		}
		
		// Iterate through each secondary background component and change its foreground colour:
		for (JComponent component: secondaryBackgroundComponents) {
			component.setForeground(new Color(rgb));
		}
	}
	
	
	// Method to change the main background colour:
	public void setMainBackgroundColour(int rgb) {
		
		// Iterate through each main background component and change its background colour:
		for (JComponent component: mainBackgroundComponents) {
			component.setBackground(new Color(rgb));
		}
		
		// Iterate through each main text component and change its background colour:
		for (JComponent component: mainTextComponents) {
			component.setBackground(new Color(rgb));
		}
	}
	
	
	// Method to change the secondary background colour:
	public void setSecondaryBackgroundColour(int rgb) {
		
		// Iterate through each secondary background component and change its background:
		for (JComponent component: secondaryBackgroundComponents) {
			component.setBackground(new Color(rgb));
		}
	}
	
	
	// Method to change the code colour:
	public void setCodeColour(int rgb) {
		ideDocumentFilter.setCodeColour(rgb);
	}
	
	
	// Method to change the keyword colour:
	public void setKeywordColour(int rgb) {
		ideDocumentFilter.setKeywordColour(rgb);
	}
	
	
	// Method to change the text editor background colour:
	public void setTextEditorBackgroundColour(int rgb) {
		textEditorBackgroundColour = new Color(rgb);
	}
	
	
	// Method to change the line highlighting colour:
	public void setLineHighlightingColour(int rgb) {
		lineHighlightingColour = new Color(rgb);
	}
	
	
	// Method to change the occurrence highlighting colour:
	public void setOccurrenceHighlightingColour(int rgb) {
		ideDocumentFilter.setOccurrenceHighlightingColour(rgb);
	}
	
	
	// Method to open the colour picker dialog and show the colour of the option chosen:
	public void openColourPickerDialog(String type) {
		
		// Set the colour in the JColorChooser to the colour of the option chosen,
		// and set lastOpenedColourSetting to the index of the colour option choson:
		if (type.equals("Buttons")) {
			colourChooser.setColor(newSettings[7]);
			lastOpenedColourSetting = 7;
		} else if (type.equals("MainText")) {
			colourChooser.setColor(newSettings[8]);
			lastOpenedColourSetting = 8;
		} else if (type.equals("Code")) {
			colourChooser.setColor(newSettings[9]);
			lastOpenedColourSetting = 9;
		} else if (type.equals("Keywords")) {
			colourChooser.setColor(newSettings[10]);
			lastOpenedColourSetting = 10;
		} else if (type.equals("MainBackground")) {
			colourChooser.setColor(newSettings[11]);
			lastOpenedColourSetting = 11;
		} else if (type.equals("SecondaryBackground")) {
			colourChooser.setColor(newSettings[12]);
			lastOpenedColourSetting = 12;
		} else if (type.equals("TextEditorBackground")) {
			colourChooser.setColor(newSettings[13]);
			lastOpenedColourSetting = 13;
		} else if (type.equals("LineHighlighting")) {
			colourChooser.setColor(newSettings[14]);
			lastOpenedColourSetting = 14;
		} else if (type.equals("OccurrenceHighlighting")) {
			colourChooser.setColor(newSettings[15]);
			lastOpenedColourSetting = 15;
		}
		
		colourPickerDialog.setVisible(true); // Make the colour picker dialog visible.
	}
	
	
	// Method to get the new settings:
	public int[] getNewSettings() {
		
		// Set the settings for the check boxes and radio buttons depending on whether they are selected or not:
		
		if (lineHighlightingCheckBox.isSelected()) {
			newSettings[0] = 1;
		} else {
			newSettings[0] = 0;
		}
		
		if (keywordHighlightingCheckBox.isSelected()) {
			newSettings[1] = 1;
		} else {
			newSettings[1] = 0;
		}
		
		if (occurenceHighlightingCheckBox.isSelected()) {
			newSettings[2] = 1;
		} else {
			newSettings[2] = 0;
		}
		
		if (autoIndentationCheckBox.isSelected()) {
			newSettings[3] = 1;
		} else {
			newSettings[3] = 0;
		}
		
		if (autoBracketClosingCheckBox.isSelected()) {
			newSettings[4] = 1;
		} else {
			newSettings[4] = 0;
		}
		
		if (autoStringClosingCheckBox.isSelected()) {
			newSettings[5] = 1;
		} else {
			newSettings[5] = 0;
		}
		
		if (defaultColourRadioButton.isSelected()) {
			newSettings[6] = 0;
		} else if (darkColourRadioButton.isSelected()) {
			newSettings[6] = 1;
		} else {
			newSettings[6] = 2;
		}
		
		return newSettings; // Return the new setting.
	}
}

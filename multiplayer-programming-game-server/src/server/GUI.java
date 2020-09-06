package server; //TODO modularise

import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import reorder.ReorderableTableModel;
import reorder.TableRowTransferHandler;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {
	
	// Create variables for the GUI elements that are used in multiple methods:
	private JTextField serverNameTextField;
	private JTextField portTextField;
	private JTextField pythonLocationTextField;
	private JButton startServerButton;
	
	private JLabel titleLabel;
	private JTextArea descriptionTextArea;
	private JTextField minsTextField;
	private JTextField secsTextField;
	private JButton defaultTimeButton;
	private JButton startRoundButton;
	private JLabel roundStatusLabel;
	private JRadioButton efficiencyModeRadioButton;
	private JRadioButton lengthModeRadioButton;
	private JRadioButton timeModeRadioButton;
	private JRadioButton randomModeRadioButton;
	
	private JTable problemTable;
	private JButton editProblemButton;
    
    private JDialog editProblemDialog;
    private JTextField editProblemIDTextField;
    private JTextField editProblemTitleTextField;
    private JTextArea editProblemDescriptionTextArea;
    private JTextField editProblemHintTextField;
    private JTextField editProblemTimeLimitTextField;
    private JTextArea editProblemSolutionTextArea;
    private JButton editProblemSaveButton;
    private JTable editProblemVariablesTable;
	
    private JTable playersTable;
	private JButton banButton;
	private JButton kickButton;
    private JButton bannedPlayersButton;
	
    private JDialog bannedPlayersDialog;
    private JTable bannedPlayersTable;
    private JButton unbanButton;
    
	private JLabel serverStatusLabel;
	private JPanel serverStatusIndicatorPanel;
	
	// Create DefaultTableModels for the tables, so that the tables can be read and changed:
	private DefaultTableModel problemTableModel;
	private DefaultTableModel playersTableModel;
	private DefaultTableModel bannedPlayersTableModel;
	private DefaultTableModel editProblemVariablesTableModel;
	
	// Create Swing Timers to allow counting down until a round starts/ends without blocking the thread:
	private Timer startRoundTimer;
	private Timer endRoundTimer;
	
	
	// Constructor which is called when this object is created:
	public GUI() {

		// Create the GUI elements:
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel serverOuterPanel = new JPanel();
		JPanel serverMiddlePanel = new JPanel();
		JPanel serverInnerPanel = new JPanel();
		JLabel serverNameLabel = new JLabel();
		serverNameTextField = new JTextField();
		JLabel portLabel = new JLabel();
		portTextField = new JTextField();
		JLabel pythonLocationLabel = new JLabel();
		pythonLocationTextField = new JTextField();
		startServerButton = new JButton();
		
		JPanel overviewOuterPanel = new JPanel();
		JPanel overviewMiddlePanel = new JPanel();
		JPanel overviewInnerPanel = new JPanel();
		titleLabel = new JLabel();
		JScrollPane descriptionScrollPane = new JScrollPane();
		descriptionTextArea = new JTextArea();
		JLabel timeRemainingHeadingLabel = new JLabel();
		defaultTimeButton = new JButton();
		minsTextField = new JTextField();
		JLabel minsLabel = new JLabel();
		secsTextField = new JTextField();
		JLabel secsLabel = new JLabel();
		startRoundButton = new JButton();
		roundStatusLabel = new JLabel();
		JLabel modeLabel = new JLabel();
		ButtonGroup modeButtonGroup = new ButtonGroup();
		efficiencyModeRadioButton = new JRadioButton();
		lengthModeRadioButton = new JRadioButton();
		timeModeRadioButton = new JRadioButton();
		randomModeRadioButton = new JRadioButton();
		
		JPanel problemQueueOuterPanel = new JPanel();
		JPanel problemQueueInnerPanel = new JPanel();
		JLabel problemQueueHeadingLabel = new JLabel();
		JScrollPane problemTableScrollPane = new JScrollPane();
		problemTable = new JTable();
		JButton randomiseProblemsButton = new JButton();
		JButton createProblemButton = new JButton();
		editProblemButton = new JButton();
		
		editProblemDialog = new JDialog((Dialog) null);
		JLabel editProblemIDLabel = new JLabel();
	    editProblemIDTextField = new JTextField();
	    JLabel editProblemTitleLabel = new JLabel();
	    editProblemTitleTextField = new JTextField();
	    JLabel editProblemDescriptionLabel = new JLabel();
	    JScrollPane editProblemDescriptionScrollPane = new JScrollPane();
	    editProblemDescriptionTextArea = new JTextArea();
	    JLabel editProblemHintLabel = new JLabel();
	    editProblemHintTextField = new JTextField();
	    JLabel editProblemTimeLimitLabel = new JLabel();
	    editProblemTimeLimitTextField = new JTextField();
	    JLabel editProblemVariablesLabel = new JLabel();
	    JScrollPane editProblemVariablesScrollPane = new JScrollPane();
	    editProblemVariablesTable = new JTable();
	    JLabel editProblemSolutionLabel = new JLabel();
	    JScrollPane editProblemSolutionScrollPane = new JScrollPane();
	    editProblemSolutionTextArea = new JTextArea();
	    editProblemSaveButton = new JButton();
	    JButton editProblemCancelButton = new JButton();
		
	    JPanel playersOuterPanel = new JPanel();
		JPanel playersInnerPanel = new JPanel();
		JLabel playersHeadingLabel = new JLabel();
		JScrollPane playersScrollPane = new JScrollPane();
		playersTable = new JTable();
		bannedPlayersButton = new JButton();
		banButton = new JButton();
		kickButton = new JButton();
		
		bannedPlayersDialog = new JDialog((Dialog) null);
		JLabel bannedPlayersLabel = new JLabel();
		JScrollPane bannedPlayersScrollPane = new JScrollPane();
		bannedPlayersTable = new JTable();
		unbanButton = new JButton();
	    
		JPanel serverStatusPanel = new JPanel();
		serverStatusLabel = new JLabel();
		serverStatusIndicatorPanel = new JPanel();
		
		
		// NetBeans generated code starts:
		editProblemTitleLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemTitleLabel.setText("Title:");

		editProblemTitleTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemTitleTextField.setText("");

		editProblemDescriptionLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemDescriptionLabel.setText("");

		editProblemDescriptionTextArea.setColumns(20);
		editProblemDescriptionTextArea.setLineWrap(true);
		editProblemDescriptionTextArea.setRows(5);
		editProblemDescriptionTextArea.setText("");
		editProblemDescriptionTextArea.setWrapStyleWord(true);
		editProblemDescriptionScrollPane.setViewportView(editProblemDescriptionTextArea);

		editProblemHintLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemHintLabel.setText("Hint:");

		editProblemHintTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemHintTextField.setText("");

		editProblemTimeLimitTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemTimeLimitTextField.setText("");

		editProblemTimeLimitLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemTimeLimitLabel.setText("Time limit (seconds):");

		editProblemSolutionLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemSolutionLabel.setText("Solution:");

		editProblemSolutionTextArea.setColumns(20);
		editProblemSolutionTextArea.setLineWrap(true);
		editProblemSolutionTextArea.setRows(5);
		editProblemSolutionTextArea.setText("");
		editProblemSolutionTextArea.setWrapStyleWord(true);
		editProblemSolutionScrollPane.setViewportView(editProblemSolutionTextArea);

		editProblemVariablesScrollPane.setViewportView(editProblemVariablesTable);
		if (editProblemVariablesTable.getColumnModel().getColumnCount() > 0) {
			editProblemVariablesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
			editProblemVariablesTable.getColumnModel().getColumn(1).setPreferredWidth(50);
			editProblemVariablesTable.getColumnModel().getColumn(2).setPreferredWidth(40);
			editProblemVariablesTable.getColumnModel().getColumn(3).setPreferredWidth(40);
		}

		editProblemVariablesLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemVariablesLabel.setText("Variables:");

		editProblemSaveButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemSaveButton.setText("Save");

		editProblemCancelButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemCancelButton.setText("Cancel");

		editProblemIDLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemIDLabel.setText("ID:");

		editProblemIDTextField.setEditable(false);
		editProblemIDTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemIDTextField.setText("");

		javax.swing.GroupLayout editProblemDialogLayout = new javax.swing.GroupLayout(editProblemDialog.getContentPane());
        editProblemDialog.getContentPane().setLayout(editProblemDialogLayout);
        editProblemDialogLayout.setHorizontalGroup(
            editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editProblemDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(editProblemDescriptionLabel)
                    .addComponent(editProblemVariablesLabel)
                    .addComponent(editProblemTimeLimitLabel)
                    .addComponent(editProblemTimeLimitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editProblemVariablesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(editProblemDescriptionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(editProblemHintLabel)
                    .addComponent(editProblemHintTextField)
                    .addGroup(editProblemDialogLayout.createSequentialGroup()
                        .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editProblemIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editProblemIDLabel))
                        .addGap(18, 18, 18)
                        .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editProblemTitleLabel)
                            .addComponent(editProblemTitleTextField))))
                .addGap(18, 18, 18)
                .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editProblemSolutionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addGroup(editProblemDialogLayout.createSequentialGroup()
                        .addComponent(editProblemSolutionLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(editProblemDialogLayout.createSequentialGroup()
                        .addComponent(editProblemCancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(editProblemSaveButton)))
                .addContainerGap())
        );
        editProblemDialogLayout.setVerticalGroup(
            editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editProblemDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(editProblemDialogLayout.createSequentialGroup()
                        .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editProblemIDLabel)
                            .addComponent(editProblemTitleLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editProblemIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editProblemTitleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editProblemDescriptionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editProblemDescriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editProblemHintLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editProblemHintTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editProblemTimeLimitLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editProblemTimeLimitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editProblemVariablesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editProblemVariablesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
                    .addGroup(editProblemDialogLayout.createSequentialGroup()
                        .addComponent(editProblemSolutionLabel)
                        .addGap(6, 6, 6)
                        .addComponent(editProblemSolutionScrollPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editProblemDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editProblemSaveButton)
                            .addComponent(editProblemCancelButton))))
                .addContainerGap())
        );
		editProblemDialog.setSize(700, 500);
        
		bannedPlayersScrollPane.setViewportView(bannedPlayersTable);
		if (bannedPlayersTable.getColumnModel().getColumnCount() > 0) {
			bannedPlayersTable.getColumnModel().getColumn(0).setResizable(false);
			bannedPlayersTable.getColumnModel().getColumn(1).setResizable(false);
			bannedPlayersTable.getColumnModel().getColumn(1).setPreferredWidth(90);
		}

		bannedPlayersLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		bannedPlayersLabel.setText("Banned players:");

		unbanButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		unbanButton.setText("Unban selected");

		javax.swing.GroupLayout bannedPlayersDialogLayout = new javax.swing.GroupLayout(
				bannedPlayersDialog.getContentPane());
		bannedPlayersDialog.getContentPane().setLayout(bannedPlayersDialogLayout);
		bannedPlayersDialogLayout.setHorizontalGroup(bannedPlayersDialogLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(bannedPlayersDialogLayout.createSequentialGroup().addGroup(bannedPlayersDialogLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(bannedPlayersDialogLayout.createSequentialGroup().addContainerGap()
								.addGroup(bannedPlayersDialogLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(bannedPlayersLabel).addComponent(bannedPlayersScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE, 262,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(0, 0, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bannedPlayersDialogLayout
								.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(unbanButton)))
						.addContainerGap()));
		bannedPlayersDialogLayout.setVerticalGroup(bannedPlayersDialogLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bannedPlayersDialogLayout.createSequentialGroup()
						.addContainerGap().addComponent(bannedPlayersLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(bannedPlayersScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 157,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(unbanButton)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		bannedPlayersDialog.setSize(300, 280);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(0, 0, 0));
		setForeground(new java.awt.Color(0, 0, 0));
		setResizable(false);

		serverOuterPanel.setBackground(new java.awt.Color(255, 255, 255));

		serverMiddlePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		serverInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		serverNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
		serverNameLabel.setText("Server name:");

		serverNameTextField.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
		serverNameTextField.setText("Dave's server");

		portLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
		portLabel.setText("Port:");

		portTextField.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
		portTextField.setText("80");

		pythonLocationLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
		pythonLocationLabel.setText("Python location:");

		pythonLocationTextField.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
		pythonLocationTextField.setText("C:\\Program Files (x86)\\Python\\");

		startServerButton.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
		startServerButton.setText("Start");

		javax.swing.GroupLayout serverInnerPanelLayout = new javax.swing.GroupLayout(serverInnerPanel);
		serverInnerPanel.setLayout(serverInnerPanelLayout);
		serverInnerPanelLayout.setHorizontalGroup(
				serverInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(serverInnerPanelLayout.createSequentialGroup().addGap(160, 160, 160)
								.addComponent(startServerButton)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverInnerPanelLayout
								.createSequentialGroup().addContainerGap(69, Short.MAX_VALUE)
								.addGroup(serverInnerPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(portLabel, javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(pythonLocationLabel, javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(serverNameLabel, javax.swing.GroupLayout.Alignment.TRAILING))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(serverInnerPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 52,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(serverNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 155,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(pythonLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
												155, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(54, 54, 54)));
		serverInnerPanelLayout.setVerticalGroup(serverInnerPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(serverInnerPanelLayout.createSequentialGroup().addGap(43, 43, 43)
						.addGroup(serverInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(serverNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(serverNameLabel))
						.addGroup(serverInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(portLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(serverInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(pythonLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(pythonLocationLabel))
						.addGap(18, 18, 18).addComponent(startServerButton).addContainerGap(103, Short.MAX_VALUE)));

		javax.swing.GroupLayout serverMiddlePanelLayout = new javax.swing.GroupLayout(serverMiddlePanel);
		serverMiddlePanel.setLayout(serverMiddlePanelLayout);
		serverMiddlePanelLayout.setHorizontalGroup(
				serverMiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								serverMiddlePanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(serverInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		serverMiddlePanelLayout
				.setVerticalGroup(
						serverMiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(serverMiddlePanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(serverInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));

		javax.swing.GroupLayout serverOuterPanelLayout = new javax.swing.GroupLayout(serverOuterPanel);
		serverOuterPanel.setLayout(serverOuterPanelLayout);
		serverOuterPanelLayout.setHorizontalGroup(
				serverOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								serverOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(serverMiddlePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		serverOuterPanelLayout
				.setVerticalGroup(
						serverOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(serverOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(serverMiddlePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));

		tabbedPane.addTab("Server", serverOuterPanel);

		overviewOuterPanel.setBackground(new java.awt.Color(255, 255, 255));

		overviewMiddlePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		overviewInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		titleLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		titleLabel.setText("The problem:");

		descriptionTextArea.setColumns(20);
		descriptionTextArea.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setRows(5);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setAutoscrolls(false);
		descriptionScrollPane.setViewportView(descriptionTextArea);

		timeRemainingHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		timeRemainingHeadingLabel.setText("Time remaining: ");

		defaultTimeButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        defaultTimeButton.setText("Default");

        minsTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        minsLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        minsLabel.setText("mins");

        secsTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        secsLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        secsLabel.setText("secs");

        startRoundButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        startRoundButton.setText("Start round");

        roundStatusLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        roundStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roundStatusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        modeLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        modeLabel.setText("Mode:");

        modeButtonGroup.add(efficiencyModeRadioButton);
        efficiencyModeRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        efficiencyModeRadioButton.setText("efficiency");

        modeButtonGroup.add(lengthModeRadioButton);
        lengthModeRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lengthModeRadioButton.setText("length");

        modeButtonGroup.add(timeModeRadioButton);
        timeModeRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        timeModeRadioButton.setText("time");

        modeButtonGroup.add(randomModeRadioButton);
        randomModeRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        randomModeRadioButton.setText("random");

        javax.swing.GroupLayout overviewInnerPanelLayout = new javax.swing.GroupLayout(overviewInnerPanel);
        overviewInnerPanel.setLayout(overviewInnerPanelLayout);
        overviewInnerPanelLayout.setHorizontalGroup(
            overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, overviewInnerPanelLayout.createSequentialGroup()
                .addGroup(overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(overviewInnerPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleLabel)
                            .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(overviewInnerPanelLayout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addGroup(overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, overviewInnerPanelLayout.createSequentialGroup()
                                            .addComponent(startRoundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(113, 113, 113))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, overviewInnerPanelLayout.createSequentialGroup()
                                            .addComponent(timeRemainingHeadingLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(minsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(minsLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(secsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(secsLabel)
                                            .addGap(18, 18, 18)
                                            .addComponent(defaultTimeButton))))
                                .addGroup(overviewInnerPanelLayout.createSequentialGroup()
                                    .addComponent(modeLabel)
                                    .addGap(18, 18, 18)
                                    .addComponent(efficiencyModeRadioButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(lengthModeRadioButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(timeModeRadioButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(randomModeRadioButton)))))
                    .addGroup(overviewInnerPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );
        overviewInnerPanelLayout.setVerticalGroup(
            overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeRemainingHeadingLabel)
                    .addComponent(minsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minsLabel)
                    .addComponent(secsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secsLabel)
                    .addComponent(defaultTimeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(overviewInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lengthModeRadioButton)
                    .addComponent(timeModeRadioButton)
                    .addComponent(efficiencyModeRadioButton)
                    .addComponent(modeLabel)
                    .addComponent(randomModeRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startRoundButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundStatusLabel)
                .addContainerGap(18, Short.MAX_VALUE))
        );

		javax.swing.GroupLayout overviewMiddlePanelLayout = new javax.swing.GroupLayout(overviewMiddlePanel);
		overviewMiddlePanel.setLayout(overviewMiddlePanelLayout);
		overviewMiddlePanelLayout.setHorizontalGroup(
				overviewMiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								overviewMiddlePanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(overviewInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		overviewMiddlePanelLayout
				.setVerticalGroup(
						overviewMiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(overviewMiddlePanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(overviewInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));

		javax.swing.GroupLayout overviewOuterPanelLayout = new javax.swing.GroupLayout(overviewOuterPanel);
		overviewOuterPanel.setLayout(overviewOuterPanelLayout);
		overviewOuterPanelLayout.setHorizontalGroup(
				overviewOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								overviewOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(overviewMiddlePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		overviewOuterPanelLayout
				.setVerticalGroup(
						overviewOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(overviewOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(overviewMiddlePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));

		tabbedPane.addTab("Game overview", overviewOuterPanel);

		problemQueueOuterPanel.setBackground(new java.awt.Color(255, 255, 255));

		problemQueueInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		problemTableScrollPane.setViewportView(problemTable);
		if (problemTable.getColumnModel().getColumnCount() > 0) {
			problemTable.getColumnModel().getColumn(0).setPreferredWidth(50);
			problemTable.getColumnModel().getColumn(1).setPreferredWidth(300);
			problemTable.getColumnModel().getColumn(2).setPreferredWidth(500);
		}

		randomiseProblemsButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		randomiseProblemsButton.setText("Randomise");

		problemQueueHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		problemQueueHeadingLabel.setText("Problem queue:");

		createProblemButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		createProblemButton.setText("Create new");

		editProblemButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		editProblemButton.setText("Edit selected");

		javax.swing.GroupLayout problemQueueInnerPanelLayout = new javax.swing.GroupLayout(problemQueueInnerPanel);
		problemQueueInnerPanel.setLayout(problemQueueInnerPanelLayout);
		problemQueueInnerPanelLayout.setHorizontalGroup(problemQueueInnerPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(problemQueueInnerPanelLayout.createSequentialGroup().addContainerGap()
						.addGroup(problemQueueInnerPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(problemQueueInnerPanelLayout.createSequentialGroup()
										.addComponent(randomiseProblemsButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93,
												Short.MAX_VALUE)
										.addComponent(createProblemButton).addGap(18, 18, 18)
										.addComponent(editProblemButton))
								.addComponent(problemTableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addGroup(problemQueueInnerPanelLayout.createSequentialGroup()
										.addComponent(problemQueueHeadingLabel).addGap(0, 0, Short.MAX_VALUE)))
						.addContainerGap()));
		problemQueueInnerPanelLayout.setVerticalGroup(
				problemQueueInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(problemQueueInnerPanelLayout.createSequentialGroup().addContainerGap()
								.addComponent(problemQueueHeadingLabel)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(problemTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 215,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
								.addGroup(problemQueueInnerPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(randomiseProblemsButton).addComponent(createProblemButton)
										.addComponent(editProblemButton))
								.addGap(25, 25, 25)));

		javax.swing.GroupLayout problemQueueOuterPanelLayout = new javax.swing.GroupLayout(problemQueueOuterPanel);
		problemQueueOuterPanel.setLayout(problemQueueOuterPanelLayout);
		problemQueueOuterPanelLayout.setHorizontalGroup(
				problemQueueOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								problemQueueOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(problemQueueInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		problemQueueOuterPanelLayout
				.setVerticalGroup(
						problemQueueOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(problemQueueOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(problemQueueInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));

		tabbedPane.addTab("Problem queue", problemQueueOuterPanel);

		playersOuterPanel.setBackground(new java.awt.Color(255, 255, 255));
		playersOuterPanel.setForeground(new java.awt.Color(255, 255, 255));

		playersInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		playersHeadingLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		playersHeadingLabel.setText("Currently connected players:");

		bannedPlayersButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		bannedPlayersButton.setText("Banned players");

		banButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		banButton.setText("Ban selected");

		kickButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		kickButton.setText("Kick selected");

		
		playersScrollPane.setViewportView(playersTable);
		if (playersTable.getColumnModel().getColumnCount() > 0) {
			playersTable.getColumnModel().getColumn(0).setPreferredWidth(125);
			playersTable.getColumnModel().getColumn(1).setPreferredWidth(200);
			playersTable.getColumnModel().getColumn(2).setPreferredWidth(90);
			playersTable.getColumnModel().getColumn(3).setPreferredWidth(70);
			playersTable.getColumnModel().getColumn(4).setPreferredWidth(70);
			playersTable.getColumnModel().getColumn(5).setPreferredWidth(110);
			playersTable.getColumnModel().getColumn(6).setPreferredWidth(90);
			playersTable.getColumnModel().getColumn(7).setPreferredWidth(60);
		}

		javax.swing.GroupLayout playersInnerPanelLayout = new javax.swing.GroupLayout(playersInnerPanel);
		playersInnerPanel.setLayout(playersInnerPanelLayout);
		playersInnerPanelLayout.setHorizontalGroup(playersInnerPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(playersInnerPanelLayout.createSequentialGroup().addContainerGap()
						.addGroup(playersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(playersInnerPanelLayout.createSequentialGroup()
										.addComponent(playersHeadingLabel).addGap(0, 0, Short.MAX_VALUE))
								.addGroup(playersInnerPanelLayout.createSequentialGroup()
										.addComponent(bannedPlayersButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71,
												Short.MAX_VALUE)
										.addComponent(kickButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(banButton)))
						.addContainerGap())
				.addGroup(playersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(playersInnerPanelLayout
								.createSequentialGroup().addContainerGap().addComponent(playersScrollPane,
										javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addContainerGap())));
		playersInnerPanelLayout.setVerticalGroup(playersInnerPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(playersInnerPanelLayout.createSequentialGroup().addContainerGap()
						.addComponent(playersHeadingLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
						.addGroup(playersInnerPanelLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(bannedPlayersButton).addComponent(banButton).addComponent(kickButton))
						.addContainerGap())
				.addGroup(playersInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(playersInnerPanelLayout
								.createSequentialGroup().addGap(40, 40, 40).addComponent(playersScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
								.addGap(41, 41, 41))));

		javax.swing.GroupLayout playersOuterPanelLayout = new javax.swing.GroupLayout(playersOuterPanel);
		playersOuterPanel.setLayout(playersOuterPanelLayout);
		playersOuterPanelLayout
				.setHorizontalGroup(
						playersOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(playersOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(playersInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		playersOuterPanelLayout
				.setVerticalGroup(
						playersOuterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(playersOuterPanelLayout.createSequentialGroup().addContainerGap()
										.addComponent(playersInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));

		tabbedPane.addTab("Players", playersOuterPanel);

		serverStatusLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
		serverStatusLabel.setText("Server is not online");

		serverStatusIndicatorPanel.setBackground(new java.awt.Color(255, 0, 0));

		javax.swing.GroupLayout serverStatusIndicatorPanelLayout = new javax.swing.GroupLayout(
				serverStatusIndicatorPanel);
		serverStatusIndicatorPanel.setLayout(serverStatusIndicatorPanelLayout);
		serverStatusIndicatorPanelLayout.setHorizontalGroup(serverStatusIndicatorPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		serverStatusIndicatorPanelLayout.setVerticalGroup(serverStatusIndicatorPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		javax.swing.GroupLayout serverStatusPanelLayout = new javax.swing.GroupLayout(serverStatusPanel);
		serverStatusPanel.setLayout(serverStatusPanelLayout);
		serverStatusPanelLayout.setHorizontalGroup(
				serverStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverStatusPanelLayout
								.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(serverStatusLabel)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(serverStatusIndicatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
		serverStatusPanelLayout.setVerticalGroup(serverStatusPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(serverStatusPanelLayout.createSequentialGroup()
						.addGroup(serverStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(serverStatusLabel).addComponent(serverStatusIndicatorPanel,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(tabbedPane)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(serverStatusPanel,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(serverStatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();

		setVisible(true);

		// NetBeans generated code ends.
		
		// Allow only one row in the problem table to be selected at a time:
		problemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		efficiencyModeRadioButton.setSelected(true); // Make the efficiency radio button automatically selected.
		
		// Create and add table models to the tables:
		playersTableModel = new DefaultTableModel(new Object[][] {},
				new String[] {"Username", "IP address", "Status", "Score", "Total", "Average", "Played", "Won"}) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		playersTable.setModel(playersTableModel);
		
		problemTableModel = new ReorderableTableModel(new Object[][] {},
				new String[] { "ID", "Title", "Description" });
		problemTable.setModel(problemTableModel);
		problemTable.setDragEnabled(true);
		problemTable.setDropMode(DropMode.INSERT_ROWS);
		problemTable.setTransferHandler(new TableRowTransferHandler(problemTable));
		
		
		bannedPlayersTableModel = new DefaultTableModel(new Object[][] {},
				new String[] {"IP address", "Last recorded username"}) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		bannedPlayersTable.setModel(bannedPlayersTableModel);
		
		
		editProblemVariablesTableModel = new DefaultTableModel(new Object[][] {},
				new String[] {"# of tests", "Variable name", "Lower limit", "Upper limit"}) {
			private static final long serialVersionUID = 1L;
		};
		for (int i = 0; i < 121; i++) {
			editProblemVariablesTableModel.addRow(new Object[] {});
		}
		editProblemVariablesTable.setModel(editProblemVariablesTableModel);
		
		// Add the action listeners to the buttons:
		randomiseProblemsButton.addActionListener(this);
		createProblemButton.addActionListener(this);
		editProblemButton.addActionListener(this);
		editProblemCancelButton.addActionListener(this);
		bannedPlayersButton.addActionListener(this);

		// Add the action commands to the buttons:
		startServerButton.setActionCommand("startserver");
		startRoundButton.setActionCommand("startround");
		defaultTimeButton.setActionCommand("default");
		randomiseProblemsButton.setActionCommand("randomiseproblems");
		createProblemButton.setActionCommand("createproblem");
		editProblemButton.setActionCommand("editproblem");
		editProblemSaveButton.setActionCommand("saveedit");
		editProblemCancelButton.setActionCommand("canceledit");
		kickButton.setActionCommand("kick");
		banButton.setActionCommand("ban");
		bannedPlayersButton.setActionCommand("bannedplayers");
		unbanButton.setActionCommand("unban");
	}
	
	
	// Method to add the Main object as an action listener of three of the buttons:
	public void setActionListener(Main main) {
		startServerButton.addActionListener(main);
		startRoundButton.addActionListener(main);
		defaultTimeButton.addActionListener(main);
		editProblemSaveButton.addActionListener(main);
		editProblemButton.addActionListener(main);
		kickButton.addActionListener(main);
		banButton.addActionListener(main);
		unbanButton.addActionListener(main);
	}
	
	
	// Method to update the player table when given the data of a single client (in the form of an Object[] array):
	public void updatePlayerTable(Object[] playerData) {
		for (int i = 0; i < playersTableModel.getRowCount(); i++) { // Loop through the table's rows.
			
			// If the first field of the row at index i is equal to the first element of the array (the usernames match):
			if (playersTableModel.getValueAt(i, 0).equals(playerData[0])) {
				
				// Loop through the fields of the row at index i, making the jth field equal to the jth element of the array:
				for (int j = 0; j < playersTableModel.getColumnCount(); j++) {
					playersTableModel.setValueAt(playerData[j], i, j);
				}
				
				return; // The table has been updated, so return.
			}
		}
		
		playersTableModel.addRow(playerData); // If the method hasn't returned yet, the player isn't in the table, so add a new row to it.
	}
	
	
	// Method to get all the data in the player table in the form of an Object[][]:
	public Object[][] getAllPlayerData() {
		int rowCount = playersTableModel.getRowCount(); // Get and store the table's row count.
		int columnCount = playersTableModel.getColumnCount(); // Get and store the table's column count.
		
		Object[][] playerData = new Object[rowCount][columnCount]; // Create an Object[][] array, playerData, with the same dimensions as the table.
		
		// Iterate through each field of the table and add to the Object[][]:
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				playerData[i][j] = playersTableModel.getValueAt(i, j);
			}
		}
		
		return playerData; // Return the result.
	}
	
	
	// Method to start a countdown for the start of the round:
	public void startRoundCountdown(String problemTitle, String problemDescription, long startTime, long endTime) {
		
		// Disable the buttons on the problem tab:
		startRoundButton.setEnabled(false);
		defaultTimeButton.setEnabled(false);
		efficiencyModeRadioButton.setEnabled(false);
		lengthModeRadioButton.setEnabled(false);
		timeModeRadioButton.setEnabled(false);
		randomModeRadioButton.setEnabled(false);
		
		startRoundTimer = new Timer(250, null); // Create a timer which activates every 250 milliseconds.
		
		// Create an ActionListener and add it to the timer:
		startRoundTimer.addActionListener(new ActionListener() {
			
			// Method which is called when the timer activates:
			public void actionPerformed(ActionEvent event) {
				long timeLeft = startTime - System.currentTimeMillis(); // Calculate and store the time left until the round should start.
				
				// If there is no time left:
				if (timeLeft <= 0) {
					startRoundTimer.stop(); // Stop the timer.
					roundStatusLabel.setText(""); // Reset the round status label.
					startRound(problemTitle, problemDescription, endTime); // Call startRound().
				}
				
				// If there is time left, calculate the minutes and seconds left and show this on the GUI using the round status label:
				else {
					long minsLeft = timeLeft / 60000;
					long secsLeft = (timeLeft % 60000) / 1000;
					roundStatusLabel.setText("New round in " + minsLeft + " : " + secsLeft);
				}
			}
		});
		
		startRoundTimer.start(); // Start the timer.
	}
	
	
	// Method to start a countdown for the end of the round:
	private void startRound(String problemTitle, String problemDescription, long endTime) {
		
		// Display the title and description:
		titleLabel.setText(problemTitle);
		descriptionTextArea.setText(problemDescription);
		
		endRoundTimer = new Timer(250, null); // Create a timer which activates every 250 milliseconds.
		
		// Create an ActionListener and add it to the timer:
		endRoundTimer.addActionListener(new ActionListener() {
			
			//Method which is called when the timer activates:
			public void actionPerformed(ActionEvent event) {
				long timeLeft = endTime - System.currentTimeMillis(); // Calculate and store the time left until the round should end.
				
				// If there is no time left, stop the timer:
				if (timeLeft <= 0) {
					endRoundTimer.stop();
				}
				
				// If there is time left, calculate the minutes and seconds left and show this on the GUI using the text areas:
				else {
					long minsLeft = timeLeft / 60000;
					long secsLeft = (timeLeft % 60000) / 1000;
					minsTextField.setText(Long.toString(minsLeft));
					secsTextField.setText(Long.toString(secsLeft));
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
		
		// Enable the buttons on the problem tab:
		startRoundButton.setEnabled(true);
		defaultTimeButton.setEnabled(true);
		efficiencyModeRadioButton.setEnabled(true);
		lengthModeRadioButton.setEnabled(true);
		timeModeRadioButton.setEnabled(true);
		randomModeRadioButton.setEnabled(true);
		
		// Reset the timer text fields:
		minsTextField.setText("");
		secsTextField.setText("");
	}
	
	
	// Method to change the round status label's text:
	public void setRoundStatus(String status) {
		roundStatusLabel.setText(status);
	}
	
	
	// Method to get the time entered in the timer text fields:
	public int getTime() {
		
		// Get and store the text fields' text:
		String minsString = minsTextField.getText();
		String secsString = secsTextField.getText();
		
		int mins, secs; // Create int variables to store the minutes and seconds seperately.
		
		// If the minutes text field was empty:
		if (!minsString.isEmpty()) {
			try { // The following line may throw an exception which I want to be caught.
				mins = Integer.parseInt(minsString); // Attempt to convert the minutes string into an integer and store it.
			}
			
			// If the string couldn't convert to an integer (there were non-numeric characters), return -1 to indicate an error:
			catch (NumberFormatException e) {
				return -1;
			}
		}
		
		// If the minutes text field was empty, treat it as 0 and set the minutes integer to 0:
		else {
			mins = 0;
		}
		
		// If the seconds text field was empty:
		if (!secsString.isEmpty()) {
			try { // The following line may throw an exception which I want to be caught.
				secs = Integer.parseInt(secsString); // Attempt to convert the seconds string into an integer and store it.
			}
			
			// If the string couldn't convert to an integer (there were non-numeric characters), return -1 to indicate an error:
			catch (NumberFormatException e) {
				return -1;
			}
		}
		
		// If the seconds text field was empty, treat it as 0 and set the seconds integer to 0:
		else {
			secs = 0;
		}
		
		// If either the minutes or seconds are out of range, return -1 to indicate an error:
		if (mins < 0 || secs < 0 || secs > 59) {
			return -1;
		}
		
		// If they were both in range, convert the times to a single milliseconds value and return it:
		else {
			return mins * 60000 + secs * 1000;
		}
	}
	
	
	// Method to set the time shown on the timer text areas:
	public void setTime(long milliseconds) {
		
		// Calculate the minutes and seconds from the milliseconds given:
		long mins = milliseconds / 60000;
		long secs = (milliseconds % 60000) / 1000;
		
		// Set the text fields' text to the calculated minutes and seconds:
		minsTextField.setText(Long.toString(mins));
		secsTextField.setText(Long.toString(secs));
	}
	
	
	// Method to set the server status label's text:
	public void setServerStatus(String status) {
		serverStatusLabel.setText(status);
	}
	
	
	// Method to set the server status indicator's colour:
	public void setServerStatusIndicatorColour(int r, int g, int b) {
		serverStatusIndicatorPanel.setBackground(new Color(r,g,b));
	}
	
	
	// Method to get the information entered by the user in the server tab:
	public String[] getServerStartInformation() {
		return new String[] {serverNameTextField.getText(), portTextField.getText(), pythonLocationTextField.getText()};
	}
	
	
	// Method to the get the ID of the first problem in the problem table:
	public String getFirstProblemID() {
		return String.valueOf(problemTableModel.getValueAt(0,0));
	}
	
	
	// Method to move the first problem in the problem table to the bottom:
	public void removeFirstProblem() {
		
		// Get and store the row and column counts of the problem table:
		int rowCount = problemTableModel.getRowCount();
		int columnCount = problemTableModel.getColumnCount();
		
		Object[] firstRow = new Object[columnCount]; // Create an Object[] array which will store the first row.
		
		// Loop through the array and add the first row's fields to it:
		for (int i = 0; i < columnCount; i++) {
			firstRow[i] = problemTableModel.getValueAt(0, i);
		}
		
		// Loop through the fields of all rows except the last, and set the value of each field equal to the value of the field directly below it:
		for (int i = 0; i < rowCount - 1; i++) {
			for (int j = 0; j < columnCount; j++) {
				problemTableModel.setValueAt(problemTableModel.getValueAt(i + 1, j), i, j);
			}
		}
		
		// Loop through the last row's fields and set the values of the fields to the values in the first row's array:
		for (int i = 0; i < columnCount; i++) {
			problemTableModel.setValueAt(firstRow[i], rowCount - 1, i);
		}
	}
	
	
	// Method to add a row of data (given by an Object[] array) to the problem table:
	public void addToProblemTable(Object[] problemData) {
		
		// Iterate through the table's rows:
		for (int i = 0; i < problemTableModel.getRowCount(); i++) {
			
			// If the first field of the row at index i is equal to the first element of the array (the IDs match):
			if (problemTableModel.getValueAt(i, 0).equals(problemData[0])) {
				
				// Loop through the fields of the row at index i, making the jth field equal to the jth element of the array:
				for (int j = 0; j < problemTableModel.getColumnCount(); j++) {
					problemTableModel.setValueAt(problemData[j], i, j);
				}
				
				return; // The table has been updated, so return.
			}
		}
		
		problemTableModel.addRow(problemData); // If the row doesn't already exist, add it.
	}
	
	
	// Method to disable the elements on the server tab:
	public void disableServerStartControls() {
		serverNameTextField.setEditable(false);
		portTextField.setEditable(false);
		pythonLocationTextField.setEditable(false);
		startServerButton.setEnabled(false);
	}
	
	
	// Method which is called when an action occurs in the GUI:
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand(); // Get and store the action command used to determine what the action was.
		
		// If the action was pressing the randomise button:
		if (command.equals("randomiseproblems")) {
			randomiseProblemQueue(); // Call randomiseProblemQueue() to randomise the problem queue.
		}
		
		// If the action was pressing the "create problem" button:
		else if (command.equals("createproblem")) {
			String id = String.format("%03d", problemTableModel.getRowCount()+1); // Calculate the ID of the new problem and format it to a 3-digit number as a string.
			openEditProblemDialog(id); // Open the edit problem dialog with the calculated ID.
		}
		
		// If the action was pressing the cancel button:
		else if (command.equals("canceledit")) {
			this.closeEditProblemDialog();
		}
		
		// If the action was pressing the "banned players" button:
		else if (command.equals("bannedplayers")) {
			bannedPlayersDialog.setVisible(true);
		}
	}
	
	
	// Method to randomise the problem queue:
	public void randomiseProblemQueue() {
		Random random = new Random(); // Create a Random object to allow generating random numbers.
		
		int rowCount = problemTableModel.getRowCount(); // Get and store the number of rows in the problem table.
		int columnCount = problemTableModel.getColumnCount(); // Get and store the number of columns in the problem table.
		
		Object[][] problemData = new Object[rowCount][columnCount]; // Create an array that can hold the problem table's data.
		
		// Iterate through each cell in the table and add it to the array.
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				problemData[i][j] = problemTableModel.getValueAt(i, j);
			}
		}
		
		Object[][] newProblemData = new Object[rowCount][columnCount]; // Create an array that will hold the new (randomised) problem table's data.
		
		// Iterate for each row in the table:
		for (int i = 0; i < rowCount; i++) {
			int randomIndex = random.nextInt(rowCount); // Generate a random index for the current row.
			
			// If the generated index is taken:
			if (newProblemData[randomIndex][0] != null) {
				
				// Increment the random index until it is not taken (and loop back to 0 when the end is reached):
				do {
					randomIndex = (randomIndex + 1) % rowCount;
				} while (newProblemData[randomIndex][0] != null);
			}
			
			newProblemData[randomIndex] = problemData[i]; // Add the current row's data to the new array in its new position.
		}
		
		// Iterate through the new array, adding each element to its cell in the problem queue table:
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				problemTableModel.setValueAt(newProblemData[i][j], i, j);
			}
		}
	}
	
	
	// Method to open the edit problem dialog and show the ID given:
	public void openEditProblemDialog(String id) {
		editProblemIDTextField.setText(id); // Show the ID in the ID text field.
		editProblemDialog.setVisible(true); // Make the dialog visible.
	}
	
	
	// Method to open the edit problem dialog and show the inputs given:
	public void openEditProblemDialog(String id, String title, String description,
			String hint, String timeLimit, String[][] variables, String solution) {
		
		// Set the text fields and text areas to the parameters:
		editProblemIDTextField.setText(id);
		editProblemTitleTextField.setText(title);
		editProblemDescriptionTextArea.setText(description);
		editProblemHintTextField.setText(hint);
		editProblemTimeLimitTextField.setText(timeLimit);
		editProblemSolutionTextArea.setText(solution);
		
		// Set the variables table's cells to those in the 2D array:
		for (int i = 0; i < variables.length; i++) {
			for (int j = 0; j < editProblemVariablesTableModel.getColumnCount(); j++) {
				editProblemVariablesTableModel.setValueAt(variables[i][j], i, j);
			}
		}
		
		// Clear any other rows:
		for (int i = variables.length; i < editProblemVariablesTableModel.getRowCount(); i++) {
			for (int j = 0; j < editProblemVariablesTableModel.getColumnCount(); j++) {
				editProblemVariablesTableModel.setValueAt("", i, j);
			}
		}
		
		editProblemDialog.setVisible(true); // Make the edit problem dialog visible.
	} 
	
	
	// Method to return the inputs in the edit problem dialog:
	public String[][] getNewProblemDetails() {
		
		// Save any cells that are being edited:
		if (editProblemVariablesTable.isEditing()) {
			editProblemVariablesTable.getCellEditor().stopCellEditing();
		}
		
		int rows = 0; // Create an integer used to count how many non-empty rows there are:
		
		// Iterate through each row:
		for (int i = 0; i < editProblemVariablesTableModel.getRowCount(); i++) {
			
			boolean isEmpty = true; // Create a boolean used to check whether the current row is empty or not.
			
			// Iterate through each cell in the current row:
			for (int j = 0; j < editProblemVariablesTableModel.getColumnCount(); j++) {
				
				// If the cell is not either null or an empty string, the row isn't empty so set isEmpty to false:
				if (!(editProblemVariablesTableModel.getValueAt(i, j) == null || editProblemVariablesTableModel.getValueAt(i, j).equals(""))) {
					isEmpty = false;
					System.out.println("."+editProblemVariablesTableModel.getValueAt(i, j)+".");
				}
			}
			
			// If the current row is empty:
			if (isEmpty) {
				
				// Return null if the first row is empty as there are missing fields:
				if (i == 0) {
					return null;
				}
				
				rows = i; // Set rows to i, as the number of non-empty rows is the number of rows until an empty one is found.
				break; // Break out of the loop so that the rest of the rows are not checked
			}
		}
		
		String[][] newProblemDetails = new String[rows + 1][4]; // Create a 2D array used to store the inputs.
		
		// Get and store all of the inputs except the variables table:
		String id = editProblemIDTextField.getText();
		String title = editProblemTitleTextField.getText();
		String description = editProblemDescriptionTextArea.getText();
		String hint = editProblemHintTextField.getText();
		String timeLimit = editProblemTimeLimitTextField.getText();
		String solution = editProblemSolutionTextArea.getText();
		
		// If all of the inputs (excluding the variables table) are not empty:
		if (!(id.equals("") || title.equals("") || description.equals("") || hint.equals("") || solution.equals(""))) {
			
			// Make the first 'row' of the 2D array store all of the inputs except the variables table:
			newProblemDetails[0] = new String[] {id, title, description, hint, timeLimit, solution};
			
			// Iterate through each cell in the table and add it to the 2D array:
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < editProblemVariablesTableModel.getColumnCount(); j++) {
					newProblemDetails[i+1][j] = (String) editProblemVariablesTableModel.getValueAt(i, j);
				}
			}
			
			return newProblemDetails; // Return the 2D array.
		}
		
		// If at least one input (excluding the variables table) are empty, return null:
		else {
			return null;
		}
	}
	
	
	// Method to show a message in a message dialog:
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(editProblemDialog, message);
	}
	
	
	// Method to close the edit problem dialog (and clear all of the input fields):
	public void closeEditProblemDialog() {
		
		// Clear the text field and text areas:
		editProblemTitleTextField.setText("");
		editProblemDescriptionTextArea.setText("");
		editProblemHintTextField.setText("");
		editProblemTimeLimitTextField.setText("");
		editProblemSolutionTextArea.setText("");
		
		// Clear the variables table:
		for (int i = 0; i < editProblemVariablesTableModel.getRowCount(); i++) {
			for (int j = 0; j < editProblemVariablesTableModel.getColumnCount(); j++) {
				editProblemVariablesTableModel.setValueAt("", i, j);
			}
		}
		
		editProblemDialog.setVisible(false); // Make the edit problem dialog invisible.
	}
	
	
	// Method to get the ID of the selected row in the problem table:
	public String getSelectedProblemID() {
		int index = problemTable.getSelectedRow(); // Get and store the index of the selected row.
		
		// If no row was selected, return null:
		if (index == -1) {
			return null;
		}
		
		// If a row was selected, return the ID in that row.
		else {
			return (String) problemTableModel.getValueAt(index, 0);
		}
	}
	
	
	// Method to get the chosen game mode:
	public int getChosenMode() {
		
		// If the efficiency mode is chosen, return 0:
		if (efficiencyModeRadioButton.isSelected()) {
			return 0;
		}
		
		// If the length mode is chosen, return 1:
		else if (lengthModeRadioButton.isSelected()) {
			return 1;
		}
		
		// If the time mode is chosen, return 2:
		else if (timeModeRadioButton.isSelected()) {
			return 2;
		}
		
		// If a random mode is chosen:
		else {
			
			// Generate and store a random number from 0, 1 or 2:
			Random random = new Random();
			int mode = random.nextInt(3);
			
			// Select the radio button of the random mode:
			switch(mode) {
				case 0:
					efficiencyModeRadioButton.setSelected(true);
					break;
				case 1:
					lengthModeRadioButton.setSelected(true);
					break;
				case 2:
					timeModeRadioButton.setSelected(true);
					break;
			}
			
			// Return the random mode's number:
			return mode;
		}
	}
	
	
	// Method to get the selected players:
	public String[][] getSelectedPlayers() {
		int[] indexes = playersTable.getSelectedRows(); // Get the indexes of each selected rows.
		
		// If no rows are selected, return null:
		if (indexes.length == 0) {
			return null;
		}
		
		// If rows are selected:
		else {
			
			// Create a 2D array to store the username and IP address of each selected player:
			String[][] selectedPlayers = new String[indexes.length][2];
			
			// Iterate through each selected row and add the username and IP address to the array:
			for (int i = 0; i < indexes.length; i++) {
				selectedPlayers[i][0] = (String) playersTableModel.getValueAt(indexes[i], 1);
				selectedPlayers[i][1] = (String) playersTableModel.getValueAt(indexes[i], 0);
			}
			
			return selectedPlayers; // Return the array.
		}
	}
	
	
	// Method to add a banned player to the banned players table:
	public void addBannedPlayer(String[] playerDetails) {
		bannedPlayersTableModel.addRow(playerDetails);
	}
	
	
	// Method to get the selected banned players:
	public String[] getSelectedBannedPlayers() {
		int[] indexes = bannedPlayersTable.getSelectedRows(); // Get the indexes of each selected rows.
		
		// If no rows are selected, return null:
		if (indexes.length == 0) {
			return null;
		}
		
		// If rows are selected:
		else {
			
			// Create a 2D array to store the IP address of each selected player:
			String[] selectedPlayers = new String[indexes.length];
			
			// Iterate through each selected row and add each IP address to the array:
			for (int i = 0; i < indexes.length; i++) {
				selectedPlayers[i] = (String) bannedPlayersTableModel.getValueAt(indexes[i], 0);
			}
			
			return selectedPlayers; // Return the array.
		}
	}
	
	
	// Method to remove a banned player from the banned players table:
	public void removeBannedPlayer(String ipAddress) {
		
		// Iterate through each row in the banned players table:
		for (int i = 0; i < bannedPlayersTableModel.getRowCount(); i++) {
			
			// If the IP address in the row matches that of the player who is being unbanned, remove the row:
			if (bannedPlayersTableModel.getValueAt(i, 0).equals(ipAddress)) {
				bannedPlayersTableModel.removeRow(i);
				break;
			}
		}
	}
}

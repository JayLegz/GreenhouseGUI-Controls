/***********************************************************************
 * COMP308 Java for Programmer,
 * SCIS, Athabasca University
 *
 *
 * Description: The GUI, for GreenhouseControls.java
 *
 * Authored/Created By: Jason A Leger
 * Student ID: 3169026
 * Date : November through Dec 6th 2023
 *
 * Special thanks to Steve for helping me understand the event time logic better
 *
 * opening Comments:  This Assignment was Long and Frusterating, and a humbling experience. This requires
 * skills in organization, planning, and logical placement of all pieces of the program
 * in order to ensure that it works throught the entire iterations of the coding development. Overall I am
 * very pleased with my final product. I really do wish we would have had an online lab (like I would have
 * had access to at a normal university) where the tutor could explain teach, or question my logic as I
 * developed this).  However, Steve L was excellent at answering my chats.
 *
 *
 * Please see TME4 Test plan to see various ways that I have confirmed that the program and GUI run as
 * intended.  At this time I am confident that this code meets all of the TME Requirements, with some additional
 * feature I decided to keep during my personal troubleshooting (copy button, Emerg Shutdown Button)
 *
 * Compile Instrs:  Please ensure tme4.GreenhouseControls.java, tme.GreenhouseGUI.java and all relevant
 * event classes are contained within the same directory. I had numerous issues during the development when
 * I had a parent and child directory system, so I made the decision to combine them into 1.
 *
 */


import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class GreenhouseGUI extends JFrame {
    private JTextArea outputArea;
    private JScrollPane scrollPane;
    private JPopupMenu popupMenu;
    private GreenhouseControls greenhouseControls;
    private AtomicInteger actionCounter = new AtomicInteger(0); //Log the GUI
    private JButton restartButton; // Flag for Restart Button
    private JButton emergencyButton; // Flag for Restart Button, Triggers Manual Serialization point
    private String selectedFilename = ""; //Needed to Restart from previous loaded list, Restart Button
                                          //This is not for Restart Operations that will be called.
    public GreenhouseGUI() { //I just made a very Generic, but nice sized GUI to accompany the buttons
        outputArea = new JTextArea(20, 20);
        outputArea.setEditable(false);
        scrollPane = new JScrollPane(outputArea);
        add(scrollPane);

        // Initialize the menu and buttons
        initializeMenu();
        initializeButtons();
        initializePopupMenu();

        addMouseListenerToComponent(this); //Added mouse listener to scroll pane
        addMouseListenerToComponent(outputArea);  //Added mouse listener to scroll pane

        // Create an instance of GreenhouseControls
        greenhouseControls = new GreenhouseControls(this);

        //System.out.println("GreenhouseControls instance in GUI: " + greenhouseControls.hashCode()); //debug

        setTitle("Greenhouse Control System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    private void initializeMenu() {  // Make the GUI Pulldown Menu, Again Generic

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem newItem = new JMenuItem("New window");
        JMenuItem closeItem = new JMenuItem("Close window");
        JMenuItem openEventsItem = new JMenuItem("Open Events");
        JMenuItem restoreItem = new JMenuItem("Restore");
        JMenuItem exitItem = new JMenuItem("Exit");

        menu.add(newItem);
        menu.add(closeItem);
        menu.add(openEventsItem);
        menu.add(restoreItem);
        menu.add(exitItem);

        // Keyboard shortcuts I made to interact with File Drop Down Menu
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)); //Cntrl N for new Window
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK)); //Cntrl W to Close Window
        openEventsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //Cntrl O to Open File
        restoreItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK)); //Cntrl R for Restore
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK)); //Cntrl Q to Exit

        // Add action listeners
        newItem.addActionListener(e -> new GreenhouseGUI());
        closeItem.addActionListener(e -> closeWindow());
        openEventsItem.addActionListener(e -> openEvents());
        restoreItem.addActionListener(e -> restore());
        exitItem.addActionListener(e -> exitApplication());

        setJMenuBar(menuBar);
    }

    private void closeWindow() {
        // Example confirmation dialog
        int result = JOptionPane.showConfirmDialog(this,
                "Do you Want to Exit this Window?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            dispose(); // Close the window
        }
    }

    private void openEvents() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedFilename = selectedFile.getPath(); // Set the selected filename

            writeToOutput("Starting Greenhouse Controls...");
            writeToOutput("Events loaded from " + selectedFile.getName()); // Notification prefix for events!

            greenhouseControls.clearEvents();
            greenhouseControls.setFilename(selectedFile.getPath());
            greenhouseControls.loadEvents(selectedFile.getPath());

            new Thread(greenhouseControls::run).start(); // For Starting Thread Process, loaded events
            restartButton.setEnabled(true);
            emergencyButton.setEnabled(true);
        }
    }
    private void restore() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Restore restore = new Restore(selectedFile.getPath());
            GreenhouseControls restoredGc = restore.restoreSystem(this);

            if (restoredGc != null) {
                restoredGc.reinitializeTransientFields(this); // Pass the GreenhouseGUI instance
                this.greenhouseControls = restoredGc;

                // Restart event processing happens here!
                new Thread(this.greenhouseControls::run).start();

                // Display the recovery message to GUI for Users
                String recoveryMessage = String.format("System Recovery Successful From %s: ", selectedFile.getName());
                writeToOutput(recoveryMessage);
                writeToOutput("Resuming Operations..." + "\n");


                restartButton.setEnabled(false);//Disables the Restart Button for Restore Events
            }
        }
    }
    private void exitApplication() {
        // Example confirmation dialog
        int result = JOptionPane.showConfirmDialog(this,
                "Are there any running processes that need to be saved before exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0); // Exit the application, including Console
        }
    }

    //Create the GUI Buttons
    private void initializeButtons() {
        JButton startButton = new JButton("Start");
        restartButton = new JButton("Restart");
        JButton terminateButton = new JButton("Terminate");
        JButton suspendButton = new JButton("Suspend");
        JButton resumeButton = new JButton("Resume");
        JButton copyButton = new JButton("Copy");
        emergencyButton = new JButton("Emergency Shutdown");

        restartButton.setEnabled(false); // Initially disable restart button
        emergencyButton.setEnabled(false); //Initially disable Emergency Reset


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(terminateButton);
        buttonPanel.add(suspendButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(copyButton); //Added a copy GUI button for debugging
                                     // (amazing feature for troubleshooting and comparing Console Output)

        // Making the Emergency shutdown Button Different in Layout to prevent accidental Emerg Shutdown
        JPanel emergencyButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        emergencyButtonPanel.add(emergencyButton);

        //The main buttons on the bottom of the GUI
        add(buttonPanel, BorderLayout.SOUTH);

        // Emergency shutdown button on top Right of GUI
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(emergencyButtonPanel, BorderLayout.EAST); // Add to the right side of the top panel
        add(topPanel, BorderLayout.NORTH);

        // Add action listeners for buttons
        startButton.addActionListener(e -> startGreenhouseControls()); //Pick the File!
        restartButton.addActionListener(e ->  restartFromSelectedFile()); //Restart From previous File
        terminateButton.addActionListener(e -> terminateGreenhouseControls()); //End All Operations and Clear
        suspendButton.addActionListener(e -> suspendGreenhouseControls()); // Pause Operations
        resumeButton.addActionListener(e -> resumeGreenhouseControls()); // Resume from the Pause
        copyButton.addActionListener(e -> copyAllTextToClipboard()); //Copies Everything in the output area to clipboard
        emergencyButton.addActionListener(e -> manuallyTriggerSerialization()); //Emergency Shutdown (serialize)
    }

    private void copyAllTextToClipboard() { //Bonus Feature I added to compare numerous test outputs
        outputArea.selectAll(); // Select all text in outputArea
        String selectedText = outputArea.getSelectedText();
        if (selectedText != null) {
            StringSelection stringSelection = new StringSelection(selectedText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }
    private void manuallyTriggerSerialization() { //For the Emergency Shutdown Button
        writeToOutput("Emergency Shutdown Protocol Initiated...");
        greenhouseControls.shutdown("Emergency Shutdown Button was Pressed");
        emergencyButton.setEnabled(false);
    }
    private void initializePopupMenu() {  //The Popup Menu
        popupMenu = new JPopupMenu();

        JMenuItem startItem = new JMenuItem("Start");
        startItem.addActionListener(e -> startGreenhouseControls());
        popupMenu.add(startItem);

        JMenuItem restartItem = new JMenuItem("Restart");
        restartItem.addActionListener(e -> restartGreenhouseControls());
        popupMenu.add(restartItem);

        JMenuItem terminateItem = new JMenuItem("Terminate");
        terminateItem.addActionListener(e -> terminateGreenhouseControls());
        popupMenu.add(terminateItem);

        JMenuItem suspendItem = new JMenuItem("Suspend");
        suspendItem.addActionListener(e -> suspendGreenhouseControls());
        popupMenu.add(suspendItem);

        JMenuItem resumeItem = new JMenuItem("Resume");
        resumeItem.addActionListener(e -> resumeGreenhouseControls());
        popupMenu.add(resumeItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(e -> copyAllTextToClipboard());
        popupMenu.add(copyItem);

    }
    private void addMouseListenerToComponent(Component component) { //Mouse Listeners Method!
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
            }
            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e);
            }
            private void showPopupMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void startGreenhouseControls() {  // Start GreenhouseControls operations
        writeToOutput("Starting Greenhouse Controls...");
        writeToOutput("Please Enter the Filename you wish to Open in the Pop-Up Box...");
        String filenameInput = JOptionPane.showInputDialog(this, "Enter event file name:");
        if (filenameInput != null && !filenameInput.isEmpty()) {

            String basePath = "C:\\Users\\leger\\Desktop\\TME4_Leger\\tme4new\\"; //if you type the filename
            // it selects from here.... please edit if you use this on your own Setup!!

            String effectiveFilename = filenameInput;

            if (!new File(filenameInput).isAbsolute()) {
                effectiveFilename = basePath + filenameInput;
            }
            final String filename = effectiveFilename; //Store this as Variable
            selectedFilename = filename; // Set the selected filename

            writeToOutput("Greenhouse Controls started with file: " + filename);

            greenhouseControls.setFilename(filename);
            greenhouseControls.clearEvents(); // Clears any existing events first.
            greenhouseControls.loadEvents(filename); // Load the events as the menu action does.

            new Thread(greenhouseControls::run).start(); // Run the loaded events.

            restartButton.setEnabled(true);// Enables the Restart Button Now
            emergencyButton.setEnabled(true); // Enable the button when events start

        } else {
            writeToOutput("No file selected. Operation not started."); //if you cancel the start button
        }
    }
    private void restartGreenhouseControls() {  // Restart GreenhouseControls operations
        writeToOutput("Restarting Greenhouse Controls From Previous Loaded File...");
        greenhouseControls.clearEvents(); // Clear existing events
        new Thread(() -> {
            greenhouseControls.restartOperations(); // Reload and start the events in a new thread
            SwingUtilities.invokeLater(() -> writeToOutput("Greenhouse Controls restart Complete."));
        }).start();
    }
    private void terminateGreenhouseControls() {  // Terminate the GreenhouseControls operations
        // Add logic to terminate greenhouse operations
        writeToOutput("Terminating and Ceasing Greenhouse Controls...");
        writeToOutput("Please Enter a Greenhouse Configuration File For Operations");
        greenhouseControls.terminateOperations();
        restartButton.setEnabled(false);//Enable the Restart Button Now
    }
    private void suspendGreenhouseControls() {  // Suspend the GreenhouseControls operations
        // Add logic to suspend greenhouse operations
        writeToOutput("Suspending Greenhouse Controls...");
        greenhouseControls.suspendOperations();
    }
    private void resumeGreenhouseControls() {  // Resume the GreenhouseControls operations
        // Add logic to resume greenhouse operations
        writeToOutput("Resuming Greenhouse Controls...");
        greenhouseControls.resumeOperations();
    }

    public void shutdownGreenhouseControls(String message) {
        writeToOutput("Shutting down Greenhouse Controls due to " + message);
        greenhouseControls.shutdown(message);
        emergencyButton.setEnabled(false);
    }

    private void restartFromSelectedFile() {
        if (!selectedFilename.isEmpty()) {
            writeToOutput("Restarting Greenhouse Controls from Selected File: " + selectedFilename);

            greenhouseControls.setFilename(selectedFilename);
            greenhouseControls.clearEvents(); // Clear existing events.
            greenhouseControls.loadEvents(selectedFilename); // Load events from the selected file.

            new Thread(greenhouseControls::run).start(); // Start the GreenhouseControls with the loaded events.

            restartButton.setEnabled(true); // Enable the Restart Button.
            emergencyButton.setEnabled(true); // Enable the button when events start.
        } else {
            writeToOutput("No selected file found. Cannot restart.");
        }
    }

    public void writeToOutput(String text) {
        int serialNumber = actionCounter.incrementAndGet();
        String formattedText = String.format("Ser. %d - %s", serialNumber, text);

        // Check if the message is a shutdown message
        boolean isShutdownMessage = text.contains("Shutting down Greenhouse Controls");

        // Print to the console
        System.out.println(formattedText); //Copy of the events that populate in the GUI
                                           //I used this as a debug feature... but its nice to keep backend

        // Appends the GUI with an extra newline if it's a shutdown message
        SwingUtilities.invokeLater(() -> {
            synchronized (outputArea) {
                if (isShutdownMessage) {
                    outputArea.append(formattedText + "\n\n"); // Add extra newline
                } else {
                    outputArea.append(formattedText + "\n");
                }
            }
        });
    }


//******************************************************************************************//

    public static void main(String[] args) { //Simple Main Method to Operate GUI and Console
        SwingUtilities.invokeLater(() -> new GreenhouseGUI());
    }
}

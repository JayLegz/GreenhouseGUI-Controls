//: innerclasses/tme4.GreenhouseControls.java
// This produces a specific application of the
// control system, all in a single class. Inner
// classes allow you to encapsulate different
// functionality for each type of event.
// From 'Thinking in Java, 4th ed.' (c) Bruce Eckel 2005
// www.BruceEckel.com. See copyright notice in CopyRight.txt.

/***********************************************************************
 * Adapated for COMP308 Java for Programmer, 
 *		SCIS, Athabasca University
 *
 * Assignment: TME3
 * @author: Steve Leung
 * @date: Oct 21, 2005
 *
 * Edited By: Jason A Leger
 * Student ID: 3169026
 * Date : November through Dec 6th 2023
 *
 * Special thanks to Steve for helping me understand the event time logic better
 *
 * opening Comments:  This Assignment was Long and Frusterating, It was a good humbling experience
 * regarding organization, planning, and ensuring all pieces of the program worked during all interations
 * of the coding development. Overall I am very pleased with my final product. I really do wish we would have
 * had an online lab (like I would have had access to at a normal university) where the tutor could explain
 * teach, or question my logic as I developed this).  However, Steve L was excellent at answering my chats.
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

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GreenhouseControls implements Serializable {

    // All Variables
    private Map<String, TwoTuple<String, Object>> stateVariables;
    private transient GreenhouseGUI gui;
    private String filename;
    private volatile boolean isTerminated = false;

    //Event Lists and Thread Management

    private transient ArrayList<Event> gcEvents = new ArrayList<>();
    private transient ArrayList<Thread> gcThreads = new ArrayList<>();

    //All the Methods used for GC

    public GreenhouseControls(GreenhouseGUI gui) { //Part1 Step5
        this.gui = gui;  //Part1 Step5
        initializeStateVariables();
    }

    private void initializeStateVariables() {
        stateVariables = new HashMap<>();
        // Initialize default states
        stateVariables.put("Light", new TwoTuple<>("Light", false));
        stateVariables.put("Water", new TwoTuple<>("Water", false));
        stateVariables.put("Fans", new TwoTuple<>("Fans", false));
        stateVariables.put("WindowOk", new TwoTuple<>("WindowOk", true));
        stateVariables.put("PowerOn", new TwoTuple<>("PowerOn", true));
        stateVariables.put("Thermostat", new TwoTuple<>("Thermostat", "Day"));
        stateVariables.put("BellRings", new TwoTuple<>("BellRings", 1));
        stateVariables.put("ErrorCode", new TwoTuple<>("ErrorCode", 0));
    }

    public synchronized void addEvent(Event e) {
        e.setGui(gui);
        gcEvents.add(e);
        threadEvent(e);
    }

    private void threadEvent(Event e) {
        Thread t = new Thread(e);
        gcThreads.add(t);
        t.start();
    }

    public void loadEvents(String filename) { //Loading Events from File similar fashioned to TME3
        System.out.println("Loading events from file: " + filename); //Debug
        try {
            Scanner sc = new Scanner(new File(filename));
            Pattern eventPattern = Pattern.compile("Event=([\\w\\.]+),time=(\\d+)(,rings=(\\d+))?");

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Matcher matcher = eventPattern.matcher(line);
                if (matcher.find()) {
                    String eventType = matcher.group(1);
                    //System.out.println("Parsed event: " + eventType); // Log each parsed event

                    long time = Long.parseLong(matcher.group(2));
                    String ringsString = matcher.group(4);
                    int rings = (ringsString != null) ? Integer.parseInt(ringsString) : 0;

                    //System.out.println("Parsed event: " + eventType + ", time: " + time + ", rings: " + rings); //Debug Statement
                    //System.out.println("Matched Event: " + eventType + ", Time: " + time + ", Rings: " + rings); //Debug Statement
                    //System.out.println("GreenhouseControls instance in loadEvents: " + this.hashCode()); //Debug Statements

                    switch (eventType) {
                        case "ThermostatNight":
                            addEvent(new ThermostatNight(time, this));
                            break;
                        case "ThermostatDay":
                            addEvent(new ThermostatDay(time, this));
                            break;
                        case "LightOn":
                            addEvent(new LightOn(time, this));
                            //System.out.println("Added LightOn event"); //debug
                            break;
                        case "LightOff":
                            addEvent(new LightOff(time, this));
                            break;
                        case "FansOn":
                            addEvent(new FansOn(time, this));
                            break;
                        case "FansOff":
                            addEvent(new FansOff(time, this));
                            break;
                        case "WaterOn":
                            addEvent(new WaterOn(time, this));
                            break;
                        case "WaterOff":
                            addEvent(new WaterOff(time, this));
                            break;
                        case "Bell":
                            if (rings > 0) {
                                addEvent(new Bell(time, rings, this));
                            }
                            break;
                        case "WindowMalfunction":
                            addEvent(new WindowMalfunction(time, this));
                            break;
                        case "PowerOut":
                            addEvent(new PowerOut(time, this));
                            break;
                        case "Terminate":
                            addEvent(new Terminate(time,this));
                            break;
                        default:
                            System.err.println("Unrecognized event: " + eventType);
                            break;
                    }
                    //System.out.println("Loaded Event: " + eventType + ", Time: " + time); //Debug Statement
                }
            }
            sc.close();
            System.out.println("Total events loaded: " + gcEvents.size());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
            gui.writeToOutput("File not found: " + filename); // Display to the GUI error
        }
    }
    public void run() {
        while (!isTerminated && !Thread.currentThread().isInterrupted()) {
            List<Event> eventsCopy;
            synchronized (this) {
                eventsCopy = new ArrayList<>(gcEvents); // Create a copy within synchronized block
            }
            for (Event e : eventsCopy) {
                if (e.ready()) {
                    try {
                        //e.action();  //Found this caused duplicate start and restart button outputs in GUI
                        synchronized (this) {
                            gcEvents.remove(e); // Synchronize the removal of the event
                        }
                    } catch (Exception ex) {
                        handleException(ex);
                    }
                }
            }
        }
    }

    private void handleException(Exception ex) {
        System.err.println("An Emergency Shutdown in process due to: " + ex.getMessage());
        gui.writeToOutput("Error: " + ex.getMessage());
        shutdown(ex.getMessage());
    }

    protected synchronized void shutdown(String message) {

        // Mark the system as terminated to prevent further event processing
        isTerminated = true;

        // Interrupt all running threads
        for (Thread thread : gcThreads) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        }

        System.out.println("Events before serialization:");
        for (Event event : this.getEventList()) {
            System.out.println("Event " + event + " remaining time: " + event.getRemainingTime());
        }

        // Fetch error code from stateVariables map
        Object errorCodeObj = getVariable("ErrorCode");
        int errorCode = errorCodeObj != null ? (Integer) errorCodeObj : 0;

        try {
            // Logging to the error log
            try (PrintWriter log = new PrintWriter(new FileWriter("error_log.txt", true))) {
                log.println("Time: " + new Date());
                log.println("Error code: " + errorCode);
                log.println("Message: " + message);
                log.println(); // Added extra line between logs
            }

            // Serialization of the current state
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dump_out.txt"))) {
                out.writeObject(this); // Serialize the entire GreenhouseControls object
            }
            System.out.println("Shutdown Due to Error code: " + errorCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Clear the event and thread lists
        gcEvents.clear();
        gcThreads.clear();

        // Reinitialize the lists
        gcEvents = new ArrayList<>();
        gcThreads = new ArrayList<>();
    }

    public void setGui(GreenhouseGUI gui) {
        this.gui = gui;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public synchronized void setVariable(String key, Object value) { // Updates the state variables
        TwoTuple<String, Object> variable = stateVariables.get(key);
        if (variable != null) {
            stateVariables.put(key, new TwoTuple<>(variable.first, value));
        }
    }

    public Object getVariable(String key) {  // Get state variable
        TwoTuple<String, Object> variable = stateVariables.get(key);
        return (variable != null) ? variable.second : null;
    }

    public List<Event> getEventList() {
        if (gcEvents == null) {
            gcEvents = new ArrayList<>();
        }
        return gcEvents;
    }

    public void removeEvent(Event event) {
        gcEvents.remove(event);
    }

    public int getError() { //Added Step4 Part3
        Object errorCode = getVariable("ErrorCode");
        return (errorCode != null) ? (Integer) errorCode : 0;
    }

    public Fixable getFixable(int errorcode, GreenhouseGUI gui) {
        switch (errorcode) {
            case 1:
                return new FixWindow(this, gui);
            case 2:
                return new PowerOn(this, gui);
            default:
                return null;
        }
    }
    public void restartOperations() {
        long currentTime = System.currentTimeMillis();

        for (Thread thread : gcThreads) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        }
        gcThreads.clear();

        // Restart each event with the new timing based on the serialized remaining time
        for (Event event : gcEvents) {
            long remainingTime = event.getRemainingTime(); // Get remaining time from serialization
            long newStartTime = currentTime + Math.max(remainingTime, 0); // Calculate new start time
            event.start(newStartTime); // Set new start time for the event using the existing method
            threadEvent(event);
        }
    }
    public void terminateOperations() {  // Terminate greenhouse operations
        for (Thread thread : gcThreads) {
            thread.interrupt(); // Interrupt all running threads
        }
        gcEvents.clear(); // Clear pending events
        gcThreads.clear(); // Clear the thread list
    }
    public void suspendOperations() {  // Suspend (pause) greenhouse operations
        for (Event event : gcEvents) {
            event.suspend();
        }
    }
    public void resumeOperations() {  // Resume greenhouse operations
        for (Event event : gcEvents) {
            event.resume();
        }
    }

    public void clearEvents() {
        gcEvents.clear();
        gcThreads.clear();
    }

    public void reinitializeTransientFields(GreenhouseGUI gui) {
        if (gcThreads == null) {
            gcThreads = new ArrayList<>();
        }
    }

    public String generateSystemStatusReport() { //added Step4 Part5, Ammended for TME4, Excellent for Debugging
        StringBuilder report = new StringBuilder("Current System Status After Restoration:\n");

        Object fansStatus = getVariable("Fans");
        Object waterStatus = getVariable("Water");
        Object thermostatStatus = getVariable("Thermostat");
        Object powerStatus = getVariable("PowerOn");
        Object windowStatus = getVariable("WindowOk");

        report.append("Fans: ").append(fansStatus != null && (Boolean) fansStatus ? "On" : "Off").append("\n");
        report.append("Water: ").append(waterStatus != null && (Boolean) waterStatus ? "On" : "Off").append("\n");
        report.append("Thermostat settings: ").append(thermostatStatus != null ? thermostatStatus : "Unknown").append("\n");
        report.append("Power Status: ").append(powerStatus != null && (Boolean) powerStatus ? "On" : "Off").append("\n");
        report.append("Windows Status: ").append(windowStatus != null && (Boolean) windowStatus ? "Good" : "Malfunctioned").append("\n");

        if (gui != null) {
            gui.writeToOutput(report.toString());
        }
        return report.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException { //Serialize Function
        out.defaultWriteObject();
        out.writeInt(gcEvents.size());
        for (Event event : gcEvents) {
            out.writeObject(event);
            out.writeLong(event.getRemainingTime()); // Serialize the adjusted time
        }
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException { //Deserialize function
        in.defaultReadObject();
        int size = in.readInt();
        gcEvents = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Event event = (Event) in.readObject();
            long remainingTime = in.readLong(); // Deserialize the remaining time
            event.start(System.currentTimeMillis() + remainingTime);
            gcEvents.add(event);
            System.out.println("Deserialized event: " + event + ", New Delay Time: " + event.getRemainingTime());
        }
    }
}








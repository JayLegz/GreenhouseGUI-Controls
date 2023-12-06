 /***********************************************************************
 * Adapated for COMP308 Java for Programmer,
 *		SCIS, Athabasca University
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

 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;

 public class Restore {

     private String filename;

     public Restore(String filename) {
         this.filename = filename;
     }

     public GreenhouseControls restoreSystem(GreenhouseGUI gui) {
         GreenhouseControls gc = null;

         try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
             gc = (GreenhouseControls) in.readObject();
         } catch (IOException | ClassNotFoundException e) {
             System.out.println("Error reading from file: " + filename);
             e.printStackTrace();
         }

         if (gc != null) {
             gc.setGui(gui);  // Set the correct GUI instance
             gc.reinitializeTransientFields(gui); // Reinitialize transient

             // Reinitialize fields for each Event
             for (Event event : gc.getEventList()) {
                 event.setGui(gui);
             }

             // Print the state of the saved system
             System.out.println("Attempting to recover from " + filename);
             System.out.println("Current time: " + System.currentTimeMillis());

             // Print events after deserialization
             System.out.println("Events after deserialization:");
             for (Event event : gc.getEventList()) {
                 System.out.println(event);
             }

             System.out.println(gc.generateSystemStatusReport()); // Print System State

             Fixable fixable = gc.getFixable(gc.getError(), gui); //Updated to add fix log to the GUI

             if (fixable != null) {
                 fixable.fix();
                 fixable.log();
             }

             // Restart event operations
             gc.restartOperations();

             System.out.println("Time at restore: " + System.currentTimeMillis()); // Print Time at Restore
         }

         return gc;
     }
 }

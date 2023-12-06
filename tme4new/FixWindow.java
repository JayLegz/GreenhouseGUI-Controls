import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class FixWindow implements Fixable {
    private GreenhouseControls greenhouseControls;
    private GreenhouseGUI gui;

    public FixWindow(GreenhouseControls greenhouseControls, GreenhouseGUI gui) {
        this.greenhouseControls = greenhouseControls;
        this.gui = gui;
    }

    @Override
    public void fix() {
        greenhouseControls.setVariable("WindowOk", true); //SetVar Method from GreenhouseControls
        greenhouseControls.setVariable("ErrorCode", 0);  //SetVar Method from GreenhouseControls
    }

    @Override
    public void log() {
        try {
            FileWriter fw = new FileWriter("fix_log.txt", true);
            PrintWriter pw = new PrintWriter(fw);
            Date currentTime = new Date();
            pw.println("Timestamp: " + currentTime);
            pw.println("Nature of Fix: Window Fixed, Error Codes Reset");
            pw.println(); // Add a blank line for separation
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fixMessage = "Fix applied: Window Fixed";
        String resetMessage = "Error Code has been Reset to 0";
        String resumeMessage = "Resuming Recovered operations";

        // From TME3, Back End Logging
        System.out.println(fixMessage);
        System.out.println(resetMessage);
        System.out.println(resumeMessage);

        // Send fix messages to GUI, Front End Addition
        gui.writeToOutput(fixMessage);
        gui.writeToOutput(resetMessage);
        gui.writeToOutput(resumeMessage);
    }

    @Override
    public String toString() {
        return "Window Fixed";
    }
}
